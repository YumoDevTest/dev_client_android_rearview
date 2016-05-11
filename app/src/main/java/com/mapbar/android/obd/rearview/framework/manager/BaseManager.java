package com.mapbar.android.obd.rearview.framework.manager;

import android.content.Context;

import com.mapbar.android.obd.rearview.framework.control.SDKListenerManager;

/**
 * Created by liuyy on 2016/5/10.
 */
public class BaseManager {
    protected static OBDListener baseObdListener;
    protected SDKListenerManager.SDKListener sdkListener;

    protected BaseManager() {
        initListener();
    }

    public static void init(Context context) {
        SDKListenerManager.getInstance().init();
    }

    public static void setObdListener(OBDListener obdListener) {
        baseObdListener = obdListener;
    }

    public void initListener() {
        sdkListener = new SDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                BaseManager.this.onEvent(event, o);
            }
        };
        SDKListenerManager.getInstance().setSdkListener(sdkListener);
    }

    /**
     * SDK事件回调
     *
     * @param event 事件的id
     * @param o     事件携带的数据
     */
    public void onEvent(int event, Object o) {
    }

    /**
     * FIXME 回调给用户的事件
     */
    public interface OBDListener {
        void onEvent(int event, Object o);
    }

}
