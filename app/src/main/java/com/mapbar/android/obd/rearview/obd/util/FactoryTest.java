package com.mapbar.android.obd.rearview.obd.util;

import android.text.TextUtils;
import android.util.Log;

import com.mapbar.android.obd.rearview.obd.SerialPortConnection;

import java.security.InvalidParameterException;

/**
 * 为 工厂 测试用
 * Created by zhangyunfei on 16/8/15.
 */
public class FactoryTest {

    /**
     * 工厂
     * 必须在事件4之后调用
     *
     * @return 转速和车速
     */
    public static String[] testSerialPortConnection(SerialPortConnection connection) {
        String[] resultArray = new String[2];
        resultArray[0] = "";
        resultArray[1] = "";
        try {
            /**
             *  410C后的 2个字节,
             410D的  1 个字节
             */
            String cmdRequest1 = "010C\r";//转速
            String cmdRequest2 = "010D\r";//车速

            String response1 = new String(connection.sendAndReceive(cmdRequest1.getBytes()));
            String response2 = new String(connection.sendAndReceive(cmdRequest2.getBytes()));


            resultArray[0] = "" + parseZhuansu(response1);
            resultArray[1] = "" + parseSpeed(response2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultArray;
    }

    /**
     * 发送 010c
     * 收到 7E80 4410C 2407
     * 取 后4个字符
     *
     * @param cmd
     * @return
     */
    private static int parseZhuansu(String cmd) {
        if (TextUtils.isEmpty(cmd))
            throw new NullPointerException();
        if (cmd.length() < 4)
            throw new InvalidParameterException("字符长度不合法");
        int index = cmd.indexOf("410C") + 4;
        if (index < 0)
            throw new InvalidParameterException("字符长度不合法");
        String s1 = cmd.substring(index, index + 2);
        String s2 = cmd.substring(index + 2, index + 4);
        int b1 = Integer.parseInt(s1, 16);
        int b2 = Integer.parseInt(s2, 16);
        return b1 * 256 + b2;
    }

    /**
     * 发送 010d
     * 收到 7E80 3410D 00
     * 取 后2个字符
     *
     * @param cmd
     * @return
     */
    private static int parseSpeed(String cmd) {
        Log.e("parseSpeed", cmd);
        if (TextUtils.isEmpty(cmd))
            throw new NullPointerException();
        if (cmd.length() < 4)
            throw new InvalidParameterException("字符长度不合法");
        int index = cmd.indexOf("410D") + 4;
        if (index < 0)
            throw new InvalidParameterException("字符长度不合法");
        String str = cmd.substring(index, index + 2);
        int b1 = Integer.parseInt(str, 16);
        Log.e("parseSpeed", b1 + "");
        return b1;
    }

}
