package com.mapbar.android.obd.rearview.lib.serialportsearch;

/**
 * Created by zhangyh on 2016/10/20 0020.
 * 串口数据类
 */

public class SerialportConstants {
    public static final String[] SERIALPORT_PATHS = {"/dev/ttyMT0", "/dev/ttyMT1", "/dev/ttyMT2", "/dev/ttyMT3", "/dev/ttyS4"};
    public static final int BAUDRATE_DEFAULT = 115200;//波特率
    public static final int TIMEOUT_DEFAULT = 30000;//默认 串口超时时长
    public static final boolean IS_DEBUG_SERIALPORT = true;//是否debug串口
    public static final int CONNECTIONSUCCESS = 100;
    public static final int SERIALPORT_FIND_SUCCESS = 101;
    public static final int SERIALPORT_FIND_FAILURE = 102;
}
