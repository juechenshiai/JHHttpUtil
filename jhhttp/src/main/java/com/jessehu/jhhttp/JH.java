package com.jessehu.jhhttp;

import com.jessehu.jhhttp.http.HttpManager;
import com.jessehu.jhhttp.http.HttpManagerImpl;
import com.jessehu.jhhttp.thread.ThreadPoolManager;
import com.jessehu.jhhttp.thread.ThreadPoolManagerImpl;

/**
 * JH
 *
 * @author JesseHu
 * @date 2018/11/28
 */
public class JH {

    private static HttpManagerImpl mHttpManager;
    private static ThreadPoolManagerImpl mThreadPoolManager;

    public static HttpManagerImpl http() {
        if (mHttpManager == null) {
            HttpManager.registerInstance();
        }
        return mHttpManager;
    }

    public static ThreadPoolManagerImpl thread() {
        if (mThreadPoolManager == null) {
            ThreadPoolManager.registerInstance();
        }
        return mThreadPoolManager;
    }

    public static void setThreadPoolManager(ThreadPoolManager mThreadPoolManager) {
        JH.mThreadPoolManager = mThreadPoolManager;
    }

    public static void setHttpManager(HttpManager mHttpManager) {
        JH.mHttpManager = mHttpManager;
    }
}
