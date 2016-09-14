package com.mapbar.android.obd.rearview.framework.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.mapbar.android.obd.rearview.framework.control.PageManager;
import com.mapbar.android.obd.rearview.lib.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.modules.common.MyApplication;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;

import java.util.ArrayList;


public abstract class BaseActivity extends FragmentActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static String ThisClassName;

    protected String getThisClassName() {

        if (ThisClassName == null) {
            ThisClassName = getClass().getSimpleName();
        }
        return ThisClassName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, String.format("## %s onCreate", getThisClassName()));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogUtil.d(TAG, String.format("## %s onNewIntent", getThisClassName()));
        super.onNewIntent(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.d(TAG, String.format("## %s onActivityResult", getThisClassName()));
        super.onActivityResult(requestCode, resultCode, data);
        final ArrayList<AppPage> pages = MyApplication.getInstance().getMainActivity().getPageManager().getPages();
        int size = pages.size();
        if (size > 0) {
            pages.get(pages.size() - 1).onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, String.format("## %s onStart", getThisClassName()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, String.format("## %s onStop", getThisClassName()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, String.format("## %s onDestroy", getThisClassName()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgentEx.onActivityResume(getActivity());
        LogUtil.d(TAG, String.format("## %s onResume", getThisClassName()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgentEx.onActivityPause(getActivity());
        LogUtil.d(TAG, String.format("## %s onPause", getThisClassName()));
    }

    public Activity getActivity() {
        return this;
    }
}
