package com.mapbar.android.obd.rearview.framework.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbar.android.obd.rearview.framework.inject.ViewInjectTool;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;


public abstract class AppPage extends Fragment {
    protected OBDSDKListenerManager.SDKListener sdkListener;
    protected View contentView;
    private Bundle data;
    private Context context;
    private int contentViewResource;
    private boolean isInited;

    public AppPage() {
        context = MainActivity.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null || contentView == null) {
            contentView = inflater.inflate(contentViewResource, container, false);
            ViewInjectTool.inject(this, this.contentView);
            initView();
            setListener();
        } else {
            ViewGroup parent = (ViewGroup) contentView.getParent();
            if (parent != null) {
                parent.removeView(contentView);
            }
            if (sdkListener != null) {
                OBDSDKListenerManager.getInstance().registSdkListener(sdkListener);
            }
        }
        return contentView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    public abstract void setListener();


    public OBDSDKListenerManager.SDKListener getSdkListener() {
        return sdkListener;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public Context getContext() {
        return context;
    }

    public void initByCustom(int layoutId) {
        contentView = View.inflate(MainActivity.getInstance(), layoutId, null);
        ViewInjectTool.inject(this, this.contentView);
        initView();
        setListener();
    }

    public boolean isInited() {
        return isInited;
    }

    public void setIsInited(boolean isInited) {
        this.isInited = isInited;
    }
}
