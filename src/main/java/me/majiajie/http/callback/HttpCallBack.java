package me.majiajie.http.callback;

import okhttp3.Response;

/**
 * Http回调
 */
public interface HttpCallBack {

    /**
     * 请求成功, code:[200,300)
     */
    void onResponse(Response response);

    /**
     * 发生异常
     */
    void onError(int code, String message, Response response);
}
