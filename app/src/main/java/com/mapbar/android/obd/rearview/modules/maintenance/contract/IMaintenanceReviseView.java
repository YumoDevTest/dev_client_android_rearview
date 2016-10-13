package com.mapbar.android.obd.rearview.modules.maintenance.contract;

import com.mapbar.android.obd.rearview.modules.maintenance.model.MaintenanceData;
import com.mapbar.obd.foundation.mvp.IMvpView;

/**
 * 保养校正
 * Created by zhangyh on 2016/9/23.
 */
public interface IMaintenanceReviseView extends IMvpView {

    MaintenanceData getMaintenanceData();

    void setMaintenanceData(MaintenanceData maintenanceData);

    void onSaveDataSuccess();
}
