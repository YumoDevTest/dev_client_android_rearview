package com.mapbar.android.obd.rearview.lib.base;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.util.LayoutUtils_ui;
import com.mapbar.android.obd.rearview.views.TitleBarView;
import com.mapbar.obd.foundation.base.BaseActivity;
import com.mapbar.obd.foundation.base.BaseFragmentActivity;
import com.mapbar.obd.foundation.base.BaseFragment;
import com.mapbar.obd.foundation.log.LogUtil;

/**
 * 简单 activity
 * Created by zhangyunfei on 16/7/26.
 */
public class SimpleActivity extends BaseActivity {
    private static final String TAG = SimpleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutUtils_ui.proportional(this);
        super.onCreate(savedInstanceState);
    }

}
