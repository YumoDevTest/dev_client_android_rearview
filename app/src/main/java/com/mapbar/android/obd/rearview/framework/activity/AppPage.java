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

import com.mapbar.android.obd.rearview.MainActivity;
import com.mapbar.android.obd.rearview.framework.control.SDKManager;
import com.mapbar.android.obd.rearview.framework.inject.ViewInjectTool;

import java.lang.ref.WeakReference;


public abstract class AppPage extends Fragment {
    protected SDKManager.SDKListener sdkListener;
    protected WeakReference<View> contentView;
    private Bundle data;
    private Context context;
    private int contentViewResource;

    public AppPage() {
        context = MainActivity.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null || contentView.get() == null) {
            View view = inflater.inflate(contentViewResource, container, false);
            contentView = new WeakReference<>(view);
            ViewInjectTool.inject(this, this.contentView.get());
            initView();
            setListener();
        } else {
            ViewGroup parent = (ViewGroup) contentView.get().getParent();
            if (parent != null) {
                parent.removeView(contentView.get());
            }
            if (sdkListener != null) {
                SDKManager.getInstance().registSdkListener(sdkListener);
            }
        }
        return contentView.get();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public View getContentView() {
        return contentView.get();
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


    public SDKManager.SDKListener getSdkListener() {
        return sdkListener;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public Context getContext() {
        return context;
    }

    public void initByCustom(int layoutId) {
        View view = View.inflate(MainActivity.getInstance(), layoutId, null);
        contentView = new WeakReference<>(view);
        ViewInjectTool.inject(this, this.contentView.get());
        initView();
        setListener();
    }
}
