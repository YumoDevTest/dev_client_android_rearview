package com.mapbar.android.obd.rearview.framework.common;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;

import java.text.DecimalFormat;

/**
 * Created by THINKPAD on 2016/3/22.
 */
public class Utils {
    public static final String DEFAULT_CHANNEL_NAME = "unkonwn";
    private static final String format3dot2 = "##0.00";
    private static final String format2dot1 = "#0.0";
    private static final String format2dot2 = "#0.00";
    private static final String format00dot1 = "00.0";
    private static final String format000 = "000";
    private static String channel;

    public static String format3dot2(float paramFloat) {
        return new DecimalFormat(format3dot2).format(paramFloat);
    }

    public static String format2dot1(float paramFloat) {
        return new DecimalFormat(format2dot1).format(paramFloat);
    }

    public static String format2dot2(float paramFloat) {
        return new DecimalFormat(format2dot2).format(paramFloat);
    }

    public static String format00dot1(float paramFloat) {
        return new DecimalFormat(format00dot1).format(paramFloat);
    }

    public static String format000(int paramInt) {
        return new DecimalFormat(format000).format(paramInt);
    }

    /**
     * 检查当前网络是否可用
     *
     * @param activity
     * @return
     */
    public static boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 日志
                    if (Log.isLoggable(LogTag.TEMP, Log.DEBUG)) {
                        Log.d(LogTag.TEMP, "   ===状态===:" + networkInfo[i].getState() + "  ===类型===:" + networkInfo[i].getTypeName());
                    }
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 获取渠道号
     *
     * @param context
     * @return
     */
    public static String getChannel(Context context) {
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            channel = String.valueOf(appInfo.metaData.get("UMENG_CHANNEL").toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(channel)) {
            channel = DEFAULT_CHANNEL_NAME;
        }
        return channel;
    }


    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersion(Context mContext) {

        int versionCode = 0;
        String packageName = mContext.getPackageName();
        try {
            versionCode = mContext.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    public static String getImei(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String deviceId = tm.getDeviceId();
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, "IMEI -->> " + deviceId);
        }
//        return deviceId;
//        return "jjsadhfjksd5452dr4g3$$$$$$$$$$$$$$$";//6.17王龙测试填写\
//        return "7772qqqqqqqqqqqqqqqqqqqqqqqqqweishite";//此号已提供其他厂商
        return "777296%%%%hh01739!55555555";//外网注册通过
//        return "77726%%fsdffsdfssdfsdfsdfsd";//测试专用，不可外传1；
//        return "77726%%fsdffsdfssdfsdfsd%%%%%%";//测试专用，不可外传2；

    }
}
