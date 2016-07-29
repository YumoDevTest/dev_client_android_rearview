package com.mapbar.android.obd.rearview.framework.manager;

import android.os.Handler;

import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.control.SDKListenerManager;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.obd.Firmware;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCenterError;

import java.util.HashMap;

/**
 * sdk 管理类，接收sdk所有事件
 */
public class OBDManager {
    /**
     * 登录成功，开始业务
     */
    public static final int EVENT_OBD_USER_LOGIN_SUCC = 0xF40001;
    /**
     * 登录失败,参数o是QRInfo，收到此事件，需要根据QRInfo中url生成二维码并弹出
     */
    public static final int EVENT_OBD_USER_LOGIN_FAILED = 0xF40002;
    /**
     * 收到此事件说明微信注册成功,需要做的只有关闭二维码
     */
    public static final int EVENT_OBD_USER_REGISTER_SUCC = 0xF40003;
    public static final int EVENT_OBD_USER_REGISTER_FAILED = 0xF4004;
    /**
     * aimi设置用户信息
     */
    public static final int EVENT_OBD_AIMI_SET_USER_DATA = 0xF4010;
    public static final int EVENT_OBD_USER_BINDVIN_SUCC = 0xF4005;
    public static final int EVENT_OBD_USER_BINDVIN_FAILED = 0xF4006;
    /**
     * 有新的固件
     */
    public static final int EVENT_OBD_OTA_HAS_NEWFIRMEWARE = 0xF4007;
    public static final int EVENT_OBD_OTA_NEED_VIN = 0xF4008;
    public static final int EVENT_OBD_OTA_SCANVIN_SUCC = 0xF4009;
    /**
     * token失效
     */
    public static final int EVENT_OBD_TOKEN_LOSE = 0xF4011;
    protected static OBDListener baseObdListener;
    private static HashMap<Class<? extends OBDManager>, OBDManager> map;
    private static OBDManager obdManager;
    public SDKListenerManager.SDKListener sdkListener;
    protected String reg_info = "请扫描填写信息，绑定激活后才能使用汽车卫士功能\n如：爱车体检、故障预警、语音升窗落锁等";
    protected String scan_succ = "扫描成功\n请等待绑定激活成功";
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

        boolean isTokenInvalid = tokenInvalid(event, o);
        if (isTokenInvalid) {
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, " -->> token失效，事件为" + event);
            }
        }
        if (isTokenInvalid && baseObdListener != null) {

            baseObdListener.onEvent(EVENT_OBD_TOKEN_LOSE, null);
        }
        if (baseObdListener != null) {
            baseObdListener.onEvent(event, o);
        }
    }

    /**
     * 检测token是否失效
     *
     * @param event
     * @param o
     * @return true为失效 false为没有失效
     */
    private boolean tokenInvalid(int event, Object o) {
        if (o != null && o instanceof Firmware.EventData) {
            Firmware.EventData eventData = (Firmware.EventData) o;
            if (Configs.TOKEN_INVALID == eventData.getRspCode()) {
                return true;
            }
        }
        if (o != null && o instanceof UserCenterError) {
            UserCenterError erro = (UserCenterError) o;
            if (erro.errorType == 2 && erro.errorCode == 1401) {
                return true;
            }
        }
        if (event == Manager.Event.queryCarFailed) {
            int errorCode = (int) o;
            if (errorCode == Manager.CarInfoResponseErr.unauthorized || errorCode == Manager.CarInfoResponseErr.notLogined) {
                return true;
            }
        }
        if (event == Manager.Event.commitLogFailed) {
            return true;
        }
        return false;
    }

    /**
     * SDK回调给用户的事件
     */
    public interface OBDListener {
        void onEvent(int event, Object o);
    }
}
