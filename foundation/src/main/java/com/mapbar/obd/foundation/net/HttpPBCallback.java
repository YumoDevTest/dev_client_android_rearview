package com.mapbar.obd.foundation.net;

/**
 * Created by zhangyunfei on 16/8/16.
 */
public interface HttpPBCallback {

    void onFailure(Exception e);

    void onSuccess(byte[] bytes2);
}