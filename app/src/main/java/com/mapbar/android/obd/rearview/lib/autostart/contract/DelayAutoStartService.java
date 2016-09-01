package com.mapbar.android.obd.rearview.lib.autostart.contract;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.InvalidObjectException;
import java.lang.ref.WeakReference;

/**
 * 自启动处理器
 * Created by zhangyunfei on 16/9/1.
 */
public abstract class DelayAutoStartService {
    public static final String ACTION_DELAY_START_APP = "ACTION_DELAY_START_APP";//延迟启动app
    public static final String ACTION_STOP_START_APP = "ACTION_STOP_START_APP";//停止启动app

    private WeakReference<Context> contextWeakReference;


    public void start(Context context) {
        if (contextWeakReference != null)
            throw new RuntimeException("## 已经启动");
        contextWeakReference = new WeakReference<Context>(context);

        IntentFilter filter = new IntentFilter();
        filter.addAction(DelayAutoStartService.ACTION_DELAY_START_APP);
        filter.addAction(DelayAutoStartService.ACTION_STOP_START_APP);
        getContext().registerReceiver(myBroadcastReceiver, filter);
    }

    protected abstract void onHandleIntent(Intent intent);

    protected Context getContext() {
        return contextWeakReference == null ? null : contextWeakReference.get();
    }

    public void clear() {
        getContext().unregisterReceiver(myBroadcastReceiver);
        if (contextWeakReference != null) {
            contextWeakReference.clear();
            contextWeakReference = null;
        }
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onHandleIntent(intent);
        }
    };
}
