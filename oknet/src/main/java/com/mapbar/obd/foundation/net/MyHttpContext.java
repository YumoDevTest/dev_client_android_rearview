package com.mapbar.obd.foundation.net;

import com.mapbar.obd.foundation.log.LogUtil;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * http 上下文
 * Created by zhangyunfei on 16/8/9.
 */
public class MyHttpContext {
    private static final String TAG = "HTTP";
    private static OkHttpClient mOkHttpClient;
    private static HttpExceptionHandler defaultExceptionHandler;
    private static RequestIntercepter requestIntercepter;

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

    /**
     * 默认的异常处理器
     * @return HttpExceptionHandler
     */
    public static HttpExceptionHandler getDefaultExceptionHandler() {
        return defaultExceptionHandler;
    }

    /**
     * 设置默认的异常处理器
     * @param defaultExceptionHandler defaultExceptionHandler
     */
    public static void setDefaultExceptionHandler(HttpExceptionHandler defaultExceptionHandler) {
        MyHttpContext.defaultExceptionHandler = defaultExceptionHandler;
    }


    public static void print(String msg) {
        LogUtil.d(TAG, msg);
    }

    public static void print(String msg, Exception e) {
        LogUtil.e(TAG, msg, e);
    }

    /**
     * 获得 请求拦截器
     * @return RequestIntercepter
     */
    public static RequestIntercepter getRequestIntercepter() {
        return requestIntercepter;
    }

    /**
     * 设置 请求拦截器
     * @param requestIntercepter requestIntercepter
     */
    public static void setRequestIntercepter(RequestIntercepter requestIntercepter) {
        MyHttpContext.requestIntercepter = requestIntercepter;
    }
}
