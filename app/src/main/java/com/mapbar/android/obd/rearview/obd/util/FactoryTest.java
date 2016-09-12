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
            response1 = OutputStringUtil.transferForPrint(response1);
            String response2 = new String(connection.sendAndReceive(cmdRequest2.getBytes()));
            response2 = OutputStringUtil.transferForPrint(response2);
            if (response1.equals("NODATA") || response1.equals("?"))
                resultArray[0] = "NODATA";
            else
                resultArray[0] = "" + parseZhuansu(response1);

            if (response2.equals("NODATA") || response1.equals("?"))
                resultArray[1] = "NODATA";
            else
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
//        Log.d("parseZhuansu 转换前 ", cmd);
        if (TextUtils.isEmpty(cmd))
            throw new NullPointerException();
        if (cmd.length() < 4)
            throw new InvalidParameterException("字符长度不合法");

        int index = cmd.indexOf("410C");
        if (cmd.length() < 0)
            throw new InvalidParameterException("数据不合法");
        index = index + 4;
        if (index < 0)
            throw new InvalidParameterException("字符长度不合法");
        String s1 = cmd.substring(index, index + 2);
        String s2 = cmd.substring(index + 2, index + 4);
        int b1 = Integer.parseInt(s1, 16);
        int b2 = Integer.parseInt(s2, 16);
        int res = b1 * 256 + b2;

//        Log.d("parseZhuansu 转换后 ", res + "");
        return res;
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
//        Log.d("parseSpeed 转换前", cmd);
        if (TextUtils.isEmpty(cmd))
            throw new NullPointerException();
        if (cmd.length() < 4)
            throw new InvalidParameterException("字符长度不合法");
        int index = cmd.indexOf("410D") + 4;
        if (index < 0)
            throw new InvalidParameterException("字符长度不合法");
        String str = cmd.substring(index, index + 2);
        int b1 = Integer.parseInt(str, 16);
//        Log.d("parseSpeed 转换后", b1 + "");
        return b1;
    }

}
