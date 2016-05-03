package com.mapbar.android.obd.rearview.framework;

import android.os.Environment;

public class Configs {

    //
    public final static String FILE_PATH = "/mapbar/obd_gsm";
    public final static String SHARE_PATH = Environment.getExternalStorageDirectory() + Configs.FILE_PATH;


    /**
     * 在com.mapbar.obd.Config.DEBUG = false时，此开关无效。只用于专项测试全数据
     */
    public static boolean AlwaysOBDDetailMode = false;
}
