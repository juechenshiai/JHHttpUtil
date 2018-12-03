package com.jessehu.jhhttp.http;

import okhttp3.Callback;

/**
 * ProgressCallback
 *
 * @author JesseHu
 * @date 2018/11/30
 */
public interface ProgressCallback extends Callback {
    void onStarted();

    void onProgress();

    void onFinished();

    void onError();
}
