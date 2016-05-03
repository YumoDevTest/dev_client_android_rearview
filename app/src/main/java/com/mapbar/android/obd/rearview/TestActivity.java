package com.mapbar.android.obd.rearview;

import android.app.Activity;
import android.os.Bundle;

import com.mapbar.android.obd.rearview.framework.common.StringUtil;


/**
 * Created by liuyy on 2016/3/23.
 */
public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test);
        StringUtil.toastStringShort("测试");
    }
}
