package com.mapbar.android.obd.rearview.lib.net;

/**
 * Created by zhangyunfei on 16/8/16.
 */
public interface HttpCallback {

    void onFailure(int httpCode, Exception e, HttpResponse httpResponse);

    void onSuccess(int httpCode, String data, HttpResponse httpResponse);
}