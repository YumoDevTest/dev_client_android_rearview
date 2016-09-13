package com.mapbar.android.obd.rearview.lib.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.android.obd.rearview.views.TitleBarView;

/**
 * Created by zhangyunfei on 16/7/26.
 */
public class MyBaseFragmentActivity extends FragmentActivity {
    private static final String TAG = MyBaseActivity.class.getSimpleName();
    public static final int CONTAINER_VIEW_ID = R.id.container1;
    private TitleBarView titlebarview1;
    private static String ThisClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_base_activity);
        titlebarview1 = (TitleBarView)findViewById(R.id.titlebarview1);
        LogUtil.d(TAG, String.format("## %s onCreate", getThisClassName()));
    }

    protected String getThisClassName() {
        if (ThisClassName == null) {
            ThisClassName = getClass().getSimpleName();
        }
        return ThisClassName;
    }

    /**
     * 获得标题栏
     * @return
     */
    public TitleBarView getTitlebarview() {
        return titlebarview1;
    }

    public void showFragment(MyBaseFragment fragment) {
        showFragment(fragment, true);
    }

    public void showFragment(MyBaseFragment fragment, boolean isAddToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(CONTAINER_VIEW_ID, fragment);
        if (isAddToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void alert(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void alert(int sourceID) {
        Toast.makeText(getActivity(), sourceID, Toast.LENGTH_SHORT).show();
    }

    public Context getActivity() {
        return this;
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
        LogUtil.d(TAG, String.format("## %s onResume", getThisClassName()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, String.format("## %s onPause", getThisClassName()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, String.format("## %s onActivityResult", getThisClassName()));
    }
}
