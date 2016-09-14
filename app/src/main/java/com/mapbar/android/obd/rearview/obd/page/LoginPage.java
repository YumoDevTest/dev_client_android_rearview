package com.mapbar.android.obd.rearview.obd.page;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.modules.common.MyApplication;

public class LoginPage extends AppPage {
    @ViewInject(R.id.tv_login_goPage)
    private TextView tv_goPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_login);
    }

    @Override
    public void initView() {
        setListener();
    }

    public void setListener() {
        tv_goPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().getMainActivity().goMainPage();
            }
        });

    }


}
