package com.jessehu.jhhttp.http.parameter;

/**
 * 下载参数
 *
 * @author JesseHu
 * @date 2018/12/6
 */
public class DownloadParams {

    private String startPointKey;
    private long startPointValue;
    private String responseStartPointKey;
    private String downloadPath;
    private String downloadFilename;
    private boolean downloadCover = false;
    private boolean downloadTempCover = false;

    public DownloadParams() {
    }

    public DownloadParams(String startPointKey, long startPointValue) {
        this(startPointKey, startPointValue, null);
    }

    public DownloadParams(String startPointKey, long startPointValue, String responseStartPointKey) {
        this(startPointKey, startPointValue, responseStartPointKey, null, null);
    }

    public DownloadParams(String startPointKey, long startPointValue, String downloadPath, String downloadFilename) {
        this(startPointKey, startPointValue, null, downloadPath, downloadFilename);
    }

    public DownloadParams(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public DownloadParams(String downloadPath, String downloadFilename) {
        this(null, 0L, null, downloadPath, downloadFilename);
    }

    public DownloadParams(String startPointKey, long startPointValue, String responseStartPointKey, String downloadPath, String downloadFilename) {
        this.startPointKey = startPointKey;
        this.startPointValue = startPointValue;
        this.responseStartPointKey = responseStartPointKey;
        this.downloadPath = downloadPath;
        this.downloadFilename = downloadFilename;
    }

    /**
     * 设置下载断点(起始点)
     *
     * @param startPointKey   断点请求头key
     * @param startPointValue 断点请求头value，需要下载的起始点
     */
    public void setDownloadStartPoint(String startPointKey, long startPointValue) {
        setDownloadStartPoint(startPointKey, startPointValue, null);
    }

    /**
     * 设置下载断点(起始点)
     *
     * @param startPointKey         断点请求头key
     * @param startPointValue       断点请求头value，需要下载的起始点
     * @param responseStartPointKey 断点响应头key，用户获取服务器返回的起始点
     */
    public void setDownloadStartPoint(String startPointKey, long startPointValue, String responseStartPointKey) {
        this.startPointKey = startPointKey;
        this.startPointValue = startPointValue;
        this.responseStartPointKey = responseStartPointKey;
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
        this.downloadPath = downloadPath;
        this.downloadFilename = downloadFilename;
    }

    public String getStartPointKey() {
        return startPointKey;
    }

    public void setStartPointKey(String startPointKey) {
        this.startPointKey = startPointKey;
    }

    public long getStartPointValue() {
        return startPointValue;
    }

    public void setStartPointValue(long startPointValue) {
        this.startPointValue = startPointValue;
    }

    public String getResponseStartPointKey() {
        return responseStartPointKey;
    }

    public void setResponseStartPointKey(String responseStartPointKey) {
        this.responseStartPointKey = responseStartPointKey;
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

    public boolean isDownloadCover() {
        return downloadCover;
    }

    public void setDownloadCover(boolean downloadCover) {
        this.downloadCover = downloadCover;
    }

    public boolean isDownloadTempCover() {
        return downloadTempCover;
    }

    public void setDownloadTempCover(boolean downloadTempCover) {
        this.downloadTempCover = downloadTempCover;
    }
}