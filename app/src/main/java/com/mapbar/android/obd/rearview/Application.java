package com.mapbar.android.obd.rearview;

import com.mapbar.android.obd.rearview.framework.common.Global;


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
    }
}
