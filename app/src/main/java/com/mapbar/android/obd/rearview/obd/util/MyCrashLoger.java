package com.mapbar.android.obd.rearview.obd.util;

import android.os.Environment;
import android.util.Log;

import com.mapbar.android.obd.rearview.obd.Application;
import com.umeng.analytics.MobclickAgent;

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
        //打印异常
        exception.printStackTrace();
        //上报umeng
        MobclickAgent.reportError(Application.getInstance(), exception);
        //保存到本地日志
        String str = getExceptionString(exception);
        android.util.Log.e(TAG, "## uncaughtException: " + str);
        logException(str);
    }

    private static void logException(String var3) {
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
