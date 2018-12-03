package com.jessehu.jhhttp.http;

import android.support.annotation.NonNull;

import com.jessehu.jhhttp.JH;

import java.io.File;
import java.io.IOException;
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

    private ProgressCallback mProgressCallback;

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
        Call call = getHttpRequestCall(requestParams, false);
        call.enqueue(callback);
    }

    @Override
    public Response get(String url) throws IOException {
        RequestParams requestParams = new RequestParams(url);
        return get(requestParams);
    }

    @Override
    public Response get(RequestParams requestParams) throws IOException {
        Call call = getHttpRequestCall(requestParams, false);
        return call.execute();
    }

    @Override
    public void post(String url, Callback callback) {
        RequestParams requestParams = new RequestParams(url);
        post(requestParams, callback);
    }

    @Override
    public void post(RequestParams requestParams, Callback callback) {
        Call call = getHttpRequestCall(requestParams, true);
        call.enqueue(callback);
    }

    @Override
    public Response post(String url) throws IOException {
        RequestParams requestParams = new RequestParams(url);
        return post(requestParams);
    }

    @Override
    public Response post(RequestParams requestParams) throws IOException {
        Call call = getHttpRequestCall(requestParams, true);
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
        Call call = getUploadRequestCall(requestParams);
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
        this.mProgressCallback = progressCallback;
        progressCallback.onStarted();
        Call call = getUploadRequestCall(requestParams);
        call.enqueue(progressCallback);
    }

    @Override
    public void download(String url, ProgressCallback progressCallback) {

    }

    @Override
    public void download(RequestParams requestParams, ProgressCallback progressCallback) {

    }

    @Override
    public void download(String url, String filePath, ProgressCallback progressCallback) {

    }

    @Override
    public void download(RequestParams requestParams, String filePath, ProgressCallback progressCallback) {

    }

    @Override
    public void download(String url, String filePath, String fileName, ProgressCallback progressCallback) {

    }

    @Override
    public void download(RequestParams requestParams, String filePath, String fileName, ProgressCallback progressCallback) {

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
     * 获取HTTP请求Call对象
     *
     * @param requestParams 请求参数
     * @param isPost        是否为POST请求
     * @return OkHttp Call对象
     */
    private Call getHttpRequestCall(RequestParams requestParams, boolean isPost) {
        return getRequestCall(requestParams, isPost, false);
    }

    /**
     * 获取上传文件Call对象
     *
     * @param requestParams 请求参数
     * @return OkHttp Call对象
     */
    private Call getUploadRequestCall(RequestParams requestParams) {
        return getRequestCall(requestParams, true, true);
    }

    /**
     * 添加请求头和参数等到请求中
     *
     * @param requestParams 参数
     * @param isPost        是否是POST请求
     * @param isUpload      是否为上传文件
     * @return request call
     */
    @NonNull
    private Call getRequestCall(RequestParams requestParams, boolean isPost, boolean isUpload) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();
        String url = requestParams.getUrl();
        if (url == null) {
            throw new RuntimeException("Url is empty");
        }
        requestBuilder.url(url);

        addHeaders(requestParams, requestBuilder);

        addClientParams(requestParams, okHttpClient, url);

        if (isPost) {
            if (isUpload) {
                doUpload(requestParams, requestBuilder);
            } else {
                doPost(requestParams, requestBuilder);
            }
        } else {
            doGet(requestParams, requestBuilder);
        }

        Request request = requestBuilder.build();
        return okHttpClient.newCall(request);
    }

    /**
     * 添加Client参数
     *
     * @param requestParams 请求参数
     * @param okHttpClient  OkHttpClient
     * @param url           请求连接
     */
    private void addClientParams(RequestParams requestParams, OkHttpClient okHttpClient, String url) {
        OkHttpClient.Builder clientBuilder = okHttpClient.newBuilder();
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
    private void doUpload(RequestParams requestParams, Request.Builder requestBuilder) {
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
                    if (mProgressCallback != null) {
                        requestBody = new ProgressRequestBody(requestBody, mProgressCallback);
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

}
