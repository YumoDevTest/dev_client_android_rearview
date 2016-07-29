package com.mapbar.android.obd.rearview.obd.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by zhangyunfei on 16/7/29.
 */
public class MyStringUtils {

    /**
     * 输出 异常的详细stack信息
     * @param ex
     * @return
     */
    public static String getExceptionString(Throwable ex) {
        if (ex == null) return null;
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        ex.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
