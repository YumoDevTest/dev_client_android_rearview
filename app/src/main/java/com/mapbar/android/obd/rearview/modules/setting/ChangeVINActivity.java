package com.mapbar.android.obd.rearview.modules.setting;

import android.os.Bundle;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.base.TitlebarActivity;
import com.mapbar.android.obd.rearview.util.LayoutUtils_ui;

/**
 *
 * 修改VIN页面
 *  Created by zhangyh on 2016/9/8.
 */
public class ChangeVINActivity extends TitlebarActivity {
    private ChangeVinBarcodeFragment changeVinBarcodeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeVinBarcodeFragment = new ChangeVinBarcodeFragment();
        showPage_barcode();
    }

    @Override
    protected void onStart() {
        getTitlebarview().setTitle(R.string.page_title_change_vin);
        getTitlebarview().setEnableBackButton(true);
        super.onStart();
    }
    /**
     * 显示二维码页面
     */
    public void showPage_barcode(){
        showFragment(changeVinBarcodeFragment,false);
    }
}
