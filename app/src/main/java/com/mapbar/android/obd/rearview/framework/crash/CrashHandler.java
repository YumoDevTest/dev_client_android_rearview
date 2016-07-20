package com.mapbar.android.obd.rearview.framework.crash;

import android.content.Context;
import android.os.Environment;

import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.log.LogManager;
import com.mapbar.obd.Config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by tianff on 2016/7/20.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {


    private static CrashHandler instance;  //单例引用，这里我们做成单例的，因为我们一个应用程序里面只需要一个UncaughtExceptionHandler实例
    private Context context;

    private CrashHandler() {
    }

    public synchronized static CrashHandler getInstance() {  //同步方法，以免单例多线程环境下出现异常
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {  //初始化，把当前对象设置成UncaughtExceptionHandler处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        context = ctx;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {  //当有未处理的异常发生时，就会来到这里。。
        LogManager.getInstance().init(context);
        String logFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + Configs.FILE_PATH + "/client_Log_obdserver/";
        File file = new File(logFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss");
        String fileName = "/log_" + format.format(c.getTime())
                + ".txt";
        File logFile = new File(logFilePath + fileName);
        if (Config.DEBUG) {
            LogManager.getInstance().setLogFile(logFile);
        }
        System.exit(0);
    }

}