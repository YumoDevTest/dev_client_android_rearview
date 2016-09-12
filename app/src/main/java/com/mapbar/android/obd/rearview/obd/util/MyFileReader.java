package com.mapbar.android.obd.rearview.obd.util;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by zhangyunfei on 16/8/14.
 */
public class MyFileReader {
    public static byte[] readAllBytes2(File file) throws IOException {
        if (file == null)
            throw new NullPointerException();
        if (!file.exists() || !file.isFile())
            throw new FileNotFoundException(file.getPath());
        FileInputStream inputStream = new FileInputStream(file);
        int allBytesLength = (int) file.length();
        byte[] buffer = new byte[allBytesLength];
        int n = 0;
        n = inputStream.read(buffer, 0, buffer.length);
        Assert.assertEquals(n, allBytesLength);
        return buffer;
    }

    public static byte[] readAllBytes(File file) throws IOException {
        if (file == null)
            throw new NullPointerException();
        if (!file.exists() || !file.isFile())
            throw new FileNotFoundException(file.getPath());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int) file.length());
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int n = 0;
        while ((n = inputStream.read(buffer, 0, buffer.length)) > 0) {
            byteArrayOutputStream.write(buffer, 0, n);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
