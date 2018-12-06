package com.jessehu.jhhttp.thread;

import android.os.Handler;
import android.os.Looper;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jessehu.jhhttp.JH;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程实现
 *
 * @author JesseHu
 * @date 2018/8/16
 */
public class ThreadPoolManagerImpl implements ThreadPoolManager {

    private static final Object LOCK = new Object();
    private static volatile ThreadPoolManagerImpl instance;
    private static Handler mHandler;

    public static void registerInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new ThreadPoolManagerImpl();
            }
        }
        JH.setThreadPoolManager(instance);
    }


    @Override
    public void singleThread(Runnable runnable) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-pool-%d").build();
        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        singleThreadPool.execute(runnable);
        singleThreadPool.shutdown();
    }


    @Override
    public void multipleThread(int corePoolSize, int maximumPoolSize, long keepAliveTime, Runnable runnable) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-pool-%d").build();
        ExecutorService multipleThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        multipleThreadPool.execute(runnable);
        multipleThreadPool.shutdown();
    }

    @Override
    public void uiThread(Runnable runnable) {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.post(runnable);
    }
}
