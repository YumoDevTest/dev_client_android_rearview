package com.mapbar.android.obd.rearview.obd.page;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.control.PageManager;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.model.AppPage;

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

    }

    @Override
    public void setListener() {
        tv_goPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageManager.getInstance().goPage(MainPage.class);
            }
        });

    }


}
