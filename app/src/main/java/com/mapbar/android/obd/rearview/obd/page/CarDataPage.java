package com.mapbar.android.obd.rearview.obd.page;


import android.os.Bundle;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.model.AppPage;

/**
 * Created by THINKPAD on 2016/5/6.
 */
public class CarDataPage extends AppPage {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_car_current_data_page);
    }

    @Override
    public void initView() {

    }

    @Override
    public void setListener() {

    }
}
