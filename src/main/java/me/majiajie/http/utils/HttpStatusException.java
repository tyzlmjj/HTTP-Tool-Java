package me.majiajie.http.utils;

import me.majiajie.http.exception.HttpException;

/**
 * Created by mjj on 2017/4/19
 */
public class HttpStatusException {

    private HttpStatusException(){}

    public static HttpException getExceptionByCode(int httpCode){
        return new HttpException(HttpStatusCode.getMsgByCode(httpCode));
    }
}
