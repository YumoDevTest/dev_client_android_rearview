package com.mapbar.android.obd.rearview.lib.base;

import com.mapbar.android.obd.rearview.modules.common.OBDSDKListenerManager;

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
public abstract class BaseEventDispatcher<CALLBACK extends Object> {
    private OBDSDKListenerManager.SDKListener sdkListener;
    private CALLBACK callback;

    public BaseEventDispatcher(CALLBACK callback) {
        this.callback = callback;
    }

    public final void start() {
        if (sdkListener != null)
            return;
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                raiseOnSDKEvent(event, o);
            }
        };
        OBDSDKListenerManager.getInstance().addSdkListener(sdkListener);

    }

    /**
     * 接收sdk消息逻辑
     *
     * @param event
     * @param o
     */
    protected void raiseOnSDKEvent(int event, Object o) {
        onSDKEvent(event, o, callback);
    }

    protected abstract void onSDKEvent(int event, Object o, CALLBACK callback);


    public final void stop() {
        OBDSDKListenerManager.getInstance().releaseSdkListener(sdkListener);
    }
}
