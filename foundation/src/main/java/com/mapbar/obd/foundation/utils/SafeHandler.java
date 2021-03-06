package com.mapbar.obd.foundation.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 安全的 handler
 * Created by zhangyunfei on 16/7/21.
 */
public class SafeHandler<T> extends Handler {
    private WeakReference<T> innerObject;

    public SafeHandler(T object) {
        this.innerObject = new WeakReference<T>(object);
    }

    public T getInnerObject() {
        return innerObject == null ? null : innerObject.get();
    }

    @Override
    public void handleMessage(Message msg) {
        if (getInnerObject() == null)
            return;

        super.handleMessage(msg);
    }
}
