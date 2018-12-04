package com.jessehu.jhhttp.http;

import okhttp3.Callback;

/**
 * ProgressCallback
 *
 * @author JesseHu
 * @date 2018/11/30
 */
public interface ProgressCallback extends Callback {
    /**
     * 开始请求
     */
    void onStarted();

    /**
     * 传输(上传/下载)进度
     *
     * @param totalLength  文件大小
     * @param bytesWritten 已传输大小
     * @param percent      传输百分比
     */
    void onProgress(long totalLength, long bytesWritten, float percent);

    /**
     * 传输完成
     */
    void onFinished();
}
