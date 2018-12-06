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
    /**
     * 请求链接
     */
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
    private DownloadStartPoint downloadStartPoint;
    private boolean asJsonContent = false;
    private String jsonString;
    private String downloadPath;
    private String downloadFilename;

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

    public DownloadStartPoint getDownloadStartPoint() {
        return downloadStartPoint;
    }

    public void setDownloadStartPoint(DownloadStartPoint downloadStartPoint) {
        this.downloadStartPoint = downloadStartPoint;
    }

    public void setDownloadStartPoint(String startPointKey, long startPointValue) {
        this.downloadStartPoint = new DownloadStartPoint(startPointKey, startPointValue);
    }

    public void setDownloadStartPoint(String startPointKey, long startPointValue, String responseKey) {
        this.downloadStartPoint = new DownloadStartPoint(startPointKey, startPointValue, responseKey);
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

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getDownloadFilename() {
        return downloadFilename;
    }

    public void setDownloadFilename(String downloadFilename) {
        this.downloadFilename = downloadFilename;
    }

    public void setDowload(String filePath, String filename) {
        this.downloadPath = filePath;
        this.downloadFilename = filename;
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

    public static class DownloadStartPoint {

        public String startPointKey;
        public long startPointValue;
        public String responseStartPointKey;

        public DownloadStartPoint(String startPointKey, long startPointValue) {
            this(startPointKey, startPointValue, null);
        }

        public DownloadStartPoint(String startPointKey, long startPointValue, String responseStartPointKey) {
            this.startPointKey = startPointKey;
            this.startPointValue = startPointValue;
            this.responseStartPointKey = responseStartPointKey;
        }
    }
}
