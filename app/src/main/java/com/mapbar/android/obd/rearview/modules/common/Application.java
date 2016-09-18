package com.mapbar.android.obd.rearview.modules.common;

import com.ixintui.pushsdk.PushSdkApi;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.lib.base.CustomMadeType;
import com.mapbar.android.obd.rearview.lib.umeng.MobclickAgentEx;
import com.mapbar.obd.CrashHandler;
import com.mapbar.obd.Manager;


/**
 * Created by yun on 16/1/7.
 */
public class Application extends android.app.Application {
    private static final String TAG = "Application";
    private static Application instance;

    public Application() {
        instance = this;
    }

    public static Application getInstance() {
        return Application.instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.d(TAG, "## [application] 启动");
        CustomMadeType.printLog();

        Manager.onApplicationonCreate(this);
        //捕捉异常注册
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this, 3);
        //注册爱心推
        PushSdkApi.register(this, AixintuiConfigs.AIXINTUI_APPKEY, Utils.getChannel(this), Utils.getVersion(this) + "");
        //禁用默认页面统计
        MobclickAgentEx.openActivityDurationTrack(false);
    }

    @Override
    public void onTerminate() {
        android.util.Log.d(TAG, "## [application] 停止");
        // infos 当前应用如果被系统强杀则方法不会被调用
        Manager.onApplicationTerminate();
        super.onTerminate();
    }
}