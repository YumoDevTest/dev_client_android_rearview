package com.mapbar.android.obd.rearview.framework.manager;

import com.mapbar.android.obd.rearview.framework.control.SDKListenerManager;

import java.util.HashMap;

/**
 * sdk 管理类，接收sdk所有事件
 */
public class OBDManager {
    protected static OBDListener baseObdListener;
    protected static SDKListenerManager.SDKListener sdkListener;
    private static HashMap<Class<? extends OBDManager>, OBDManager> map;

    public static OBDManager getInstance(Class<? extends OBDManager> clazz) {
        if (map == null) {
            map = new HashMap<>();
        }
        if (map.get(clazz) == null) {
            try {
                map.put(clazz, clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map.get(clazz);
    }

    /**
     * 设置监听SDK事件;所有的sdk事件都通过此方法接收
     *
     * @param obdListener {@link OBDListener}事件回调接口
     */
    public static void init(OBDListener obdListener) {
        SDKListenerManager.getInstance().init();
        baseObdListener = obdListener;
    }

    /**
     *sdk内部回调事件
     * @param event 事件的id
     * @param o     事件携带的数据
     */
    public void onSDKEvent(int event, Object o) {
        if (baseObdListener != null) {
            baseObdListener.onEvent(event, o);
        }
    }

    /**
     *  SDK回调给用户的事件
     */
    public interface OBDListener {
        void onEvent(int event, Object o);
    }

}
