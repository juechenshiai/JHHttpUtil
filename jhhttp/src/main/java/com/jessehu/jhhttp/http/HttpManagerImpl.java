package com.jessehu.jhhttp.http;

import android.support.annotation.NonNull;

import com.jessehu.jhhttp.JH;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
        Call call = getRequestCall(requestParams, false);
        call.enqueue(callback);
    }

    @Override
    public Response get(String url) throws IOException {
        RequestParams requestParams = new RequestParams(url);
        return get(requestParams);
    }

    @Override
    public Response get(RequestParams requestParams) throws IOException {
        Call call = getRequestCall(requestParams, false);
        return call.execute();
    }

    @Override
    public void post(String url, Callback callback) {
        RequestParams requestParams = new RequestParams(url);
        post(requestParams, callback);
    }

    @Override
    public void post(RequestParams requestParams, Callback callback) {
        Call call = getRequestCall(requestParams, true);
        call.enqueue(callback);
    }

    @Override
    public Response post(String url) throws IOException {
        RequestParams requestParams = new RequestParams(url);
        return post(requestParams);
    }

    @Override
    public Response post(RequestParams requestParams) throws IOException {
        Call call = getRequestCall(requestParams, true);
        return call.execute();
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
        if (value == null || value instanceof String || value instanceof Number) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 添加请求头和参数等到请求中
     *
     * @param requestParams 参数
     * @param isPost        是否是POST请求
     * @return request call
     */
    @NonNull
    private Call getRequestCall(RequestParams requestParams, boolean isPost) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();
        String url = requestParams.getUrl();
        if (url == null) {
            throw new RuntimeException("Url is empty");
        }
        requestBuilder.url(url);

        for (String key : requestParams.getHeaders().keySet()) {
            Object value = requestParams.getHeaders().get(key);
            if (typeJudgment(value)) {
                requestBuilder.addHeader(key, value == null ? "" : value.toString());
            } else {
                throw new RuntimeException("Header types are not supported");
            }
        }

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
        if (isPost) {
            FormBody.Builder requestBodyBuilder = new FormBody.Builder();
            for (String key : requestParams.getBodyParams().keySet()) {
                Object value = requestParams.getBodyParams().get(key);
                if (typeJudgment(value)) {
                    requestBodyBuilder.add(key, value == null ? "" : value.toString());
                } else {
                    throw new RuntimeException("Parameter types are not supported");
                }
            }
            requestBuilder.post(requestBodyBuilder.build());
        } else {
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

        Request request = requestBuilder.build();
        return okHttpClient.newCall(request);
    }

}
