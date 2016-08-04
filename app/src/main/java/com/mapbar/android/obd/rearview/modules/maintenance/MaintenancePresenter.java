package com.mapbar.android.obd.rearview.modules.maintenance;

import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.modules.maintenance.contract.IMaintenanceView;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;

/**
 * 保养 presenter
 * Created by zhangyunfei on 16/8/4.
 */
public class MaintenancePresenter extends BasePresenter<IMaintenanceView>{

    public MaintenancePresenter(IMaintenanceView view) {
        super(view);

        //试用提醒
        IPermissionAlertViewAble permissionAlertViewAble = getView();
        permissionAlertViewAble.showPermissionAlertView_FreeTrial(true, 0);
    }
}
