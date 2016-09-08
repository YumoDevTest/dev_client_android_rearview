package com.mapbar.demo;

import junit.framework.Assert;

/**
 * Created by zhangyunfei on 16/9/8.
 */
public class TestParseBooleanBit {


    public static boolean parseBoolean(int value, int index) {
        if (index < 1)
            throw new IndexOutOfBoundsException("index 必须大于等于1");
        int offset = index - 1;
        boolean b = ((value >> offset) & 0x01) == 0x01;
        return b;
    }


    public static void run() {
        Assert.assertEquals("1", true, parseBoolean(0x01, 1));
        Assert.assertEquals("2", false, parseBoolean(0x01, 2));
        Assert.assertEquals("3", false, parseBoolean(0x02, 1));
        Assert.assertEquals("4", true, parseBoolean(0x02, 2));
    }
}
