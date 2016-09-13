package com.mapbar.android.obd.rearview.obd.util;

import android.os.Debug;

/**
 * Created by zhangyunfei on 16/9/13.
 */
public class TraceUtil {

    public static final String TRACE_1 = "trace1";

    public static void start(){
        Debug.startMethodTracing(TRACE_1);
        Debug.startNativeTracing();
    }

    public static void stop(){
        Debug.stopMethodTracing();
        Debug.stopNativeTracing();
    }
}
