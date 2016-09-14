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
import com.mapbar.android.obd.rearview.lib.mvp.IMvpView;
import com.mapbar.android.obd.rearview.modules.common.OBDSDKListenerManager;

import java.lang.ref.WeakReference;


public abstract class AppPage extends Fragment implements IMvpView {
    public boolean isUmenngWorking = false;
    protected OBDSDKListenerManager.SDKListener sdkListener;
    protected View contentView;
    private Bundle data;
    private WeakReference<Activity> activityWeakReference;
    private int contentViewResource;
    private boolean isInited;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null) {
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

    @Override
    public void onAttach(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
        super.onAttach(activity);
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
        return activityWeakReference == null ? null : activityWeakReference.get();
    }

    public void initByCustom(Context context, int layoutId) {
        contentView = View.inflate(context, layoutId, null);
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
}
