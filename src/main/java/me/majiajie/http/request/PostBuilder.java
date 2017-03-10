package me.majiajie.http.request;


import me.majiajie.http.Result;
import me.majiajie.http.callback.HttpCallBack;
import me.majiajie.http.progress.ProgressRequestBody;
import me.majiajie.http.progress.ProgressResponseBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

import java.util.Map;

public interface PostBuilder{

    /**
     * 增加一个头部参数
     *
     * @param name      参数名
     * @param value     参数值
     */
    PostBuilder addHeader(String name, String value);

    /**
     * 设置头部参数
     *
     * @param headers   整个头部参数集合
     */
    PostBuilder headers(Map<String, String> headers);

    /**
     * 设置连接超时时间，单位毫秒
     */
    PostBuilder connectTimeout(long connectTimeout);

    /**
     * 设置读入超时时间，单位毫秒
     */
    PostBuilder readTimeout(long readTimeout);

    /**
     * 设置写出超时时间，单位毫秒
     */
    PostBuilder writeTimeout(long writeTimeout);

    /**
     * 设置提交的数据
     */
    PostBuilder params(Map<String, String> params);

    /**
     * 设置提交的数据
     */
    PostBuilder params(String params);

    /**
     * 设置提交的数据
     */
    PostBuilder params(byte[] params);

    /**
     * 设置提交的数据和类型，常用类型使用{@link me.majiajie.http.MT MT}
     */
    PostBuilder params(MediaType mediaType, String params);

    /**
     * 多类型参数上传,例子：
     * <p>
     * MultipartBody multipartBody = new MultipartBody.Builder()
     *                  .setType(MultipartBody.FORM)
     *                  .addFormDataPart("title", "Square Logo")
     *                  .addFormDataPart("image", "logo-square.png",RequestBody.create(MT.MEDIA_TYPE_PNG, new File("logo-square.png")))
     *                  .build();
     * </p>
     */
    PostBuilder params(MultipartBody multipartBody);

    /**
     * 上传监听
     */
    PostBuilder uploadListener(ProgressRequestBody.UpLoadProgressListener listener);

    /**
     * 下载监听
     */
    PostBuilder downloadListerner(ProgressResponseBody.DownloadProgressListener listener);

    /**
     * 在当前线程执行
     */
    Result execute();

    /**
     * 在子线程执行
     */
    void enqueue(final HttpCallBack callBack);
}
