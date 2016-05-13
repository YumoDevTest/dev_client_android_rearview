package com.mapbar.android.obd.rearview.obd;

import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
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
            //每次响应事件都要判断是否返回code=29，token失效
            /*if (o != null && o instanceof ObdSDKResult) {
                ObdSDKResult obdSDKResult = (ObdSDKResult) o;
                if ((Constants.USER_INVALID == obdSDKResult.code || Constants.TOKEN_INVALID == obdSDKResult.code) && !sdkListenerManager.flag_token && !PageManager.getInstance().getCurrentPageName().equals(LoginPage.class.getName())
                        ) {
                    sdkListenerManager.flag_token = true;
                    PageManager.getInstance().finishAll();
                    PageManager.getInstance().goPage(LoginPage.class);
                    return;
                }
            }*/
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