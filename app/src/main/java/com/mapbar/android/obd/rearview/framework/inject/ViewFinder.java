package com.mapbar.android.obd.rearview.framework.inject;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * @author baimi
 */
public class ViewFinder {

    private View view;
    private Activity activity;

    public ViewFinder(View view) {
        this.view = view;
    }


    public ViewFinder(Activity activity) {
        this.activity = activity;
    }

    public View findViewById(int id, int pid) {
        View pView = null;
        if (pid > 0) {
            pView = this.findViewById(pid);
        }

        View view = null;
        if (null != pView) {
            view = pView.findViewById(id);
        } else {
            view = this.findViewById(id);
        }
        return view;
    }

    public View findViewById(int id) {
        return null == activity ? view.findViewById(id) : activity.findViewById(id);
    }


    public Context getContext() {
        if (null != view) {
            return view.getContext();
        }
        if (null != activity) {
            return activity;
        }
        return null;
    }
}
