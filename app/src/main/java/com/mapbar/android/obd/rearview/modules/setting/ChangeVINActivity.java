package com.mapbar.android.obd.rearview.modules.setting;

import android.os.Bundle;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.common.LayoutUtils_ui;
import com.mapbar.android.obd.rearview.lib.base.MyBaseFragmentActivity;

/**
 *
 * 修改VIN页面
 */
public class ChangeVinActivity extends MyBaseFragmentActivity {
    private ChangeVinBarcodeFragment changeVinBarcodeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeVinBarcodeFragment = new ChangeVinBarcodeFragment();
        showPage_barcode();
        LayoutUtils_ui.proportional();
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
