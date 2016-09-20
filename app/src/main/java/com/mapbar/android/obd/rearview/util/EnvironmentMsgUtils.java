package com.mapbar.android.obd.rearview.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class EnvironmentMsgUtils {
    public static LinkedHashMap<String, String> environmentMsgMap;
    private static Activity context;

    public static LinkedHashMap<String, String> getBasicMsg(Activity activity){
        context = activity;
        if(environmentMsgMap == null){
            environmentMsgMap = new LinkedHashMap<String, String>();
        }
        putScreenMsg();
        putMemoryMsg();
        putExternalMemoryMsg();
        putReaviewMsg();
        putAndroidMsg();
        putAppMsg();
        return environmentMsgMap;
    }

    /**
     * 获取屏幕分辨率和密度
     */
    public static void putScreenMsg(){
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dpi = dm.densityDpi;
        environmentMsgMap.put("宽:", width + "");
        environmentMsgMap.put("高:", height + "");
        environmentMsgMap.put("DPI:", dpi + "");
    }

    /**
     * 获取运行内存情况
     */
    public static void putMemoryMsg(){
        //获取总运行内存
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        int begin = content.indexOf(':');
        // endIndex
        int end = content.indexOf('k');
        // 截取字符串信息
        content = content.substring(begin + 1, end).trim();
        long totalMem = Long.parseLong(content) * 1024;
        environmentMsgMap.put("内存总大小:", Formatter.formatFileSize(context,totalMem));

        //获取可用内存
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        environmentMsgMap.put("内存可用大小:", Formatter.formatFileSize(context,memoryInfo.availMem));
    }
    /**
     * 获取储存情况
     */
    public static void putExternalMemoryMsg(){
        File path = Environment.getExternalStorageDirectory(); //获取SDCard根目录
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();
        environmentMsgMap.put("储存总大小:", Formatter.formatFileSize(context,totalBlocks * blockSize));
        environmentMsgMap.put("储存可用大小:", Formatter.formatFileSize(context,availableBlocks * blockSize));
    }
    /**
     * 获取固件信息
     */
    public static void putReaviewMsg(){
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        environmentMsgMap.put("IMEI:", imei);

        //获取手机型号
        String model = Build.MODEL;
        environmentMsgMap.put("型号:", model);
        //获取手机品牌
        String brand = Build.BRAND;
        environmentMsgMap.put("品牌:", brand);
    }
    /**
     * 获取Android信息
     */
    public static void putAndroidMsg(){
        String release = Build.VERSION.RELEASE;
        environmentMsgMap.put("Android版本:", release);
    }
    /**
     * 获取app信息
     */
    public static void putAppMsg(){
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo("com.mapbar.android.obd.rearview", 0);
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            environmentMsgMap.put("App版本号:", versionCode + "");
            environmentMsgMap.put("App版本名称:", versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
