package com.jessehu.jhhttp.http;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Response;

/**
 * HttpManager
 *
 * @author JesseHu
 * @date 2018/11/28
 */
public interface HttpManager {
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

    /**
     * 文件上传，无进度回调
     *
     * @param key      请求参数名称
     * @param url      请求链接
     * @param filePath 文件路径
     * @param callback 请求回调
     */
    void upload(String url, String key, String filePath, Callback callback);

    /**
     * 文件上传，无进度回调
     *
     * @param requestParams 请求参数
     * @param callback      请求回调
     */
    void upload(RequestParams requestParams, Callback callback);

    /**
     * 文件上传，有进度回调
     *
     * @param key              请求参数名称
     * @param url              请求链接
     * @param filePath         文件路径
     * @param progressCallback 进度回调
     */
    void upload(String url, String key, String filePath, ProgressCallback progressCallback);

    /**
     * 文件上传，有进度回调
     *
     * @param requestParams    请求参数
     * @param progressCallback 进度回调
     */
    void upload(RequestParams requestParams, ProgressCallback progressCallback);
}