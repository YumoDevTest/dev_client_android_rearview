package com.mapbar.obd.foundation.net;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * http 上下文
 * Created by zhangyunfei on 16/8/9.
 */
public class MyHttpContext {
    private static OkHttpClient mOkHttpClient;
    private static String token;

    public static OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (MyHttpContext.class) {
                if (mOkHttpClient == null) {
                    mOkHttpClient = createOkHttpClient();
                }
            }
        }
        return mOkHttpClient;
    }

    private static OkHttpClient createOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(3000, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(3000, TimeUnit.MILLISECONDS);
        return okHttpClient;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        MyHttpContext.token = token;
    }
}
