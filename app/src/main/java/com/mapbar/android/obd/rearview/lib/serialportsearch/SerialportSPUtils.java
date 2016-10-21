package com.mapbar.android.obd.rearview.lib.serialportsearch;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhangyh on 2016/10/18.
 * 保存查询到的串口的工具类
 */

public class SerialportSPUtils {

    private static SharedPreferences sp;

    public static String getSerialport(Context context) {
        sp = context.getSharedPreferences("SerialPortName", Context.MODE_PRIVATE);
        String serialport = sp.getString("serialport", "");
        return serialport;
    }

    public static void setSerialport(Context context, String serialport) {
        if (sp == null) {
            sp = context.getSharedPreferences("SerialPortName", Context.MODE_PRIVATE);
        }
        sp.edit().putString("serialport", serialport).commit();
    }
}
