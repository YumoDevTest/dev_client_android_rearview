package com.mapbar.android.obd.rearview.lib.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.views.TitleBarView;

/**
 * Created by zhangyunfei on 16/7/26.
 */
public class MyBaseActivity extends FragmentActivity {
    public static final int CONTAINER_VIEW_ID = R.id.container1;
    private TitleBarView titlebarview1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_base_activity);
        titlebarview1 = (TitleBarView)findViewById(R.id.titlebarview1);
    }

    /**
     * 获得标题栏
     * @return
     */
    public TitleBarView getTitlebarview() {
        return titlebarview1;
    }

    public void showFragment(MyBaseFragment fragment) {
        showFragment(fragment, true);
    }

    public void showFragment(MyBaseFragment fragment, boolean isAddToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(CONTAINER_VIEW_ID, fragment);
        if (isAddToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void alert(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void alert(int sourceID) {
        Toast.makeText(getActivity(), sourceID, Toast.LENGTH_SHORT).show();
    }

    public Context getActivity() {
        return this;
    }
}
