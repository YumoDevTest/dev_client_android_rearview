package com.mapbar.android.obd.rearview;

import android.app.Application;

import com.mapbar.android.obd.rearview.framework.common.Global;


/**
 * Created by yun on 16/1/7.
 */
public class OBDApplication extends Application {
    private static OBDApplication instance;

    public OBDApplication() {
        instance = this;
    }

    public static OBDApplication getInstance() {
        return OBDApplication.instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Global.setAppContext(this);
    }
}
