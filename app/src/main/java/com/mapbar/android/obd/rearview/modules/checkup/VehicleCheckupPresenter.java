package com.mapbar.android.obd.rearview.modules.checkup;

import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.modules.checkup.contract.IVehicleCheckupView;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;

/**
 * Created by zhangyunfei on 16/8/4.
 */
public class VehicleCheckupPresenter extends BasePresenter<IVehicleCheckupView>{

    public VehicleCheckupPresenter(IVehicleCheckupView view) {
        super(view);

        //试用提醒
        IPermissionAlertViewAble permissionAlertViewAble = getView();
        permissionAlertViewAble.showPermissionAlertView_FreeTrial(true, 0);
    }
}
