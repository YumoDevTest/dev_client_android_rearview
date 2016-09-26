package com.mapbar.obd.foundation.log;

/**
 * Created by zhangyunfei on 16/9/23.
 */
public interface Logger {

    public void d(String tag, String msg);

    public void i(String tag, String msg);

    public void e(String tag, String msg);

    public void e(String tag, String msg, Exception ex);
}