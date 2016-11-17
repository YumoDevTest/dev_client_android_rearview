package com.mapbar.android.obd.rearview.lib.config;

import com.mapbar.android.obd.rearview.BuildConfig;
import com.mapbar.android.obd.rearview.lib.config.CustomMadeType;

/**
 * Created by THINKPAD on 2016/2/23.
 */
public class Constants {
    /**
     * Mode of the edition, DEBUG or RELEASE
     */
    public static final boolean DEBUG = false;
    /**
     * 海外版标识
     */
    public final static boolean IS_OVERSEAS_EDITION = false;

//    public static final String SERIALPORT_PATH = BuildConfig.SERIALPORT_PATH;//"/dev/ttys1";//"/dev/ttyMT1";
  /**
     * 后台服务行程时默认的的channel名称
     */
    public static final String COMAPCT_SERVICE_CHANNEL_NAME = "ObdCompactService";

    /**
     * 定制模式。通用版，或者定制版。
     * ---迪瑞特专版特点：开机延迟10分钟自启动app
     */
//    public static final int CUSTOM_MADE_TYPE = CustomMadeType.CUSTOM_MADE_TYPE_COMMON;
    public static final int CUSTOM_MADE_TYPE = CustomMadeType.CUSTOM_MADE_TYPE_COMMON;

    /**
     * 是否广播跳转进入程序,
     * 体检广播进入的话直接显示体检界面
     * 广播进入0,正常进入1
     */
    public static int ISRECEIVER = 1;
}
