package com.mapbar.android.obd.rearview.lib.base;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.util.LayoutUtils_ui;
import com.mapbar.android.obd.rearview.views.TitleBarView;
import com.mapbar.obd.foundation.base.BaseFragmentActivity;
import com.mapbar.obd.foundation.base.BaseFragment;

/**
 * 带有 标题栏的activity，作为fragment的容器
 * Created by zhangyunfei on 16/7/26.
 */
public class TitlebarActivity extends BaseFragmentActivity {
    private static final String TAG = BaseFragmentActivity.class.getSimpleName();
    public static final int CONTAINER_VIEW_ID = R.id.container1;
    private TitleBarView titlebarview1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutUtils_ui.proportional(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_base_activity);
        titlebarview1 = (TitleBarView) findViewById(R.id.titlebarview1);
    }

    /**
     * 获得标题栏
     *
     * @return
     */
    public TitleBarView getTitlebarview() {
        return titlebarview1;
    }

    public void showFragment(BaseFragment fragment) {
        showFragment(fragment, true);
    }

    public void showFragment(BaseFragment fragment, boolean isAddToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(CONTAINER_VIEW_ID, fragment);
        if (isAddToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}
