package com.mapbar.android.obd.rearview.framework.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.framework.inject.ViewInjectTool;
import com.mapbar.android.obd.rearview.lib.base.MyBaseActivity;
import com.mapbar.android.obd.rearview.lib.base.MyBaseFragmentActivity;
import com.mapbar.android.obd.rearview.lib.mvp.IMvpView;
import com.mapbar.android.obd.rearview.lib.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.modules.common.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;

import java.lang.ref.WeakReference;


public abstract class AppPage extends Fragment implements IMvpView {
    private static final String TAG = MyBaseActivity.class.getSimpleName();
    private   String ThisClassName;
    protected View contentView;
    private Bundle data;
    private WeakReference<Activity> activityWeakReference;
    private int contentViewResource;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(contentViewResource, container, false);
            ViewInjectTool.inject(this, this.contentView);
            initView();
        } else {
            ViewGroup parent = (ViewGroup) contentView.getParent();
            if (parent != null) {
                parent.removeView(contentView);
            }
        }
        return contentView;
    }

    protected String getThisClassName() {
        if (ThisClassName == null) {
            ThisClassName = getClass().getSimpleName();
        }
        return ThisClassName;
    }

    @Override
    public void onAttach(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
        LogUtil.d(TAG, String.format("## %s onAttach", getThisClassName()));
        super.onAttach(activity);
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(int contentViewResource) {
        this.contentViewResource = contentViewResource;
    }

    public Bundle getData() {
        return data;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    public abstract void initView();

    public Context getContext() {
        return activityWeakReference == null ? null : activityWeakReference.get();
    }

    public void initByCustom(Context context, int layoutId) {
        contentView = View.inflate(context, layoutId, null);
        ViewInjectTool.inject(this, this.contentView);
        initView();
    }

    public void alert(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void alert(final int sourceID) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), sourceID, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, String.format("## %s onDestroy", getThisClassName()));
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.d(TAG, String.format("## %s onStart", getThisClassName()));
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d(TAG, String.format("## %s onStop", getThisClassName()));
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgentEx.onPageStart(getThisClassName());
        LogUtil.d(TAG, String.format("## %s onPause", getThisClassName()));
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgentEx.onPageEnd(getThisClassName());
        LogUtil.d(TAG, String.format("## %s onResume", getThisClassName()));
    }


    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.d(TAG, String.format("## %s onDetach", getThisClassName()));
    }
}
