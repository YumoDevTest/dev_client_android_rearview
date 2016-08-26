package com.mapbar.android.obd.rearview.modules.carstate.contract;

import com.mapbar.android.obd.rearview.lib.mvp.IMvpView;

/**
 * 修改vin的视图
 * Created by zhangyunfei on 16/8/26.
 */
public interface IVinChangeView extends IMvpView {

    /**
     * 显示vin 二维码 的对话框
     */
    public void showVinInputDialog();


    /**
     * 显示 VIN修改 扫码成功
     */
    public void showVinScanOK();


    /**
     * 隐藏vin 二维码 的对话框
     */
    public void hideVinInputDialog();

}
