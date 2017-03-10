package me.majiajie.http;


import me.majiajie.http.request.BaseRequest;
import me.majiajie.http.request.GetBuilder;
import me.majiajie.http.request.PostBuilder;
import me.majiajie.http.request.RequestType;
import me.majiajie.http.utils.HttpsUtils;
import okhttp3.OkHttpClient;
import okhttp3.internal.Platform;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * 访问自签名接口时使用，需要先调用{@link HttpsRequest#initialization}方法初始化证书
 */
public class HttpsRequest
{
    private static OkHttpClient mOkHttpClient = HttpRequest.getHttpClient();

    private static final int TIME_OUT = 10_000;//毫秒

    /**
     * 初始化，信任所有证书
     */
    public static void initialization(){

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        SSLSocketFactory sslSocketFactory = HttpsUtils.getTrustAllHttpsSSLSocketFactory();

        mOkHttpClient =  mOkHttpClient.newBuilder()
                .hostnameVerifier(hostnameVerifier)
                .sslSocketFactory(sslSocketFactory)
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 初始化,单向验证
     */
    public static void initialization(InputStream certificates) throws FileNotFoundException
    {
        SSLSocketFactory sslSocketFactory = HttpsUtils.getSslSocketFactory(certificates);
        X509TrustManager trustManager = Platform.get().trustManager(sslSocketFactory);
        initialization(sslSocketFactory,trustManager);
    }

    /**
     * 初始化,双向认证
     */
    public static void initialization(InputStream certificates, InputStream key, String keyPassword) throws FileNotFoundException
    {
        SSLSocketFactory sslSocketFactory = HttpsUtils.getSslSocketFactory(certificates,key,keyPassword);
        X509TrustManager trustManager = Platform.get().trustManager(sslSocketFactory);
        initialization(sslSocketFactory,trustManager);
    }

    private static void initialization(SSLSocketFactory sslSocketFactory,X509TrustManager trustManager)
    {
        mOkHttpClient =  mOkHttpClient.newBuilder()
                .sslSocketFactory(sslSocketFactory,trustManager)
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .build();
    }

    private HttpsRequest(){}

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

}
