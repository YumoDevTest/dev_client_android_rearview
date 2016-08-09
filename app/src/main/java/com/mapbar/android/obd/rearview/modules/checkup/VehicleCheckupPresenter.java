package com.mapbar.android.obd.rearview.modules.checkup;

import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.modules.checkup.contract.IVehicleCheckupView;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;

/**
 * Created by zhangyunfei on 16/8/4.
 */
public class VehicleCheckupPresenter extends BasePresenter<IVehicleCheckupView>{
    private PermissionManager permissionManager;

    public VehicleCheckupPresenter(IVehicleCheckupView view) {
        super(view);

        permissionManager = LogicFactory.createPermissionManager(getView().getContext());
        PermissionManager.PermissionResult permission4State = permissionManager.checkPermission(PermissionKey.PERMISSION_CHECK_UP);
        if (!permission4State.isValid) {
            IPermissionAlertViewAble permissionAlertViewAble = getView();
            permissionAlertViewAble.showPermissionAlertView_FreeTrial(permission4State.expired, permission4State.numberOfDay);
        }
    }
}
