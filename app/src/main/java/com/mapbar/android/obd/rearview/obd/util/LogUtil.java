package com.mapbar.android.obd.rearview.obd.util;

import com.mapbar.android.obd.rearview.BuildConfig;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;

/**
 * 日志操作类
 * Created by zhangyunfei on 16/7/21.
 */
public class LogUtil {

    public static boolean isEnable = BuildConfig.DEBUG;//是否开启日志

    public static void d(String tag, String msg) {
        if (!isEnable) return;

        if (Log.isLoggable(LogTag.OBD, Log.DEBUG))
            Log.d(LogTag.OBD, msg);
    }

    public static void i(String tag, String msg) {
        if (!isEnable) return;

        if (Log.isLoggable(LogTag.OBD, Log.INFO))
            Log.i(LogTag.OBD, msg);
    }

    public static void e(String tag, String msg) {
        if (!isEnable) return;

        if (Log.isLoggable(LogTag.OBD, Log.ERROR))
            Log.e(LogTag.OBD, msg);
    }

    public static void e(String tag, String msg, Exception ex) {
        if (!isEnable) return;

        if (Log.isLoggable(LogTag.OBD, Log.ERROR))
            Log.e(LogTag.OBD, msg, ex);
    }
}
