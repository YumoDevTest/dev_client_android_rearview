package com.mapbar.android.obd.rearview.lib.base;

import android.content.Context;
import android.view.View;

import com.mapbar.obd.foundation.mvp.IMvpView;


public abstract class AppPage2 extends TitlebarFragment implements IMvpView {
    private static final String TAG = AppPage2.class.getSimpleName();
    protected View contentView;

    public View getContentView() {
        return contentView;
    }

    protected void createContenttView(int layoutId) {
        contentView = View.inflate(getActivity(), layoutId, null);
    }

    public Context getContext() {
        return getActivity();
    }


}
