package com.mapbar.android.obd.rearview.obd.page;

import android.os.Bundle;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.model.AppPage;


public class MainPage extends AppPage {

    private static MainPage instance;

    public static MainPage getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_main);
    }

    @Override
    public void initView() {


    }

    @Override
    public void setListener() {

    }


}
