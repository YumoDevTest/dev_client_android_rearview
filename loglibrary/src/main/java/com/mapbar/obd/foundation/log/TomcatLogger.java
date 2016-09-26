package com.mapbar.obd.foundation.log;

/**
 * Created by zhangyunfei on 16/9/23.
 */
public class TomcatLogger implements Logger {

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