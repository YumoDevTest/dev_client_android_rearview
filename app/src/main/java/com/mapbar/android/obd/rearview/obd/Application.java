package com.mapbar.android.obd.rearview.obd;

import com.ixintui.pushsdk.PushSdkApi;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;


/**
 * Created by yun on 16/1/7.
 */
public class Application extends android.app.Application {
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
        Global.setAppContext(this);
//注册爱心推
        PushSdkApi.register(this, AixintuiConfigs.AIXINTUI_APPKEY, Utils.getChannel(this), Utils.getVersion(this) + "");
    }
}
