package com.mapbar.android.obd.rearview.lib.net;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;

import java.lang.reflect.Type;

/**
 * Gson格式的回调
 * Created by zhangyunfei on 16/8/16.
 */
public abstract class GsonHttpCallback<T> implements HttpCallback {
    protected Gson gson = new Gson();
    private Class<T> aClass;
    private Type type;

    public GsonHttpCallback(Class<T> aClass) {
        if (aClass == null)
            throw new NullPointerException();
        this.aClass = aClass;
    }

    public GsonHttpCallback(Type type) {
        if (type == null)
            throw new NullPointerException();
        this.type = type;
    }

    @Override
    public void onSuccess(int httpCode, String data, HttpResponse httpResponse) {
        if (httpResponse.code != 200) {
            if (!onFailure(httpResponse.code, httpResponse)) {
                onFailure(httpCode, new Exception("HTTP异常，服务器响应" + httpResponse.code), httpResponse);
            }
            return;
        }
        try {
            if (aClass != null) {
                T t = gson.fromJson(data, aClass);
                onSuccess(t, httpResponse);
            } else if (type != null) {
                T t = gson.fromJson(data, type);
                onSuccess(t, httpResponse);
            }
        } catch (JsonSyntaxException jsonSyntaxException) {
            LogUtil.e("GsonHttpCallback", String.format("## GSON序列化异常,json=%s, exception=%s", data, jsonSyntaxException));
            jsonSyntaxException.printStackTrace();
            onFailure(httpCode, jsonSyntaxException, httpResponse);
        }
    }

    protected abstract void onSuccess(T t, HttpResponse httpResponse);

    /**
     * 当遇到 msg != 200 的时候，处理失败情形
     *
     * @param code
     * @param e
     * @param httpResponse
     * @return
     */
    protected boolean onFailure(int code, HttpResponse httpResponse) {
        return false;
    }

    @Override
    public void onFailure(int httpCode, Exception e, HttpResponse httpResponse) {
        LogUtil.e("HTTP", e.getMessage(), e);
        DefalutHttpExceptionHandler.handleException(httpCode, e, httpResponse);
    }


}