package com.jessehu.jhhttp.http;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import com.jessehu.jhhttp.JH;

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
 * HttpManagerImpl
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

    // TODO: 2018/12/4 json格式数据传输

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
    public Response get(String url) throws IOException {
        RequestParams requestParams = new RequestParams(url);
        return get(requestParams);
    }

    @Override
    public Response get(RequestParams requestParams) throws IOException {
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
    public Response post(String url) throws IOException {
        RequestParams requestParams = new RequestParams(url);
        return post(requestParams);
    }

    @Override
    public Response post(RequestParams requestParams) throws IOException {
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
        Call call = getRequestCall(requestParams, METHOD_UPLOAD, null);
        call.enqueue(callback);
    }

    @Override
    public void upload(String url, String key, String filePath, ProgressCallback progressCallback) {
        RequestParams requestParams = new RequestParams(url);
        requestParams.addFile(key, filePath);
        upload(requestParams, progressCallback);
    }

    @Override
    public void upload(RequestParams requestParams, @NonNull ProgressCallback progressCallback) {
        progressCallback.onStarted();
        Call call = getRequestCall(requestParams, METHOD_UPLOAD, progressCallback);
        call.enqueue(progressCallback);
    }

    @Override
    public void download(String url, boolean autoDownload, ProgressCallback progressCallback) {
        RequestParams requestParams = new RequestParams(url);
        download(requestParams, autoDownload, progressCallback);
    }

    @Override
    public void download(RequestParams requestParams, boolean autoDownload, ProgressCallback progressCallback) {
        if (autoDownload) {
            download(requestParams, null, null, progressCallback);
        } else {
            progressCallback.onStarted();
            Call call = getRequestCall(requestParams, METHOD_DOWNLOAD, progressCallback);
            call.enqueue(progressCallback);
        }
    }

    @Override
    public void download(String url, String filePath, ProgressCallback progressCallback) {
        RequestParams requestParams = new RequestParams(url);
        download(requestParams, filePath, progressCallback);
    }

    @Override
    public void download(RequestParams requestParams, String filePath, ProgressCallback progressCallback) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            download(requestParams, filePath, null, progressCallback);
        } else {
            download(requestParams, file.getParent(), file.getName(), progressCallback);
        }
    }

    @Override
    public void download(String url, String filePath, String fileName, ProgressCallback progressCallback) {
        RequestParams requestParams = new RequestParams(url);
        download(requestParams, filePath, fileName, progressCallback);
    }

    @Override
    public void download(final RequestParams requestParams, final String filePath, final String fileName, final ProgressCallback progressCallback) {
        progressCallback.onStarted();
        Call call = getRequestCall(requestParams, METHOD_DOWNLOAD, progressCallback);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressCallback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressCallback.onResponse(call, response);
                String path = filePath;
                if (path == null) {
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                }
                String name = fileName;
                if (name == null) {
                    String url = response.request().url().toString();
                    String mimeType = MimeTypeMap.getFileExtensionFromUrl(url);
                    name = URLUtil.guessFileName(url, null, mimeType);
                }
                File file = new File(path, name);
                saveFile(call, response, file, progressCallback);
            }
        });
    }

    /**
     * 保存下载的文件
     *
     * @param call             Call
     * @param response         Response
     * @param file             要保存的文件
     * @param progressCallback 进度回调
     */
    private void saveFile(Call call, Response response, File file, ProgressCallback progressCallback) {
        InputStream is = null;
        RandomAccessFile randomAccessFile = null;
        BufferedInputStream bis = null;
        byte[] buff = new byte[2048];
        int len;
        try {
            is = response.body().byteStream();
            bis = new BufferedInputStream(is);

            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            // 随机访问文件，可以指定断点续传的起始位置
            randomAccessFile = new RandomAccessFile(file, "rwd");
//                    randomAccessFile.seek (startsPoint);
            while ((len = bis.read(buff)) != -1) {
                randomAccessFile.write(buff, 0, len);
            }

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

        addHeaders(requestParams, requestBuilder);
        addClientParams(requestParams, clientBuilder, url);

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
                doDownload(clientBuilder, progressCallback);
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
     * @param url           请求连接
     */
    private void addClientParams(RequestParams requestParams, OkHttpClient.Builder clientBuilder, String url) {
        clientBuilder.connectTimeout(requestParams.getConnectTimeout(), TimeUnit.MILLISECONDS);
        clientBuilder.writeTimeout(requestParams.getWriteTimeout(), TimeUnit.MILLISECONDS);
        clientBuilder.writeTimeout(requestParams.getWriteTimeout(), TimeUnit.MILLISECONDS);

        if (url.startsWith(TYPE_HTTPS)) {
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
    private void addHeaders(RequestParams requestParams, Request.Builder requestBuilder) {
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
        HttpUrl.Builder httpUrlBuilder = new HttpUrl.Builder();
        for (String key : requestParams.getBodyParams().keySet()) {
            Object value = requestParams.getBodyParams().get(key);
            if (typeJudgment(value)) {
                httpUrlBuilder.addQueryParameter(key, value == null ? "" : value.toString());
            } else {
                throw new RuntimeException("Parameter types are not supported");
            }
        }
        requestBuilder.get();
    }

    /**
     * POST请求添加参数
     *
     * @param requestParams  请求参数
     * @param requestBuilder Request.Builder
     */
    private void doPost(RequestParams requestParams, Request.Builder requestBuilder) {
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
     * @param clientBuilder OkHttpClient.Builder
     */
    private void doDownload(OkHttpClient.Builder clientBuilder, final ProgressCallback progressCallback) {
        if (progressCallback != null) {
            // 重写ResponseBody监听请求
            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressCallback))
                            .build();
                }
            };
            clientBuilder.addInterceptor(interceptor);
        }
    }

}
