package com.mapbar.android.obd.rearview.modules.common.contract;

import com.mapbar.android.obd.rearview.lib.mvp.IMvpView;
import com.mapbar.map.MapView;
import com.mapbar.obd.TPMSAlarmData;

/**
 * Created by zhangyunfei on 16/8/18.
 */
public interface IMainPageView extends IMvpView {

    void notifyTirePresstureAlermComming(TPMSAlarmData alarmData);

}
