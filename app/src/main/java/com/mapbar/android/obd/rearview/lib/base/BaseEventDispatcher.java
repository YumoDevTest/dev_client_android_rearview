package com.mapbar.android.obd.rearview.lib.base;

import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;

/**
 * 消息分发器。接收来自sdk层的事件消息，主要方法有
 * 启动接收消息： start()
 * 停止接收消息： stop();
 * 子类自己实现下面这个方法实现接收消息的逻辑
 * void onSDKEvent(int event, Object o)
 * <p/>
 * 实例代码参考类  TirePressureDataEventDispatcher
 * <p/>
 * Created by zhangyunfei on 16/8/18.
 */
public abstract class BaseEventDispatcher {
    private OBDSDKListenerManager.SDKListener sdkListener;


    public final void start() {
        if (sdkListener != null)
            return;
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                onSDKEvent(event, o);
            }
        };
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);

    }

    /**
     * 接收sdk消息逻辑
     *
     * @param event
     * @param o
     */
    protected abstract void onSDKEvent(int event, Object o);


    public final void stop() {
        OBDSDKListenerManager.getInstance().releaseSdkListener(sdkListener);
    }
}
