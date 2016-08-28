package com.mapbar.android.obd.rearview.lib.net;

/**
 * Created by zhangyunfei on 16/8/16.
 */
public interface HttpCallback {

    void onFailure(Exception e, HttpResponse httpResponse);

    void onSuccess(String data, HttpResponse httpResponse);
}