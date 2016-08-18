package com.mapbar.android.obd.rearview.modules.common;

import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.lib.mvp.IMvpView;
import com.mapbar.android.obd.rearview.modules.common.contract.IMainPageView;
import com.mapbar.android.obd.rearview.modules.tirepressure.TirePressureAlermEventDispatcher;
import com.mapbar.obd.TPMSAlarmData;

/**
 * MainPage persenter
 * Created by zhangyunfei on 16/8/18.
 */
public class MainPagePersenter extends BasePresenter<IMainPageView> {
    private TirePressureAlermEventDispatcher tirePressureAlermEventDispatcher;

    public MainPagePersenter(final IMainPageView view) {
        super(view);
        tirePressureAlermEventDispatcher = new TirePressureAlermEventDispatcher();
        tirePressureAlermEventDispatcher.start(new TirePressureAlermEventDispatcher.TirePressureAlarmCallback() {
            @Override
            public void onAlerm(TPMSAlarmData alarmData) {
                view.notifyTirePresstureAlermComming(alarmData);
            }
        });
    }


    public void clear() {
        if (tirePressureAlermEventDispatcher != null) {
            tirePressureAlermEventDispatcher.stop();
            tirePressureAlermEventDispatcher = null;
        }
    }
}
