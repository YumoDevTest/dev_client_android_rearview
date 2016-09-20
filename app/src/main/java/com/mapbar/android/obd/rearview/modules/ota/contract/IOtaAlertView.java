package com.mapbar.android.obd.rearview.modules.ota.contract;

import com.mapbar.obd.foundation.mvp.IMvpView;

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
