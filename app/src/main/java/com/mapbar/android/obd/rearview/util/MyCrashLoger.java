package com.mapbar.android.obd.rearview.util;

import android.content.Context;
import android.os.Environment;

import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.obd.foundation.umeng.MobclickAgentEx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 记录错误日志
 */
public class MyCrashLoger implements UncaughtExceptionHandler {
    private static final String TAG = "MyCrashLoger";

    public void uncaughtException(Thread thread1, Throwable exception) {
        try {
            com.mapbar.obd.foundation.log.LogUtil.e(TAG, "## uncaughtException: " + exception.getMessage(), (Exception) exception);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //打印异常
        exception.printStackTrace();
        Context context = MyApplication.getInstance().getMainActivity();
        if (context == null)
            context = MyApplication.getInstance();
        //上报umeng
        MobclickAgentEx.reportError(context, exception);
        //保存到本地日志
        String str = getExceptionString(exception);
        logExceptionToFile(str);
    }

    private static void logExceptionToFile(String var3) {
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mapbar/obd" + "/client_Log_obdserver/";
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdirs();
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String path = "/log_" + simpleDateFormat.format(calendar.getTime()) + ".txt";
        File file1 = new File(fileName + path);

        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(file1, true));
            writer.println(var3);
            writer.flush();
            writer.close();
            writer = null;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static String getExceptionString(Throwable var0) {
        if (var0 == null) {
            return null;
        } else {
            StringWriter var1 = new StringWriter();
            PrintWriter var2 = new PrintWriter(var1, true);
            var0.printStackTrace(var2);
            return var1.toString();
        }
    }
}
