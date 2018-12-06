package com.jessehu.jhhttp;

import com.jessehu.jhhttp.http.HttpManagerImpl;
import com.jessehu.jhhttp.http.HttpManager;
import com.jessehu.jhhttp.thread.ThreadPoolManager;
import com.jessehu.jhhttp.thread.ThreadPoolManagerImpl;

/**
 * library入口
 *
 * @author JesseHu
 * @date 2018/11/28
 */
public class JH {

    private static HttpManager mHttpManager;
    private static ThreadPoolManager mThreadPoolManager;

    public static HttpManager http() {
        if (mHttpManager == null) {
            HttpManagerImpl.registerInstance();
        }
        return mHttpManager;
    }

    public static ThreadPoolManager thread() {
        if (mThreadPoolManager == null) {
            ThreadPoolManagerImpl.registerInstance();
        }
        return mThreadPoolManager;
    }

    public static void setThreadPoolManager(ThreadPoolManagerImpl mThreadPoolManagerImpl) {
        JH.mThreadPoolManager = mThreadPoolManagerImpl;
    }

    public static void setHttpManager(HttpManagerImpl mHttpManagerImpl) {
        JH.mHttpManager = mHttpManagerImpl;
    }
}
