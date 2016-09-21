package com.mapbar.obd.foundation.log;

import android.text.TextUtils;

/**
 * 日志操作类
 * Created by zhangyunfei on 16/7/21.
 */
public class LogUtil {

    public static boolean isEnable = true;//是否开启日志
    //    private static LogPersenter logPersenter = new FileLogPersentoer();
    private static LogPersenter logPersenter = new TomCatPersenter();

    public static void d(String tag, String msg) {
        if (!isEnable) return;
        if (TextUtils.isEmpty(msg)) return;
        logPersenter.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (!isEnable) return;
        if (TextUtils.isEmpty(msg)) return;
        logPersenter.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (!isEnable) return;
        if (TextUtils.isEmpty(msg)) return;
        logPersenter.e(tag, msg);
    }

    public static void e(String tag, String msg, Exception ex) {
        if (!isEnable) return;
        if (TextUtils.isEmpty(msg)) return;
        logPersenter.e(tag, msg, ex);
    }


    private interface LogPersenter {
        public void d(String tag, String msg);

        public void i(String tag, String msg);

        public void e(String tag, String msg);

        public void e(String tag, String msg, Exception ex);
    }

    private static class TomCatPersenter implements LogPersenter {

        @Override
        public void d(String tag, String msg) {
            android.util.Log.d(tag, msg);
        }

        @Override
        public void i(String tag, String msg) {
            android.util.Log.i(tag, msg);
        }

        @Override
        public void e(String tag, String msg) {
            android.util.Log.e(tag, msg);
        }

        @Override
        public void e(String tag, String msg, Exception ex) {
            android.util.Log.e(tag, msg);
        }
    }

//    private static class FileLogPersentoer implements LogPersenter {
//        public void d(String tag, String msg) {
//            if (!isEnable) return;
//
//            if (Log.isLoggable(LogTag.OBD, Log.DEBUG))
//                Log.d(LogTag.OBD, msg);
//        }
//
//        public void i(String tag, String msg) {
//            if (!isEnable) return;
//
//            if (Log.isLoggable(LogTag.OBD, Log.INFO))
//                Log.i(LogTag.OBD, msg);
//        }
//
//        public void e(String tag, String msg) {
//            if (!isEnable) return;
//
//            if (Log.isLoggable(LogTag.OBD, Log.ERROR))
//                Log.e(LogTag.OBD, msg);
//        }
//
//        public void e(String tag, String msg, Exception ex) {
//            if (!isEnable) return;
//
//            if (Log.isLoggable(LogTag.OBD, Log.ERROR))
//                Log.e(LogTag.OBD, msg, ex);
//        }
//    }
}
