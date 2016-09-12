package com.mapbar.android.obd.rearview.obd;

import android.text.TextUtils;

import java.io.IOException;

/**
 * 串口 的连接
 * Created by zhangyunfei on 16/8/14.
 */
public abstract class SerialPortConnection {
    private String serialportName;
    private int baudrate;

    public SerialPortConnection(String serialportName, int baudrate) {
        if (TextUtils.isEmpty(serialportName)) {
            throw new NullPointerException("串口名称不合法");
        }
        if (baudrate <= 0)
            throw new NullPointerException("baudrate不合法");
        this.serialportName = serialportName;
        this.baudrate = baudrate;
    }

    public String getSerialportName() {
        return serialportName;
    }

    public int getBaudrate() {
        return baudrate;
    }

    public abstract void start() throws IOException;

    public abstract void stop();

    public abstract byte[] sendAndReceive(byte[] cmd) throws ReadTimeoutException, IOException;
}
