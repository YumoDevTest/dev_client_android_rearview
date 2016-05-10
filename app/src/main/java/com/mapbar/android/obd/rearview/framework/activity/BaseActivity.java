package com.mapbar.android.obd.rearview.framework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.mapbar.android.obd.rearview.framework.control.PageManager;

import java.util.ArrayList;


public abstract class BaseActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ArrayList<AppPage> pages = PageManager.getInstance().getPages();
        int size = pages.size();
        if (size > 0) {
            pages.get(pages.size() - 1).onActivityResult(requestCode, resultCode, data);
        }
    }
}
