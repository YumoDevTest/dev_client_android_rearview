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

import com.mapbar.android.obd.rearview.BuildConfig;
import com.mapbar.android.obd.rearview.lib.config.Urls;
import com.mapbar.obd.foundation.log.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * 环境信息 打印
 * Created by Administrator on 2016/9/20 0020.
 */
public class EnvironmentInfoUtils {
    private static final String TAG = "EnvironmentInfoUtils";

    /**
     * 获得基本信息
     *
     * @param context
     * @return
     */
    public static HashMap<String, String> getBasicMsg(Activity context) {
        if (context == null)
            throw new NullPointerException();
        HashMap<String, String> environmentMsgMap = new LinkedHashMap<>();
        putScreenMsg(environmentMsgMap, context);
        putMemoryMsg(environmentMsgMap, context);
        putExternalMemoryMsg(environmentMsgMap, context);
        putReaviewMsg(environmentMsgMap, context);
        putAndroidMsg(environmentMsgMap, context);
        putAppMsg(environmentMsgMap, context);
        putDebugMsg(environmentMsgMap, context);
        putUrlsMsg(environmentMsgMap, context);
        return environmentMsgMap;
    }

    /**
     * 打印
     */
    public static void print(Activity context) {
        if (context == null)
            throw new NullPointerException();
        HashMap<String, String> basicMsg = getBasicMsg(context);
        Set<String> keys = basicMsg.keySet();
        LogUtil.d(TAG, "##############################");
        for (String key : keys) {
            LogUtil.d(TAG, String.format("## %s: %s", key, basicMsg.get(key)));
        }
        LogUtil.d(TAG, "##############################");
    }

    /**
     * 获取屏幕分辨率和密度
     */
    private static void putScreenMsg(HashMap<String, String> environmentMsgMap, Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dpi = dm.densityDpi;
        environmentMsgMap.put("屏幕宽度", width + "");
        environmentMsgMap.put("屏幕高度", height + "");
        environmentMsgMap.put("屏幕DPI", dpi + "");
        environmentMsgMap.put("屏幕密度", dm.density + "");
    }

    /**
     * 获取运行内存情况
     */
    private static void putMemoryMsg(HashMap<String, String> environmentMsgMap, Activity context) {
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
        environmentMsgMap.put("内存总大小", Formatter.formatFileSize(context, totalMem));

        //获取可用内存
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        environmentMsgMap.put("内存可用大小", Formatter.formatFileSize(context, memoryInfo.availMem));
    }

    /**
     * 获取储存情况
     */
    private static void putExternalMemoryMsg(HashMap<String, String> environmentMsgMap, Activity context) {
        File path = Environment.getExternalStorageDirectory(); //获取SDCard根目录
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();
        environmentMsgMap.put("储存总大小", Formatter.formatFileSize(context, totalBlocks * blockSize));
        environmentMsgMap.put("储存可用大小", Formatter.formatFileSize(context, availableBlocks * blockSize));
    }

    /**
     * 获取固件信息
     */
    private static void putReaviewMsg(HashMap<String, String> environmentMsgMap, Activity context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        environmentMsgMap.put("手机IMEI", imei);

        //获取手机型号
        String model = Build.MODEL;
        environmentMsgMap.put("手机型号", model);
        //获取手机品牌
        String brand = Build.BRAND;
        environmentMsgMap.put("手机品牌", brand);
    }

    /**
     * 获取Android信息
     */
    private static void putAndroidMsg(HashMap<String, String> environmentMsgMap, Activity context) {
        String release = Build.VERSION.RELEASE;
        environmentMsgMap.put("Android系统版本", release);
    }

    /**
     * 获取app信息
     */
    private static void putAppMsg(HashMap<String, String> environmentMsgMap, Activity context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo("com.mapbar.android.obd.rearview", 0);
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            environmentMsgMap.put("当前App版本号", versionCode + "");
            environmentMsgMap.put("当前App版本名称", versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取gradle中debug配置参数
     */
    private static void putDebugMsg(HashMap<String, String> environmentMsgMap, Activity context) {
        environmentMsgMap.put("串口号", BuildConfig.SERIALPORT_PATH);
        environmentMsgMap.put("是否伪造IMEI", BuildConfig.IS_FAKE_IMEI + "");
        environmentMsgMap.put("伪造的IMEI", BuildConfig.FAKE_IMEI);
        environmentMsgMap.put("是否开启权限管理", BuildConfig.IS_ENABLE_PERMISSION + "");
        environmentMsgMap.put("是否开启首页提示", BuildConfig.IS_ENABLE_ALERM_ON_MAIN_PAGE + "");
        environmentMsgMap.put("是否开启模拟控制页", BuildConfig.IS_ENABLE_TEST_CAR_DEMO + "");
        environmentMsgMap.put("是否使用内网", BuildConfig.IS_USE_TEST_HOST + "");
    }

    /**
     * 获取URL信息
     */
    private static void putUrlsMsg(HashMap<String, String> environmentMsgMap, Activity context) {
        environmentMsgMap.put("权限接口地址", Urls.PERMISSION_BASE_URL);
        environmentMsgMap.put("微信服务地址", Urls.WEIXIN_BASE_URL);
        environmentMsgMap.put("OTA基础地址", Urls.OTA_BASE_URL);
    }

}
