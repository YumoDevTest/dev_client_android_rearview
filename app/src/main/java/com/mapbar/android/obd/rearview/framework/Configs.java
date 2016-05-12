package com.mapbar.android.obd.rearview.framework;

import android.os.Environment;

public class Configs {

    // OBD产品标识
    public final static String OBD_ANDROID = "obd_android";
    public final static String FILE_PATH = "/mapbar/obd";
    public final static String SHARE_PATH = Environment.getExternalStorageDirectory() + Configs.FILE_PATH;


    /**
     * 在com.mapbar.obd.Config.DEBUG = false时，此开关无效。只用于专项测试全数据
     */
    public static boolean AlwaysOBDDetailMode = false;
}
