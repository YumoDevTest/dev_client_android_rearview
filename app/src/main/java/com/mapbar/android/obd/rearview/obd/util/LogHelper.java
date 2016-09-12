package com.mapbar.android.obd.rearview.obd.util;

/**
 * 输出日志
 * Created by zhangyunfei on 16/8/14.
 */
public class LogHelper {

    public static void print(String tag, String str) {
        android.util.Log.d(tag, str);
    }

    public static void d(String tag, String str) {
        android.util.Log.d(tag, OutputStringUtil.transferForPrint(str));
    }
}
