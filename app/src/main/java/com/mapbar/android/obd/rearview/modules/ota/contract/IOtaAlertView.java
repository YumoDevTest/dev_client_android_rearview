package com.mapbar.android.obd.rearview.modules.ota.contract;

import android.view.View;

import com.mapbar.android.obd.rearview.lib.mvp.IMvpView;

/**
 * Created by zhangyunfei on 16/8/27.
 */
public interface IOtaAlertView extends IMvpView {

    void showView_alertUpgrade();

    void showView_alert_ForceUpgrade();

    void showView_alertSuccess();

    void showView_alertFailure();

    void showView_alertProgress(int progress);
}
