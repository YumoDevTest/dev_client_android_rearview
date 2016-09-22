//package com.mapbar.android.obd.rearview.lib.base;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.mapbar.android.obd.rearview.framework.inject.ViewInjectTool;
//import com.mapbar.obd.foundation.mvp.IMvpView;
//
//
//public abstract class AppPage extends TitlebarFragment implements IMvpView {
//    private static final String TAG = AppPage.class.getSimpleName();
//    protected View contentView;
//    private Bundle data;
//    private int contentViewResource;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (contentView == null) {
//            contentView = inflater.inflate(contentViewResource, container, false);
//            ViewInjectTool.inject(this, this.contentView);
//            initView();
//        }
//        else {
//            ViewGroup parent = (ViewGroup) contentView.getParent();
//            if (parent != null) {
//                parent.removeView(contentView);
//            }
//        }
//        return contentView;
//    }
//
//    public View getContentView() {
//        return contentView;
//    }
//
//    public void setContentView(int contentViewResource) {
//        this.contentViewResource = contentViewResource;
//    }
//
//    public Bundle getData() {
//        return data;
//    }
//
//    public void setData(Bundle data) {
//        this.data = data;
//    }
//
//    public abstract void initView();
//
//    public void initByCustom(Context context, int layoutId) {
//        contentView = View.inflate(context, layoutId, null);
//        ViewInjectTool.inject(this, this.contentView);
//        initView();
//    }
//
//    public Context getContext() {
//        return getActivity();
//    }
//
//
//
//}
