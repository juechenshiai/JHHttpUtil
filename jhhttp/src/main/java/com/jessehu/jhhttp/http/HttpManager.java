package com.jessehu.jhhttp.http;

import com.jessehu.jhhttp.http.callback.ProgressCallback;

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
     * @param url      请求链接
     * @param key      请求参数名称
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
     * @param url              请求链接
     * @param key              请求参数名称
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

    /**
     * 文件下载，有进度回调<br/>
     * 可以选择自动下载或者自己处理下载<br/>
     * 直到下载默认保存到Download目录下，使用远端文件的名称<br/>
     * 自己处理下载将不会下载文件，所有下载及保存操作自行处理
     *
     * @param url              请求链接
     * @param autoDownload     是否自动下载 true：直到现在，false：自己处理下载
     * @param progressCallback 进度回调
     */
    void download(String url, boolean autoDownload, ProgressCallback progressCallback);

    /**
     * 文件下载，有进度回调<br/>
     * 可以选择自动下载或者自己处理下载<br/>
     * 直到下载默认保存到Download目录下，使用远端文件的名称<br/>
     * 自己处理下载将不会下载文件，所有下载及保存操作自行处理
     *
     * @param requestParams    请求参数
     * @param autoDownload     是否自动下载 true：直到现在，false：自己处理下载
     * @param progressCallback 进度回调
     */
    void download(RequestParams requestParams, boolean autoDownload, ProgressCallback progressCallback);

    /**
     * 文件下载，有进度回调<br/>
     * 支持自定义保存路径，默认使用远端文件的名称
     *
     * @param url              请求链接
     * @param filePath         保存路径
     * @param progressCallback 进度回调
     */
    void download(String url, String filePath, ProgressCallback progressCallback);

    /**
     * 文件下载，有进度回调<br/>
     * 支持自定义保存路径，默认使用远端文件的名称
     *
     * @param requestParams    请求链接
     * @param filePath         文件保存路径
     * @param progressCallback 进度回调
     */
    void download(RequestParams requestParams, String filePath, ProgressCallback progressCallback);

    /**
     * 文件下载，有进度回调<br/>
     * 支持自定义保存路径及名称
     *
     * @param url              请求链接
     * @param filePath         文件保存路径
     * @param fileName         文件保存名称
     * @param progressCallback 进度回调
     */
    void download(String url, String filePath, String fileName, ProgressCallback progressCallback);

    /**
     * 文件下载，有进度回调<br/>
     * 支持自定义保存路径及名称
     *
     * @param requestParams    请求参数
     * @param filePath         文件保存路径
     * @param fileName         文件保存名称
     * @param progressCallback 进度回调
     */
    void download(RequestParams requestParams, String filePath, String fileName, ProgressCallback progressCallback);
}