package com.mapbar.android.obd.rearview.obd.page;

import android.os.Bundle;
import android.widget.ImageView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.model.AppPage;


/**
 * Created by liuyy on 2016/3/22.
 */
public class SplashPage extends AppPage {

    @ViewInject(R.id.iv_splash)
    private ImageView ivSplash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_splash);
    }

    @Override
    public void initView() {


    }

    @Override
    public void setListener() {

    }

}
