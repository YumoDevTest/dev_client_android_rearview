package com.mapbar.android.obd.rearview.framework.common;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yun on 15/12/28.
 */
public class ViewUtils {

    public static View newInstance(Context context, int resId) {
        return View.inflate(context, resId, null);
    }

    public static View newInstance(ViewGroup parent, int resId) {
        return LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
    }
}
