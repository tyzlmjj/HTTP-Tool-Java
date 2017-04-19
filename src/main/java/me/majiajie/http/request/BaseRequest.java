package me.majiajie.http.request;


import io.reactivex.Observable;
import me.majiajie.http.MT;
import me.majiajie.http.callback.HttpCallBack;
import me.majiajie.http.progress.ProgressHelper;
import me.majiajie.http.progress.ProgressRequestBody;
import me.majiajie.http.progress.ProgressResponseBody;
import me.majiajie.http.utils.HttpStatusCode;
import me.majiajie.http.utils.Utils;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BaseRequest
{
    private String mUrl;

    private OkHttpClient mOkHttpClient;

    private RequestType mRequestType;

    public BaseRequest(String url, OkHttpClient okHttpClient, RequestType requestType) {
        mUrl = url;
        mOkHttpClient = okHttpClient;
        mRequestType = requestType;
    }

    public Builder newBuilder()
    {
        return new Builder();
    }

    public class Builder implements GetBuilder,PostBuilder
    {

        Request.Builder requestBuild;

        RequestBody requestBody;

        ProgressRequestBody.UpLoadProgressListener uploadListener;

        ProgressResponseBody.DownloadProgressListener downloadListener;

        long connectTimeout = 0;

        long readTimeout = 0;

        long writeTimeout = 0;

        Builder()
        {
            requestBuild = new Request.Builder();
            requestBuild.url(mUrl);

            requestBody = RequestBody.create(null, new byte[0]);
        }

        @Override
        public Builder addHeader(String name, String value) {
            requestBuild.addHeader(name,value);
            return this;
        }

        @Override
        public Builder headers(Map<String, String> headers) {
            requestBuild.headers(Headers.of(headers));
            return this;
        }

        @Override
        public Builder params(Map<String, String> params) {
            requestBody = RequestBody.create(MT.MEDIA_TYPE_DEFUALT, Utils.encodeParameters(params,"UTF-8"));
            return this;
        }

        @Override
        public Builder params(String params) {
            requestBody = RequestBody.create(MT.MEDIA_TYPE_DEFUALT,params);
            return this;
        }

        @Override
        public Builder params(byte[] params) {
            requestBody = RequestBody.create(MT.MEDIA_TYPE_DEFUALT,params);
            return this;
        }

        @Override
        public Builder params(MediaType mediaType, String params) {
            requestBody = RequestBody.create(mediaType,params);
            return this;
        }

        @Override
        public Builder params(MultipartBody multipartBody) {
            requestBody = multipartBody;
            return this;
        }

        @Override
        public Builder uploadListener(ProgressRequestBody.UpLoadProgressListener listener) {
            uploadListener = listener;
            return this;
        }

        @Override
        public Builder downloadListerner(ProgressResponseBody.DownloadProgressListener listener) {
            downloadListener = listener;
            return this;
        }

        @Override
        public Builder connectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        @Override
        public Builder readTimeout(long readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        @Override
        public Builder writeTimeout(long writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        @Override
        public Observable<Response> execute() {

            warp();

            return Observable.create(emitter -> {
                Response response = mOkHttpClient.newCall(requestBuild.build()).execute();
                emitter.onNext(response);
                emitter.onComplete();
            });
        }

        @Override
        public void enqueue(HttpCallBack callBack) {

            warp();

            mOkHttpClient.newCall(requestBuild.build()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    if(call.isCanceled())
                    {
                        callBack.onError(-2,"请求被取消",null);
                    }
                    else{
                        callBack.onError(-1,"请求失败",null);
                    }
                }

                @Override
                public void onResponse(Call call, Response response){
                    if(response.isSuccessful())
                    {
                        callBack.onResponse(response);
                    }
                    else
                    {
                        callBack.onError(response.code(), HttpStatusCode.getMsgByCode(response.code()),response);
                    }
                }
            });
        }

        private void warp()
        {
            warpListener();

            warpRequest();

            warpClient();
        }

        private void warpListener()
        {
            if(uploadListener != null)
            {
                requestBody = ProgressHelper.addProgressRequestListener(requestBody,uploadListener);
            }

            if(downloadListener != null)
            {
                mOkHttpClient = ProgressHelper.addProgressResponseListener(mOkHttpClient,downloadListener);
            }
        }

        private void warpRequest()
        {
            switch (mRequestType)
            {
                case POST:
                    requestBuild.post(requestBody);
                    break;
                case PUT:
                    requestBuild.put(requestBody);
                    break;
                case DELETE:
                    requestBuild.delete(requestBody);
                    break;
            }
        }

        private void warpClient()
        {
            if( connectTimeout > 0 || readTimeout > 0 || writeTimeout > 0)
            {
                mOkHttpClient = mOkHttpClient.newBuilder()
                    .connectTimeout(connectTimeout !=0 ?connectTimeout: 10_000, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout !=0 ?readTimeout:10_000, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeout !=0 ?writeTimeout:10_000, TimeUnit.MILLISECONDS)
                    .build();
            }
        }
    }
}
