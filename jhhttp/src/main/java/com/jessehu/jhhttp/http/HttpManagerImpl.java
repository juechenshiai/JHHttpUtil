package com.jessehu.jhhttp.http;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import com.alibaba.fastjson.JSON;
import com.jessehu.jhhttp.JH;
import com.jessehu.jhhttp.http.callback.ProgressCallback;
import com.jessehu.jhhttp.http.parameter.DownloadParams;
import com.jessehu.jhhttp.http.parameter.RequestParams;
import com.jessehu.jhhttp.http.progress.ProgressRequestBody;
import com.jessehu.jhhttp.http.progress.ProgressResponseBody;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * HTTP请求实现
 *
 * @author JesseHu
 * @date 2018/11/28
 */
public class HttpManagerImpl implements HttpManager {
    private static final Object LOCK = new Object();
    private static volatile HttpManagerImpl instance;
    private static final String TYPE_HTTP = "http";
    private static final String TYPE_HTTPS = "https";
    private static final int METHOD_GET = 1;
    private static final int METHOD_POST = 2;
    private static final int METHOD_UPLOAD = 3;
    private static final int METHOD_DOWNLOAD = 4;
    private static final String FILENAME_SEQUENCE_SEPARATOR = "-";
    private static final String SUFFIX_TEMP = ".temp";

    public static void registerInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new HttpManagerImpl();
            }
        }
        JH.setHttpManager(instance);
    }

    @Override
    public void get(String url, Callback callback) {
        RequestParams requestParams = new RequestParams(url);
        get(requestParams, callback);
    }

    @Override
    public void get(RequestParams requestParams, Callback callback) {
        Call call = getRequestCall(requestParams, METHOD_GET, null);
        call.enqueue(callback);
    }

    @Override
    public Response getSync(String url) throws IOException {
        RequestParams requestParams = new RequestParams(url);
        return getSync(requestParams);
    }

    @Override
    public Response getSync(RequestParams requestParams) throws IOException {
        Call call = getRequestCall(requestParams, METHOD_GET, null);
        return call.execute();
    }

    @Override
    public void post(String url, Callback callback) {
        RequestParams requestParams = new RequestParams(url);
        post(requestParams, callback);
    }

    @Override
    public void post(RequestParams requestParams, Callback callback) {
        Call call = getRequestCall(requestParams, METHOD_POST, null);
        call.enqueue(callback);
    }

    @Override
    public Response postSync(String url) throws IOException {
        RequestParams requestParams = new RequestParams(url);
        return postSync(requestParams);
    }

    @Override
    public Response postSync(RequestParams requestParams) throws IOException {
        Call call = getRequestCall(requestParams, METHOD_POST, null);
        return call.execute();
    }

    @Override
    public void upload(String url, String key, String filePath, Callback callback) {
        RequestParams requestParams = new RequestParams(url);
        requestParams.addFile(key, filePath);
        upload(requestParams, callback);
    }

    @Override
    public void upload(RequestParams requestParams, Callback callback) {
        ProgressCallback progressCallback = null;
        if (callback instanceof ProgressCallback) {
            progressCallback = (ProgressCallback) callback;
        }
        Call call = getRequestCall(requestParams, METHOD_UPLOAD, progressCallback);
        call.enqueue(callback);
    }

    @Override
    public void download(String url, Callback callback) {
        RequestParams requestParams = new RequestParams(url);
        download(requestParams, callback);
    }

    @Override
    public void download(RequestParams requestParams, Callback callback) {
        if (callback instanceof ProgressCallback) {
            ProgressCallback progressCallback = (ProgressCallback) callback;
            progressCallback.onStarted();
            downloadFile(requestParams, progressCallback);
        } else {
            Call call = getRequestCall(requestParams, METHOD_DOWNLOAD, null);
            call.enqueue(callback);
        }
    }

    /**
     * 下载
     *
     * @param requestParams    请求参数
     * @param progressCallback 请求回调
     */
    private void downloadFile(final RequestParams requestParams, final ProgressCallback progressCallback) {
        String filePath = requestParams.getDownloadParams().getDownloadPath();
        String filename = requestParams.getDownloadParams().getDownloadFilename();
        if (filePath != null) {
            File file = new File(filePath);
            // 路径不为null的情况下，如果路径是文件则判断是否有两个文件名并判断文件名是否相等
            if (!file.isDirectory()) {
                filePath = file.getParent();
                String filename2 = file.getName();
                if (filename != null && !filename2.equals(filename)) {
                    throw new RuntimeException("There are two different filenames");
                } else {
                    filename = filename2;
                }
            }
        } else {
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        }
        Call call = getRequestCall(requestParams, METHOD_DOWNLOAD, progressCallback);
        final String finalFilePath = filePath;
        final String finalFilename = filename;
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressCallback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressCallback.onResponse(call, response);
                String name = finalFilename;
                if (name == null) {
                    // 获取连接中的文件名，response中可以获取到真实直链，request只能获取重定向之前的链接，如果不是直链将无法获取直链
                    String url = response.request().url().toString();
                    String mimeType = MimeTypeMap.getFileExtensionFromUrl(url);
                    name = URLUtil.guessFileName(url, null, mimeType);
                }
                File file = new File(finalFilePath, name);
                long startPoint = getStartPoint(response, requestParams);
                boolean downloadCover = requestParams.getDownloadParams().isDownloadCover();
                boolean downloadTempCover = requestParams.getDownloadParams().isDownloadTempCover();
                saveFile(call, response, chooseUniqueTempFile(file, downloadCover, downloadTempCover), startPoint, progressCallback);
            }
        });

    }

    /**
     * 保存下载的文件
     *
     * @param call             Call
     * @param response         Response
     * @param tempFile         下载换成文件
     * @param startPoint       断点续传起始点
     * @param progressCallback 进度回调
     */
    private void saveFile(Call call, Response response, File tempFile, long startPoint, ProgressCallback progressCallback) {
        InputStream is = null;
        RandomAccessFile randomAccessFile = null;
        BufferedInputStream bis = null;
        byte[] buff = new byte[2048];
        int len;
        try {
            is = response.body().byteStream();
            bis = new BufferedInputStream(is);

            File parentFile = tempFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            // 随机访问文件，可以指定断点续传的起始位置
            randomAccessFile = new RandomAccessFile(tempFile, "rwd");
            randomAccessFile.seek(startPoint);
            while ((len = bis.read(buff)) != -1) {
                randomAccessFile.write(buff, 0, len);
            }

            String absolutePath = tempFile.getAbsolutePath();
            tempFile.renameTo(new File(absolutePath.substring(0, absolutePath.length() - SUFFIX_TEMP.length())));

            // 下载完成
            progressCallback.onFinished();
        } catch (IOException e) {
            progressCallback.onFailure(call, e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                progressCallback.onFailure(call, e);
            }
        }
    }

    /**
     * 判断参数类型<br/>
     * 只支持String和Number<br/>
     * 如果是null，默认将转换成空字符串"",
     *
     * @param value 参数
     * @return true:支持 false:不支持
     */
    private boolean typeJudgment(Object value) {
        return value == null || value instanceof String || value instanceof Number || value instanceof File;
    }

    /**
     * 获取文件MimeType
     *
     * @param filename 文件名
     * @return 文件的MimeType
     */
    private static String getMimeType(String filename) {
        FileNameMap filenameMap = URLConnection.getFileNameMap();
        String contentType = filenameMap.getContentTypeFor(filename);
        if (contentType == null) {
            //所有不能识别的的文件全部使用文件流传输
            contentType = "application/octet-stream";
        }
        return contentType;
    }

    /**
     * 添加请求头和参数等到请求中
     *
     * @param requestParams 参数
     * @param method        请求方式
     * @return request call
     */
    @NonNull
    private Call getRequestCall(RequestParams requestParams, int method, ProgressCallback progressCallback) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder();
        Request.Builder requestBuilder = new Request.Builder();
        String url = requestParams.getUrl();
        if (url == null) {
            throw new RuntimeException("Url is empty");
        }
        requestBuilder.url(url);

        addHeaders(requestParams, requestBuilder, method);
        addClientParams(requestParams, clientBuilder);

        switch (method) {
            case METHOD_GET:
                doGet(requestParams, requestBuilder);
                break;
            case METHOD_POST:
                doPost(requestParams, requestBuilder);
                break;
            case METHOD_UPLOAD:
                doUpload(requestParams, requestBuilder, progressCallback);
                break;
            case METHOD_DOWNLOAD:
                doDownload(requestParams, clientBuilder, progressCallback);
                break;
            default:
        }

        Request request = requestBuilder.build();
        return clientBuilder.build().newCall(request);
    }


    /**
     * 添加Client参数
     *
     * @param requestParams 请求参数
     * @param clientBuilder OkHttpClient.Builder
     */
    private void addClientParams(RequestParams requestParams, OkHttpClient.Builder clientBuilder) {
        clientBuilder.connectTimeout(requestParams.getConnectTimeout(), TimeUnit.MILLISECONDS);
        clientBuilder.writeTimeout(requestParams.getWriteTimeout(), TimeUnit.MILLISECONDS);
        clientBuilder.writeTimeout(requestParams.getWriteTimeout(), TimeUnit.MILLISECONDS);

        if (requestParams.getUrl().startsWith(TYPE_HTTPS)) {
            HostnameVerifier hostnameVerifier = requestParams.getHostnameVerifier();
            SSLSocketFactory sslSocketFactory = requestParams.getSslSocketFactory();
            X509TrustManager x509TrustManager = requestParams.getX509TrustManager();
            if (hostnameVerifier == null || sslSocketFactory == null || x509TrustManager == null) {
                throw new RuntimeException("Https request need to add the certificate and verify the hostname");
            }
            clientBuilder.hostnameVerifier(hostnameVerifier);
            clientBuilder.sslSocketFactory(sslSocketFactory, x509TrustManager);
        }
    }

    /**
     * 添加请求头
     *
     * @param requestParams  请求参数
     * @param requestBuilder Request.Builder
     */
    private void addHeaders(RequestParams requestParams, Request.Builder requestBuilder, int method) {
        if (method == METHOD_DOWNLOAD && requestParams.getDownloadParams() != null) {
            String startPointKey = requestParams.getDownloadParams().getStartPointKey();
            if (startPointKey != null) {
                String startPointValue = requestParams.getDownloadParams().getStartPointValue() + "";
                requestBuilder.addHeader(startPointKey, startPointValue);
            }
        }
        for (String key : requestParams.getHeaders().keySet()) {
            Object value = requestParams.getHeaders().get(key);
            if (typeJudgment(value)) {
                requestBuilder.addHeader(key, value == null ? "" : value.toString());
            } else {
                throw new RuntimeException("Header types are not supported");
            }
        }
    }

    /**
     * GET请求添加参数
     *
     * @param requestParams  请求参数
     * @param requestBuilder Request.Builder
     */
    private void doGet(RequestParams requestParams, Request.Builder requestBuilder) {
        HttpUrl httpUrl = HttpUrl.parse(requestParams.getUrl());
        HttpUrl.Builder httpUrlBuilder = httpUrl.newBuilder();
        for (String key : requestParams.getBodyParams().keySet()) {
            Object value = requestParams.getBodyParams().get(key);
            if (typeJudgment(value)) {
                httpUrlBuilder.addQueryParameter(key, value == null ? "" : value.toString());
            } else {
                throw new RuntimeException("Parameter types are not supported");
            }
        }
        requestBuilder.url(httpUrlBuilder.build());
        requestBuilder.get();
    }

    /**
     * POST请求添加参数
     *
     * @param requestParams  请求参数
     * @param requestBuilder Request.Builder
     */
    private void doPost(RequestParams requestParams, Request.Builder requestBuilder) {
        if (requestParams.isAsJsonContent()) {
            String jsonString = requestParams.getJsonString();
            if (jsonString == null) {
                jsonString = JSON.toJSONString(requestParams.getBodyParams());
            }
            RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
            requestBuilder.post(requestBody);
        } else {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for (String key : requestParams.getBodyParams().keySet()) {
                Object value = requestParams.getBodyParams().get(key);
                if (typeJudgment(value)) {
                    formBodyBuilder.add(key, value == null ? "" : value.toString());
                } else {
                    throw new RuntimeException("Parameter types are not supported");
                }
            }
            requestBuilder.post(formBodyBuilder.build());
        }

    }

    /**
     * 文件上传添加参数
     *
     * @param requestParams  请求参数
     * @param requestBuilder Request.Builder
     */
    private void doUpload(RequestParams requestParams, Request.Builder requestBuilder, ProgressCallback progressCallback) {
        MultipartBody.Builder multiBodyBuilder = new MultipartBody.Builder();
        multiBodyBuilder.setType(MultipartBody.FORM);
        for (String key : requestParams.getBodyParams().keySet()) {
            Object value = requestParams.getBodyParams().get(key);
            if (typeJudgment(value)) {
                if (value instanceof File) {
                    // 添加文件
                    File file = (File) value;
                    String fileName = file.getName();
                    String mimeType = getMimeType(fileName);
                    RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), file);
                    if (progressCallback != null) {
                        requestBody = new ProgressRequestBody(requestBody, progressCallback);
                    }
                    multiBodyBuilder.addFormDataPart(key, fileName, requestBody);
                    multiBodyBuilder.addFormDataPart("JHFileName", fileName);
                } else {
                    multiBodyBuilder.addFormDataPart(key, value == null ? "" : value.toString());
                }

            } else {
                throw new RuntimeException("Parameter types are not supported");
            }
        }
        requestBuilder.post(multiBodyBuilder.build());
    }

    /**
     * 设置文件下载拦截器
     *
     * @param requestParams    请求参数
     * @param clientBuilder    OkHttpClient.Builder
     * @param progressCallback 进度回调
     */
    private void doDownload(final RequestParams requestParams, OkHttpClient.Builder clientBuilder, final ProgressCallback progressCallback) {
        if (progressCallback != null) {
            // 重写ResponseBody监听请求
            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    long startPoint = getStartPoint(originalResponse, requestParams);
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), startPoint, progressCallback))
                            .build();
                }
            };
            clientBuilder.addInterceptor(interceptor);
        }
    }

    /**
     * 获取下载断点(起始点)
     *
     * @param response      Response
     * @param requestParams 请求参数
     * @return 下载断点
     */
    private long getStartPoint(Response response, RequestParams requestParams) {
        DownloadParams downloadParams = requestParams.getDownloadParams();
        long startPoint = 0L;
        if (downloadParams != null) {
            String startPointKey = downloadParams.getStartPointKey();
            String responseStartPointKey = downloadParams.getResponseStartPointKey();
            if (responseStartPointKey == null) {
                responseStartPointKey = startPointKey;
            }
            if (responseStartPointKey != null) {
                String startPointStr = response.header(responseStartPointKey);
                startPointStr = startPointStr == null ? "0" : startPointStr;
                startPoint = Long.parseLong(startPointStr);
            }
        }
        return startPoint;
    }

    /**
     * 判断重名文件并进行重命名<br/>
     * downloadCover优先级 > downloadTempCover<br/>
     * 如果downloadCover==true则downloadTempCover==true此时如果文件和缓存文件都存在的情况下两个文件都将被覆盖<br/>
     * 如果downloadCover==false且downloadTempCover==true此时只会覆盖对应文件不存在的缓存文件，如果缓存文件对应的文件存在则不会覆盖
     *
     * @param downloadCover     是否覆盖已存在的重名文件
     * @param downloadTempCover 是否覆盖已存在的重名缓存文件
     * @param file              下载的文件
     * @return 重命名后的文件
     */
    private File chooseUniqueTempFile(File file, boolean downloadCover, boolean downloadTempCover) {
        String filePath = file.getAbsolutePath();
        if (!file.exists() || downloadCover) {
            return new File(filePath + SUFFIX_TEMP);
        }
        String filename = filePath.substring(0, filePath.lastIndexOf("."));
        String extension = filePath.substring(filePath.lastIndexOf("."), filePath.length());
        filename = filename + FILENAME_SEQUENCE_SEPARATOR;

        for (int sequence = 1; sequence < Integer.MAX_VALUE; sequence++) {
            filePath = filename + sequence + extension;
            File file1 = new File(filePath);
            if (downloadTempCover) {
                if (!file1.exists()) {
                    return new File(filePath + SUFFIX_TEMP);
                }
            } else {
                File tempFile = new File(filePath + SUFFIX_TEMP);
                if (!file1.exists() && !tempFile.exists()) {
                    return tempFile;
                }
            }

        }
        return new File(filePath + SUFFIX_TEMP);
    }

}
