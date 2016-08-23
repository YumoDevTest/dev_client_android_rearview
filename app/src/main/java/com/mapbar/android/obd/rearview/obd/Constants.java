package com.mapbar.android.obd.rearview.obd;

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

    //    public static final String SERIALPORT_PATH = "/dev/ttyS1";//"/dev/ttyMT1";//
    public static final String SERIALPORT_PATH = "/dev/ttyMT2";//"/dev/ttyS1";//
    /**
     * 后台服务行程时默认的的channel名称
     */
    public static final String COMAPCT_SERVICE_CHANNEL_NAME = "ObdCompactService";
}
