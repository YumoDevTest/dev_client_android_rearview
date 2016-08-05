package com.mapbar.android.obd.rearview.obd;

import android.content.Intent;

import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.control.OBDV3HService;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.mapdal.NativeEnv;
import com.mapbar.obd.ExtraTripInfo;
import com.mapbar.obd.Manager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by liuyy on 2016/5/13.
 */
public class OBDSDKListenerManager {


    private static OBDSDKListenerManager sdkListenerManager;
    private Manager.Listener listener;
    private ArrayList<WeakReference<SDKListener>> regListeners;
    private boolean flag_token = false;//token失效标记，防止同时多次触发注销到登录页

    private OBDSDKListenerManager() {
    }

    public static OBDSDKListenerManager getInstance() {
        if (sdkListenerManager == null) {
            sdkListenerManager = new OBDSDKListenerManager();
        }
        return sdkListenerManager;
    }


    public void init() {
        regListeners = new ArrayList<>();
        OBDManager.OBDListener obdListener = new OBDManager.OBDListener() {
            @Override
            public void onEvent(int event, Object o) {
                {
                    Log.e("rrrrrrr", event + "");

                    // 日志
                    if (Log.isLoggable(LogTag.FRAMEWORK, Log.VERBOSE)) {
                        Log.v(LogTag.FRAMEWORK, "sdk -->> event:" + event);
                        Log.v(LogTag.FRAMEWORK, "sdk -->> regListeners.size():" + regListeners.size());
                    }
                    if (regListeners.size() > 0) {
                        for (int i = regListeners.size() - 1; i >= 0 && i < regListeners.size(); i--) {
                            if (regListeners.get(i) == null || regListeners.get(i).get() == null) {
                                regListeners.remove(i);
                            } else {
                                if (regListeners.get(i).get().isActive()) {
                                    regListeners.get(i).get().onEvent(event, o);
                                }
                            }
                        }
                    }
                }
            }
        };
        OBDManager.init(obdListener);
//        //// TODO: tianff 2016/7/26 SDKListenerManager init 解决后台服务
        try {

            ExtraTripInfo exInfo = new ExtraTripInfo("0", "autoguardapp");
            Manager.getInstance().setExtraTripInfo(exInfo);
            exInfo = Manager.getInstance().getExtraTripInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (NativeEnv.isServiceRunning(OBDV3HService.class.getName())) {
            Intent i = new Intent(OBDV3HService.ACTION_COMPACT_SERVICE);

            i.putExtra(OBDV3HService.EXTRA_AUTO_RESTART, false);
            i.putExtra(OBDV3HService.EXTRA_WAIT_FOR_SIGNAL, true);
            i.putExtra(OBDV3HService.EXTRA_NEED_CONNECT, false);

            boolean cName = Global.getAppContext().stopService(i);
            android.util.Log.e("[OBDBusiness]", "-------------------" + cName + "-------------------");

        }
    }

    /**
     * 设置一个监听SDK回调的listener
     *
     * @param listener
     */
    public void setSdkListener(SDKListener listener) {
        WeakReference<SDKListener> weakListener = new WeakReference<>(listener);
        regListeners.add(weakListener);
    }

    /**
     * 解除SDK回调监听
     *
     * @param listener
     */
    public void registSdkListener(SDKListener listener) {
        listener.setIsReged(true);
    }

    /**
     * 解除SDK回调监听
     *
     * @param listener
     */
    public void releaseSdkListener(SDKListener listener) {
        listener.setIsReged(false);
    }

    public void stopListener(SDKListener listener) {
        // 日志
        if (Log.isLoggable(LogTag.FRAMEWORK, Log.DEBUG)) {
            Log.d(LogTag.FRAMEWORK, "stopListener -->> ");
        }
        if (listener != null)
            listener.active = false;
    }

    public void startListener(SDKListener listener) {
        // 日志
        if (Log.isLoggable(LogTag.FRAMEWORK, Log.DEBUG)) {
            Log.d(LogTag.FRAMEWORK, "startListener -->> ");
        }
        if (listener != null)
            listener.active = true;
    }

    public void clearListener() {
        if (regListeners != null) {
            regListeners.clear();
        }
    }

    public void setFlag_token(boolean flag_token) {
        this.flag_token = flag_token;
    }

    public static abstract class SDKListener {
        /**
         * false ，listener被销毁；true，listener存在
         */
        boolean isReged = true;

        /**
         * false，listener不生效；true listener生肖
         */
        boolean active = true;

        public void onEvent(int event, Object o) {

//            if (o != null && o instanceof ObdSDKResult) {
//                ObdSDKResult obdSDKResult = (ObdSDKResult) o;
//                if ((Constants.USER_INVALID == obdSDKResult.code || Constants.TOKEN_INVALID == obdSDKResult.code) && !sdkListenerManager.flag_token && !PageManager.getInstance().getCurrentPageName().equals(LoginPage.class.getName())
//                        ) {
//                    sdkListenerManager.flag_token = true;
//
//                    PageManager.getInstance().goPage(LoginPage.class);
//                    return;
//                }
//            }
        }

        protected boolean isReged() {
            return isReged;
        }

        protected void setIsReged(boolean isReged) {
            this.isReged = isReged;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
