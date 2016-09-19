package com.mapbar.android.obd.rearview.obd.util;

import android.os.Debug;

/**
 * Created by zhangyunfei on 16/9/13.
 */
public class TraceUtil {
    public static boolean DEBUG = false;
    public static final String TRACE_1 = "trace1";

    public static void start() {
        if (!DEBUG) return;
        Debug.startMethodTracing(TRACE_1);
        Debug.startNativeTracing();
    }

    public static void stop() {
        if (!DEBUG) return;
        Debug.stopMethodTracing();
        Debug.stopNativeTracing();
    }
}
