package com.mapbar.android.obd.rearview.modules.maintenance;

import android.os.Bundle;

import com.mapbar.android.obd.rearview.lib.base.TitlebarActivity;

/**
 * 保养校正activity,显示保养校正的fragment视图
 * Created by zhangyh on 2016/9/23.
 */
public class CarMaintenanceReviseActivity extends TitlebarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            showFragment(new CarMaintenanceReviseFragment(), false);
        }
        getTitlebarview().setTitle(getTitle().toString());
        getTitlebarview().setEnableBackButton(true);
    }
}
