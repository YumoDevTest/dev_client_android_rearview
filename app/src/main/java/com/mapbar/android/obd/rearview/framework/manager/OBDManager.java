package com.mapbar.android.obd.rearview.framework.manager;

import android.os.Handler;
import android.util.Log;

import com.mapbar.android.obd.rearview.framework.control.SDKListenerManager;

import java.util.HashMap;

/**
 * sdk 管理类，接收sdk所有事件
 */
public class OBDManager {
    public static final int EVENT_OBD_USER_LOGIN_SUCC = 0xF40001;//登录成功
    public static final int EVENT_OBD_USER_LOGIN_FAILED = 0xF40002;//登录失败,需要注册,弹出二维码
    public static final int EVENT_OBD_USER_REGISTER_SUCC = 0xF40003;//微信注册成功,关闭二维码
    public static final int EVENT_OBD_USER_REGISTER_FAILED = 0xF4004;
    public static final int EVENT_OBD_USER_BINDVIN_SUCC = 0xF4005;
    public static final int EVENT_OBD_USER_BINDVIN_FAILED = 0xF4006;
    public static final int EVENT_OBD_OTA_HAS_NEWFIRMEWARE = 0xF4007;
    public static final int EVENT_OBD_OTA_NEED_VIN = 0xF4008;
    protected static OBDListener baseObdListener;
    protected static int flag = -1;
    private static HashMap<Class<? extends OBDManager>, OBDManager> map;
    private static OBDManager obdManager;
    protected SDKListenerManager.SDKListener sdkListener;
    protected String reg_info = "请扫描填写信息，以获得更多汽车智能化功能\n如：远程定位、防盗提醒、远程查看车辆状态等";
    protected String scan_succ = "扫描成功\\n请等待填写完成";
    protected String reg_succ = "您已通过手机\\n成功完善爱车信息";
    protected Handler mHandler = new Handler();
    protected OBDManager() {
        Log.e("event", "构造了");
        initListener();
    }
    public static OBDManager getInstance(Class<? extends OBDManager> clazz) {
        if (obdManager == null) {
            obdManager = new OBDManager();
        }
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

    public void initListener() {
        sdkListener = new SDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                onSDKEvent(event, o);
            }
        };
        Log.e("event", "放进去了");
        SDKListenerManager.getInstance().setSdkListener(sdkListener);
    }

    /**
     * sdk内部回调事件
     *
     * @param event 事件的id
     * @param o     事件携带的数据
     */
    public void onSDKEvent(int event, Object o) {
        if (baseObdListener != null) {
            baseObdListener.onEvent(event, o);
        }
    }

    /**
     * SDK回调给用户的事件
     */
    public interface OBDListener {
        void onEvent(int event, Object o);
    }

}
