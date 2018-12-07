package com.jessehu.jhhttp.http.parameter;

import com.jessehu.jhhttp.http.CertManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 请求参数<br/>
 * 包括请求头、请求参数、上传参数、下载参数
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

    /**
     * 请求头
     */
    private Map<String, Object> headers;
    /**
     * 请求参数，如果asJsonContent==true将会转换为Json String
     */
    private Map<String, Object> bodyParams;
    /**
     * 下载参数
     */
    private DownloadParams downloadParams;
    /**
     * post json请求
     */
    private boolean asJsonContent = false;
    /**
     * 自定义json数据，适合于bodyParams无法满足的json格式
     */
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

    /**
     * 设置https证书
     *
     * @param host
     * @param cert
     */
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

    /**
     * 设置请求头
     *
     * @param headers 请求头
     */
    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    /**
     * 添加单个请求头
     *
     * @param key   请求头key
     * @param value 请求头value
     */
    public void addHeaders(String key, Object value) {
        headers.put(key, value);
    }

    public DownloadParams getDownloadParams() {
        return downloadParams;
    }

    /**
     * 设置下载相关参数
     *
     * @param downloadParams 下载参数
     */
    public void setDownloadParams(DownloadParams downloadParams) {
        this.downloadParams = downloadParams;
    }

    /**
     * 设置下载断点(起始点)
     *
     * @param startPointKey   断点请求头key
     * @param startPointValue 断点请求头value，需要下载的起始点
     */
    public void setDownloadStartPoint(String startPointKey, long startPointValue) {
        if (downloadParams == null) {
            this.downloadParams = new DownloadParams(startPointKey, startPointValue);
        } else {
            this.downloadParams.setDownloadStartPoint(startPointKey, startPointValue);
        }
    }

    /**
     * 设置下载断点(起始点)
     *
     * @param startPointKey   断点请求头key
     * @param startPointValue 断点请求头value，需要下载的起始点
     * @param responseKey     断点响应头key，用户获取服务器返回的起始点
     */
    public void setDownloadStartPoint(String startPointKey, long startPointValue, String responseKey) {
        if (downloadParams == null) {
            this.downloadParams = new DownloadParams(startPointKey, startPointValue, responseKey);
        } else {
            this.downloadParams.setDownloadStartPoint(startPointKey, startPointValue, responseKey);
        }
    }

    /**
     * 设置下载文件的路径<br/>
     * 如果是完整文件路径，文件名将以路径中的文件名为准<br/>
     * 如果是文件的存储文件夹，文件名则为远端文件名
     *
     * @param downloadPath 保存路径
     */
    public void setDownloadFile(String downloadPath) {
        if (downloadParams == null) {
            this.downloadParams = new DownloadParams(downloadPath);
        } else {
            this.downloadParams.setDownloadPath(downloadPath);
        }
    }

    /**
     * 设置下载文件路径<br/>
     * 如果路径为完整文件路径，则会判断文件名是否一致<br/>
     * 如果文件名不为null且不一样将会报错,如果文件名为null则以路径中的文件名为准<br/>
     * 如果文件路径为null则保存到Download文件夹中<br/>
     * 如果文件路径与名称都为null，将会保存到Download文件夹中，文件名为远端文件名
     *
     * @param downloadPath     文件保存路径
     * @param downloadFilename 文件保存名称
     */
    public void setDownloadFile(String downloadPath, String downloadFilename) {
        if (downloadParams == null) {
            this.downloadParams = new DownloadParams(downloadPath, downloadFilename);
        } else {
            this.downloadParams.setDownloadFile(downloadPath, downloadFilename);
        }
    }

    /**
     * 是否覆盖重名文件<br/>
     * 如果为true，downloadTempCover也将为true,此时如果文件和缓存文件都存在的情况下两个文件都将被覆盖
     *
     * @param downloadCover true:覆盖 false:重命名后新建一个文件
     */
    public void setDownloadCover(boolean downloadCover) {
        if (downloadParams == null) {
            downloadParams = new DownloadParams();
        }
        downloadParams.setDownloadCover(downloadCover);
    }

    /**
     * 是否覆盖缓存文件<br/>
     * 只有downloadCover==false时有效，如果设为true，此时只会覆盖对应文件不存在的缓存文件，如果缓存文件对应的文件存在则不会覆盖
     *
     * @param downloadTempCover true:覆盖 false:新建一个缓存文件
     */
    public void setDownloadTempCover(boolean downloadTempCover) {
        if (downloadParams == null) {
            downloadParams = new DownloadParams();
        }
        downloadParams.setDownloadTempCover(downloadTempCover);
    }

    public Map<String, Object> getBodyParams() {
        return bodyParams;
    }

    /**
     * 设置请求参数
     *
     * @param bodyParams 请求参数
     */
    public void setBodyParams(Map<String, Object> bodyParams) {
        this.bodyParams = bodyParams;
    }

    /**
     * 添加单个参数
     *
     * @param key   参数key
     * @param value 参数value
     */
    public void addBodyParams(String key, Object value) {
        bodyParams.put(key, value);
    }

    public boolean isAsJsonContent() {
        return asJsonContent;
    }

    /**
     * 是否使用json提交<br/>
     * 如果json string为null则bodyParams将转换为json string<br/>
     * 如果json string不为null则直接提交json string，不转换bodyParams
     *
     * @param asJsonContent true:使用json提交参数 false:使用表单提交参数
     */
    public void setAsJsonContent(boolean asJsonContent) {
        this.asJsonContent = asJsonContent;
    }

    public String getJsonString() {
        return jsonString;
    }

    /**
     * 设置json string
     *
     * @param jsonString json string
     */
    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    /**
     * 添加上传文件
     *
     * @param key      上传参数key
     * @param filePath 上传文件的路径
     */
    public void addFile(String key, String filePath) {
        if (filePath == null) {
            throw new RuntimeException("File is not exists");
        }
        File file = new File(filePath);
        addFile(key, file);
    }

    /**
     * 添加上传文件
     *
     * @param key  上传参数key
     * @param file 上传的文件
     */
    public void addFile(String key, File file) {
        if (file.exists()) {
            bodyParams.put(key, file);
        } else {
            throw new RuntimeException("File is not exists");
        }
    }


}
