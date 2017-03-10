package me.majiajie.http;

import me.majiajie.http.request.*;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * 进行Http请求
 * 添加依赖
 * compile 'com.squareup.okhttp3:okhttp:3.3.1'
 */
public class HttpRequest
{
    private static final OkHttpClient mOkHttpClient;

    private static final long TIME_OUT = 10_000;//毫秒

    static
    {
        //全局配置
        mOkHttpClient =  new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .build();
    }

    private HttpRequest(){}

    public static GetBuilder doGet(String url)
    {
        return doit(url, RequestType.GET);
    }

    public static PostBuilder doPost(String url)
    {
        return doit(url, RequestType.POST);
    }

    public static PostBuilder doPut(String url)
    {
        return doit(url, RequestType.PUT);
    }

    public static PostBuilder doDelete(String url)
    {
        return doit(url, RequestType.DELETE);
    }

    private static BaseRequest.Builder doit(String url,RequestType requestType)
    {
        return new BaseRequest(url, mOkHttpClient, requestType).newBuilder();
    }

    public static OkHttpClient getHttpClient()
    {
        return mOkHttpClient;
    }

}
