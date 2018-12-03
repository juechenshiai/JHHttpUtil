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
     * 开始上传
     */
    void onStarted();

    /**
     * 上传进度
     *
     * @param totalLength  文件大小
     * @param bytesWritten 已上传大小
     * @param percent      上传百分比
     */
    void onProgress(long totalLength, long bytesWritten, float percent);

    /**
     * 上传完成
     */
    void onFinished();
}
