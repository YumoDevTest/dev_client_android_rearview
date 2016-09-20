package com.mapbar.android.obd.rearview.util;

import com.mapbar.android.obd.rearview.lib.services.OBDSDKListenerManager;

/**
 * 建立和obd sdk的连接，注册回调
 * Created by zhangyunfei on 16/8/5.
 */
public class SDKConnection {
    private boolean isConnected = false;
    private Object loker = new Object();
    private OBDSDKListenerManager.SDKListener listener;

    public SDKConnection startConnectSDK(OBDSDKListenerManager.SDKListener sdkListener) {
        if (isConnected)
            return null;
        synchronized (loker) {
            if (isConnected)//双检查
                return null;
            this.listener = sdkListener;
            OBDSDKListenerManager.getInstance().addSdkListener(listener);
            isConnected = true;
            return this;
        }
    }

    public SDKConnection stopConnectSDK() {
        if (!isConnected)
            return null;
        synchronized (loker) {
            if (!isConnected)//双检查
                return null;
            if (listener != null)
                OBDSDKListenerManager.getInstance().releaseSdkListener(listener);
            isConnected = false;
            listener = null;
            return this;
        }
    }


}