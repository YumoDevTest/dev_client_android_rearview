package com.mapbar.android.obd.rearview.modules.cardata;

import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.modules.cardata.contract.ICarDataView;
import com.mapbar.android.obd.rearview.modules.permission.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;

/**
 * 车辆数据，呈现器
 * Created by zhangyunfei on 16/8/3.
 */
public class CarDataPresenter extends BasePresenter<ICarDataView> {
    private TirePressureManager tirePressureManager;
    private PermissionManager permissionManager;

    public CarDataPresenter(ICarDataView view) {
        super(view);
        tirePressureManager = new TirePressureManager();

        //先隐藏所有的胎压视图
        getView().hideTirePresstureFoureView();
        getView().hideTirePresstureSingleView();

        //单一胎压
        if (tirePressureManager.isTirePressuresOK())
            getView().showTirePresstureSingleNormal();
        else
            getView().showTirePresstureSingleWarning();
        //四轮胎压
        TirePressureBean[] tirePressureBeenArray = tirePressureManager.getTirePressures();
        getView().showTirePresstureFour(tirePressureBeenArray);

        permissionManager = new PermissionManager();
        //试用提醒
        PermissionManager.PermissionResult result = permissionManager.checkPermission(PermissionKey.PERMISSION_CAR_DATA);
        if (!result.isValid) {
            IPermissionAlertViewAble permissionAlertViewAble = getView();
            permissionAlertViewAble.showPermissionAlertView_FreeTrial(result.expired, result.numberOfDay);
        }
    }


    public void clear() {
        if (tirePressureManager != null) {
            tirePressureManager.clear();
            tirePressureManager = null;
        }
    }
}
