package com.mapbar.android.obd.rearview.lib.net;

import com.google.gson.Gson;
import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;
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
        if (aClass == null)
            throw new NullPointerException();
        this.type = type;
    }

    @Override
    public void onSuccess(String data, HttpResponse httpResponse) {
        if (httpResponse.code != 200) {
            if (!onFailure(httpResponse.code, httpResponse)) {
                onFailure(new Exception("HTTP异常，服务器响应" + httpResponse.code), httpResponse);
            }
            return;
        }
        if (aClass != null) {
            T t = gson.fromJson(data, aClass);
            onSuccess(t, httpResponse);
        } else if (type != null) {
            T t = gson.fromJson(data, type);
            onSuccess(t, httpResponse);
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
    public void onFailure(Exception e, HttpResponse httpResponse) {
        LogUtil.e("HTTP", e.getMessage(), e);
        DefalutExceptionHandler.handleException(e, httpResponse);
    }


}