package com.mapbar.android.obd.rearview.modules.maintenance;

import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.maintenance.contract.IMaintenanceView;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;

/**
 * 保养 presenter
 * Created by zhangyunfei on 16/8/4.
 */
public class MaintenancePresenter extends BasePresenter<IMaintenanceView>{

    private final PermissionManager permissionManager;

    public MaintenancePresenter(IMaintenanceView view) {
        super(view);

        permissionManager = LogicFactory.createPermissionManager(getView().getContext());

    }

    public void checkPermission() {
        PermissionManager.PermissionResult permission4State = permissionManager.checkPermission(PermissionKey.PERMISSION_MAINTENANCE);
        IPermissionAlertViewAble permissionAlertViewAble = getView();
        if (!permission4State.isValid) {
            permissionAlertViewAble.showPermissionAlertView_FreeTrial(permission4State.expired, permission4State.numberOfDay);
        }else {
            permissionAlertViewAble.hidePermissionAlertView_FreeTrial();
        }
    }
}
