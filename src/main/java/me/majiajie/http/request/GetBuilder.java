package me.majiajie.http.request;


import me.majiajie.http.Result;
import me.majiajie.http.callback.HttpCallBack;

import java.util.Map;

public interface GetBuilder {

    /**
     * 增加一个头部参数
     *
     * @param name      参数名
     * @param value     参数值
     */
    GetBuilder addHeader(String name, String value);

    /**
     * 设置头部参数
     *
     * @param headers   整个头部参数集合
     */
    GetBuilder headers(Map<String, String> headers);

    /**
     * 设置连接超时时间，单位毫秒
     */
    GetBuilder connectTimeout(long connectTimeout);

    /**
     * 设置读入超时时间，单位毫秒
     */
    GetBuilder readTimeout(long readTimeout);

    /**
     * 设置写出超时时间，单位毫秒
     */
    GetBuilder writeTimeout(long writeTimeout);

    /**
     * 在当前线程执行
     */
    Result execute();

    /**
     * 在子线程执行
     */
    void enqueue(final HttpCallBack callBack);
}
