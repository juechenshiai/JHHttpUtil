package com.jessehu.jhhttp.http;

import com.jessehu.jhhttp.http.callback.ProgressCallback;
import com.jessehu.jhhttp.http.parameter.RequestParams;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Response;

/**
 * HTTP请求接口
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
     * @param callback 请求回调 {@link Callback}
     */
    void get(String url, Callback callback);

    /**
     * GET异步请求<br/>
     * 使用回调方式获取数据<br/>
     * 需要添加请求头或者参数不直接拼接在链接上等
     *
     * @param requestParams 请求参数
     * @param callback      请求回调 {@link Callback}
     */
    void get(RequestParams requestParams, Callback callback);

    /**
     * GET同步请求<br/>
     * 使用返回参数形式获取数据<br/>
     * 适合链接请求参数直接拼接在连接上或者无请求参数
     *
     * @param url 请求链接
     * @return OkHttp {@link Response}
     * @throws IOException IO异常
     */
    Response getSync(String url) throws IOException;

    /**
     * GET同步请求<br/>
     * 使用返回参数形式获取数据<br/>
     * 适合需要添加请求头或者参数不直接拼接在链接上等
     *
     * @param requestParams 请求参数
     * @return OkHttp {@link Response}
     * @throws IOException IO异常
     */
    Response getSync(RequestParams requestParams) throws IOException;

    /**
     * POST请求<br/>
     * 使用回调方式获取数据<br/>
     * 适合链接请求参数直接拼接在连接上或者无请求参数
     *
     * @param url      请求链接
     * @param callback 请求回调 {@link Callback}
     */
    void post(String url, Callback callback);

    /**
     * POST请求<br/>
     * 使用回调方式获取数据<br/>
     * 需要添加请求头或者参数不直接拼接在链接上等
     *
     * @param requestParams 请求参数
     * @param callback      请求回调 {@link Callback}
     */
    void post(RequestParams requestParams, Callback callback);

    /**
     * POST请求<br/>
     * 使用返回参数形式获取数据<br/>
     * 适合链接请求参数直接拼接在连接上或者无请求参数
     *
     * @param url 请求链接
     * @return OkHttp {@link Response}
     * @throws IOException IO异常
     */
    Response postSync(String url) throws IOException;

    /**
     * POST请求<br/>
     * 使用返回参数形式获取数据<br/>
     * 适合需要添加请求头或者参数不直接拼接在链接上等
     *
     * @param requestParams 请求参数
     * @return OkHttp {@link Response}
     * @throws IOException IO异常
     */
    Response postSync(RequestParams requestParams) throws IOException;

    /**
     * 文件上传<br/>
     * 使用Callback没有进度回调<br/>
     * 使用ProgressCallback有进度回调
     *
     * @param url      请求链接
     * @param key      请求参数名称
     * @param filePath 文件路径
     * @param callback 请求回调 {@link Callback} or {@link ProgressCallback}
     */
    void upload(String url, String key, String filePath, Callback callback);

    /**
     * 文件上传<br/>
     * 使用Callback没有进度回调<br/>
     * 使用ProgressCallback有进度回调
     *
     * @param requestParams 请求参数
     * @param callback      请求回调 {@link Callback} or {@link ProgressCallback}
     */
    void upload(RequestParams requestParams, Callback callback);

    /**
     * 文件下载<br/>
     * 可以选择Callback或者ProgressCallback<br/>
     * 使用ProgressCallback下载默认保存到Download目录下，使用远端文件的名称，有进度回调<br/>
     * 使用Callback将不会下载文件，所有下载及保存操作自行处理
     *
     * @param url      请求链接
     * @param callback 请求回调 {@link Callback} or {@link ProgressCallback}
     */
    void download(String url, Callback callback);

    /**
     * 文件下载<br/>
     * 可以选择Callback或者ProgressCallback<br/>
     * 使用ProgressCallback下载默认保存到Download目录下，使用远端文件的名称，有进度回调<br/>
     * 使用Callback将不会下载文件，所有下载及保存操作自行处理
     *
     * @param requestParams 请求参数
     * @param callback      请求回调 {@link Callback} or {@link ProgressCallback}
     */
    void download(RequestParams requestParams, Callback callback);
}