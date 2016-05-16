package com.mapbar.android.obd.rearview.obd.page;

import android.widget.ListView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;

/**
 * Created by liuyy on 2016/5/15.
 */
public class ControlTestPage extends AppPage {

    @ViewInject(R.id.lv_test)
    private ListView lv;

    @Override
    public void initView() {

    }

    @Override
    public void setListener() {

    }
}
