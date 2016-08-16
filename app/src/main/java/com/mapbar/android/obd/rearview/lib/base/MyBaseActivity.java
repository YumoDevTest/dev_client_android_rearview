package com.mapbar.android.obd.rearview.lib.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Activity 基础类
 * Created by zhangyunfei on 16/7/26.
 */
public class MyBaseActivity extends Activity {

    public void alert(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void alert(int sourceID) {
        Toast.makeText(getActivity(), sourceID, Toast.LENGTH_SHORT).show();
    }



    public Context getActivity() {
        return this;
    }
}
