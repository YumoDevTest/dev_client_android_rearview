package com.mapbar.android.obd.rearview.lib.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.lib.mvp.IMvpView;

/**
 * Activity 基础类
 * Created by zhangyunfei on 16/7/26.
 */
public class MyBaseActivity extends Activity implements IMvpView {

    public void alert(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void alert(final int sourceID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), sourceID, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Context getContext() {
        return this;
    }


    public Activity getActivity() {
        return this;
    }
}
