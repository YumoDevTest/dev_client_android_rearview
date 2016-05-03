package com.mapbar.android.obd.rearview.framework.common;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;

import java.text.DecimalFormat;

/**
 * Created by THINKPAD on 2016/3/22.
 */
public class Utils {
    private static final String format3dot2 = "##0.00";
    private static final String format2dot1 = "#0.0";
    private static final String format2dot2 = "#0.00";
    private static final String format00dot1 = "00.0";
    private static final String format000 = "000";

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
}
