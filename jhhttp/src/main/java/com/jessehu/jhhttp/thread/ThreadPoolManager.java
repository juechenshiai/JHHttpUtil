package com.jessehu.jhhttp.thread;

/**
 * 线程接口
 *
 * @author JesseHu
 * @date 2018/11/28
 */
public interface ThreadPoolManager {
    /**
     * 单个线程线程池
     *
     * @param runnable Runnable
     */
    void singleThread(Runnable runnable);

    /**
     * 多线程线程池
     *
     * @param corePoolSize    线程池大小
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   存活时间
     * @param runnable        Runnable
     */
    void multipleThread(int corePoolSize, int maximumPoolSize, long keepAliveTime, Runnable runnable);

    /**
     * UI 线程
     *
     * @param runnable Runnable
     */
    void uiThread(Runnable runnable);
}
