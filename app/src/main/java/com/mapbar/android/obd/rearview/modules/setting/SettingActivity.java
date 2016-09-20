package com.mapbar.android.obd.rearview.modules.setting;

import android.os.Bundle;

import com.mapbar.android.obd.rearview.lib.base.TitlebarActivity;
import com.mapbar.android.obd.rearview.util.LayoutUtils_ui;

/**
 * 设置 页
 * Created by zhangyunfei on 16/7/25.
 */
public class SettingActivity extends TitlebarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
            showFragment(new SettingFragment(), false);
        LayoutUtils_ui.proportional();
        getTitlebarview().setEnableBackButton(true);
    }


}
