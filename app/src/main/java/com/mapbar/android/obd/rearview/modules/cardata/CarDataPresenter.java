package com.mapbar.android.obd.rearview.modules.cardata;

import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.modules.cardata.contract.ICarDataView;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;

/**
 * 车辆数据，呈现器
 * Created by zhangyunfei on 16/8/3.
 */
public class CarDataPresenter extends BasePresenter<ICarDataView> {
    private TirePressureManager tirePressureManager;
    private PermissionManager permissionManager;

    public CarDataPresenter(ICarDataView view) {
        super(view);
        tirePressureManager = LogicFactory.createTirePressureManager();
        permissionManager = LogicFactory.createPermissionManager(getView().getContext());

        //先隐藏所有的胎压视图
        getView().hideTirePresstureFoureView();
        getView().hideTirePresstureSingleView();

    }


    public void clear() {
//        if (permissionManager != null)
//            permissionManager.release();
        if (tirePressureManager != null) {
            tirePressureManager.clear();
            tirePressureManager = null;
        }
    }

    public void checkPermission() {
        //检查是否有胎压权限，如果有，则显示胎压
        PermissionManager.PermissionResult result1 = permissionManager.checkPermission(PermissionKey.PERMISSION_TIRE_PRESSURE);
        if (result1.isValid) {
            //单一胎压
            if (tirePressureManager.isTirePressuresOK())
                getView().showTirePresstureSingleNormal();
            else
                getView().showTirePresstureSingleWarning();
            //四轮胎压
            TirePressureBean[] tirePressureBeenArray = tirePressureManager.getTirePressures();
            getView().showTirePresstureFour(tirePressureBeenArray);
        }

        //检查是否有车辆数据权限，如果没有，则弹出 试用提醒浮层
        PermissionManager.PermissionResult result2 = permissionManager.checkPermission(PermissionKey.PERMISSION_CAR_DATA);
        IPermissionAlertViewAble permissionAlertViewAble = getView();
        if (!result2.isValid) {
            permissionAlertViewAble.showPermissionAlertView_FreeTrial(result2.expired, result2.numberOfDay);
        } else {
            permissionAlertViewAble.hidePermissionAlertView_FreeTrial();
        }
    }
}
