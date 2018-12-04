package com.jessehu.jhhttp.http;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * RequestParams
 *
 * @author JesseHu
 * @date 2018/11/29
 */
public class RequestParams {
    private String charset = "UTF-8";
    private String url;
    private X509TrustManager x509TrustManager;
    private SSLSocketFactory sslSocketFactory;
    /**
     * https域名校验
     */
    private HostnameVerifier hostnameVerifier;
    /**
     * 连接超时时间
     */
    private int connectTimeout = 1000 * 15;
    /**
     * 读取超时时间
     */
    private int readTimeout = 1000 * 15;
    /**
     * 写入超时时间
     */
    private int writeTimeout = 1000 * 15;

    private Map<String, Object> headers;
    private Map<String, Object> bodyParams;
    private boolean asJsonContent = false;
    private String jsonString;

    public RequestParams() {
        headers = new HashMap<>();
        bodyParams = new HashMap<>();
    }

    public RequestParams(String url) {
        this();
        this.url = url.trim();
    }

    public RequestParams(String url, String host, String cert) {
        this(url);
        setHttpsCert(host, cert);
    }

    public void setHttpsCert(String host, String cert) {
        sslSocketFactory = CertManager.getSocketFactory(cert);
        x509TrustManager = CertManager.getX509TrustManager(cert);
        hostnameVerifier = new CertManager.MyHostnameVerifier(host);
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

    public void setX509TrustManager(X509TrustManager x509TrustManager) {
        this.x509TrustManager = x509TrustManager;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public void addHeaders(String key, Object value) {
        headers.put(key, value);
    }

    public Map<String, Object> getBodyParams() {
        return bodyParams;
    }

    public boolean isAsJsonContent() {
        return asJsonContent;
    }

    public void setAsJsonContent(boolean asJsonContent) {
        this.asJsonContent = asJsonContent;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public void setBodyParams(Map<String, Object> bodyParams) {
        this.bodyParams = bodyParams;
    }

    public void addBodyParams(String key, Object value) {
        bodyParams.put(key, value);
    }

    public void addFile(String key, String filePath) {
        File file = new File(filePath);
        addFile(key, file);
    }

    public void addFile(String key, File file) {
        if (file.exists()) {
            bodyParams.put(key, file);
        } else {
            throw new RuntimeException("File is not exists");
        }
    }
}
