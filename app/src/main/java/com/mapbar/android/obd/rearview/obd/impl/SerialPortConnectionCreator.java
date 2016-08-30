package com.mapbar.android.obd.rearview.obd.impl;


import com.mapbar.android.obd.rearview.obd.SerialPortConnection;

/**
 * Created by zhangyunfei on 16/8/25.
 */
public class SerialPortConnectionCreator {

    public static SerialPortConnection create(String serialportName, int baudrate) {
        return new SerialPortConnectionImpl(serialportName, baudrate);
    }
}
