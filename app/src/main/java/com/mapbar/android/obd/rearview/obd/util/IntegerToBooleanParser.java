package com.mapbar.android.obd.rearview.obd.util;

import junit.framework.Assert;

/**
 * 将 int 中的bit位转成 boolean
 * Created by zhangyunfei on 16/9/8.
 */
public class IntegerToBooleanParser {


    /**
     * 转成 boolean
     *
     * @param value  一个整数
     * @param offset 第几位,从0开始
     * @return
     */
    public static boolean parseBoolean(int value, int offset) {
        if (offset < 0)
            throw new IndexOutOfBoundsException("offset 必须大于等于0");
        boolean b = ((value >> offset) & 0x01) == 0x01;
        return b;
    }


    public static void run() {
        Assert.assertEquals("1", true, parseBoolean(0x01, 0));
        Assert.assertEquals("2", false, parseBoolean(0x01, 1));
        Assert.assertEquals("3", false, parseBoolean(0x02, 0));
        Assert.assertEquals("4", true, parseBoolean(0x02, 1));
        Assert.assertEquals("5", true, parseBoolean(0x08, 3));
        Assert.assertEquals("6", true, parseBoolean(0xFFFFFFFF, 30));
    }
}