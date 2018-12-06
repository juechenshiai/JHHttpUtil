package com.jessehu.jhhttp.http.parameter;

/**
 * DownloadParams
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

    public void setDownloadStartPoint(String startPointKey, long startPointValue) {
        setDownloadStartPoint(startPointKey, startPointValue, null);
    }

    public void setDownloadStartPoint(String startPointKey, long startPointValue, String responseStartPointKey) {
        this.startPointKey = startPointKey;
        this.startPointValue = startPointValue;
        this.responseStartPointKey = responseStartPointKey;
    }

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
}