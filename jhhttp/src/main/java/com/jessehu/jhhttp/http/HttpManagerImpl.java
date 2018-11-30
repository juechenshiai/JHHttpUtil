package com.jessehu.jhhttp.http;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * HttpManagerImpl
 *
 * @author JesseHu
 * @date 2018/11/28
 */
public interface HttpManagerImpl {
    /**
     * GET异步请求<br/>
     * 使用回调方式获取数据<br/>
     * 适合链接请求参数直接拼接在连接上或者无请求参数
     *
     * @param url      请求链接
     * @param callback 请求回调函数
     */
    void get(String url, Callback callback);

    /**
     * GET异步请求<br/>
     * 使用回调方式获取数据<br/>
     * 需要添加请求头或者参数不直接拼接在链接上等
     *
     * @param requestParams 请求参数
     * @param callback      请求回调函数
     */
    void get(RequestParams requestParams, Callback callback);

    /**
     * GET同步请求<br/>
     * 使用返回参数形式获取数据<br/>
     * 适合链接请求参数直接拼接在连接上或者无请求参数
     *
     * @param url 请求链接
     * @return HTTP response
     * @throws IOException IO异常
     */
    Response get(String url) throws IOException;

    /**
     * GET同步请求<br/>
     * 使用返回参数形式获取数据<br/>
     * 适合需要添加请求头或者参数不直接拼接在链接上等
     *
     * @param requestParams 请求参数
     * @return HTTP response
     * @throws IOException IO异常
     */
    Response get(RequestParams requestParams) throws IOException;

    /**
     * POST请求<br/>
     * 使用回调方式获取数据<br/>
     * 适合链接请求参数直接拼接在连接上或者无请求参数
     *
     * @param url      请求链接
     * @param callback 请求回调函数
     */
    void post(String url, Callback callback);

    /**
     * POST请求<br/>
     * 使用回调方式获取数据<br/>
     * 需要添加请求头或者参数不直接拼接在链接上等
     *
     * @param requestParams 请求参数
     * @param callback      请求回调函数
     */
    void post(RequestParams requestParams, Callback callback);

    /**
     * POST请求<br/>
     * 使用返回参数形式获取数据<br/>
     * 适合链接请求参数直接拼接在连接上或者无请求参数
     *
     * @param url 请求链接
     * @return HTTP response
     * @throws IOException IO异常
     */
    Response post(String url) throws IOException;

    /**
     * POST请求<br/>
     * 使用返回参数形式获取数据<br/>
     * 适合需要添加请求头或者参数不直接拼接在链接上等
     *
     * @param requestParams 请求参数
     * @return HTTP response
     * @throws IOException IO异常
     */
    Response post(RequestParams requestParams) throws IOException;
}