package com.mapbar.android.obd.rearview.modules.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.base.AppPage2;


/**
 * Created by liuyy on 2016/3/22.
 */
public class SplashPage extends AppPage2 {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentView() == null) {
            createContenttView(R.layout.page_splash);
        }
        return getContentView();
    }


}
