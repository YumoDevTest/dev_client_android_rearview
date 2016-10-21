package com.mapbar.android.obd.rearview.lib.serialportsearch;

import android.text.TextUtils;

import com.mapbar.obd.serial.comond.SerialPortConnection;
import com.mapbar.obd.serial.comond.impl.SerialPortConnectionCreator;

/**
 * Created by zhangyh on 2016/10/17 0017.
 * 查找串口的类
 */

public class SerialportFinder {
    private static final String TAG = "SerialportFinder";
    private SerialPortConnection connection;

    /**
     * 搜索到一个可用串口,需要打开串口的回调监听
     *
     * @return
     */

    public void findAvailableSerialPort(OnOpenSerialPortLinstener onOpenSerialPortLinstener) {
        if (connection == null) {
            //遍历查找串口
            for (int i = 0; i < SerialportConstants.SERIALPORT_PATHS.length; i++) {
                try {
                    connection = SerialPortConnectionCreator.create(SerialportConstants.SERIALPORT_PATHS[i], SerialportConstants.BAUDRATE_DEFAULT, SerialportConstants.TIMEOUT_DEFAULT);
                    onOpenSerialPortLinstener.onOpenSerialPortStart(SerialportConstants.SERIALPORT_PATHS[i], SerialportConstants.BAUDRATE_DEFAULT, SerialportConstants.TIMEOUT_DEFAULT);
                    connection.start();
                    String result = new String(connection.sendAndReceive("ATI\r".getBytes()));
                    if (!TextUtils.isEmpty(result)) {
                        onOpenSerialPortLinstener.onOpenSerialPortSuccess(SerialportConstants.SERIALPORT_PATHS[i]);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (i == SerialportConstants.SERIALPORT_PATHS.length - 1) {
                        onOpenSerialPortLinstener.onOpenSerialPortFailure("串口打开失败");
                    }
                } finally {
                    connection.stop();
                }
            }
        }
    }

    /**
     * 创建一个打开串口的回调接口
     */
    public interface OnOpenSerialPortLinstener {
        void onOpenSerialPortStart(String serialPortName, int baudrate, int timeout);

        void onOpenSerialPortSuccess(String serialPortName);

        void onOpenSerialPortFailure(String result);
    }
}
