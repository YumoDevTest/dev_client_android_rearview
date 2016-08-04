package com.mapbar.android.obd.rearview.modules.checkup;

import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.modules.carstate.contract.ICarStateView;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;

/**
 * 车辆状态 presenter
 * Created by zhangyunfei on 16/8/4.
 */
public class CarStatePresenter extends BasePresenter<ICarStateView> {

    public CarStatePresenter(ICarStateView view) {
        super(view);

        //试用提醒
        IPermissionAlertViewAble permissionAlertViewAble = getView();
        permissionAlertViewAble.showPermissionAlertView_FreeTrial(true, 0);
    }
}
