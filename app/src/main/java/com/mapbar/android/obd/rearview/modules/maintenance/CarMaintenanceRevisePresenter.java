package com.mapbar.android.obd.rearview.modules.maintenance;

import android.text.TextUtils;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.services.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.modules.maintenance.contract.IMaintenanceReviseView;
import com.mapbar.android.obd.rearview.modules.maintenance.model.MaintenanceData;
import com.mapbar.android.obd.rearview.util.StringUtil;
import com.mapbar.mapdal.DateTime;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCar;
import com.mapbar.obd.foundation.mvp.BasePresenter;

/**
 * 保养校正页面Presenter类
 * Created by zhangyh on 2016/9/23.
 */
public class CarMaintenanceRevisePresenter extends BasePresenter<IMaintenanceReviseView> {

    //是否有效时间
    public boolean boolPurchaseDate = false;
    public boolean boolLastMaintenanceDate = false;
    private UserCar userCar;
    private OBDSDKListenerManager.SDKListener sdkListener;

    public CarMaintenanceRevisePresenter(IMaintenanceReviseView view) {
        super(view);
    }

    public void loadLastData() {
        //从数据源头取数据
        //经数据显示到view
        MaintenanceData maintenanceData = new MaintenanceData("0", "0", "未设置", "未设置");
        LocalUserCarResult localUserCar = Manager.getInstance().queryLocalUserCar();
        UserCar[] userCars = localUserCar.userCars;
        if (userCars.length > 0) {
            userCar = localUserCar.userCars[0];
            if (userCar.purchaseDate.isValid()) {
                boolPurchaseDate = true;
                maintenanceData.purchaseDate = String.valueOf(userCar.purchaseDate.year + "-" + userCar.purchaseDate.month + "-" + userCar.purchaseDate.day);
            } else {
                boolPurchaseDate = false;
                maintenanceData.purchaseDate = "未设置";
            }

            if (userCar.lastMaintenanceDate.isValid()) {
                boolLastMaintenanceDate = true;
                maintenanceData.lastMaintenanceDate = String.valueOf(userCar.lastMaintenanceDate.year + "-" + userCar.lastMaintenanceDate.month + "-" + userCar.lastMaintenanceDate.day);
            } else {
                boolLastMaintenanceDate = false;
                maintenanceData.lastMaintenanceDate = "未设置";
            }
            if (userCar.totalMileage > 0)
                maintenanceData.totalMileage = String.valueOf(userCar.totalMileage / 1000);
            maintenanceData.lastMaintenanceMileage = String.valueOf(userCar.lastMaintenanceMileage / 1000);
        }
        getView().setMaintenanceData(maintenanceData);
    }


    public void saveData() {
        MaintenanceData maintenanceData = getView().getMaintenanceData();
        if (maintenanceData.purchaseDate.trim().equals(getView().getContext().getResources().getString(R.string.isnotset)) ||
                maintenanceData.lastMaintenanceDate.trim().equals(getView().getContext().getResources().getString(R.string.isnotset))
                || TextUtils.isEmpty(maintenanceData.totalMileage) || TextUtils
                .isEmpty(maintenanceData.lastMaintenanceMileage)) {
            StringUtil.toastStringShort("信息不完整");
        } else if (Integer.valueOf(maintenanceData.totalMileage.trim()) > 192500) {
            StringUtil.toastStringShort("行驶里程超出最大范围");
        } else {
            userCar.totalMileage = Integer.valueOf(maintenanceData.totalMileage.trim()) * 1000;
            userCar.lastMaintenanceMileage = Integer.valueOf(maintenanceData.lastMaintenanceMileage.trim()) * 1000;
            Manager.getInstance().setUserCar(userCar);
        }
    }

    public void setUserCarPurchaseDate(DateTime time_buy) {
        userCar.purchaseDate = time_buy;
    }

    public void setUserCarlastMaintenanceDate(DateTime time_buy) {
        userCar.lastMaintenanceDate = time_buy;
    }

    public void setOnListener() {
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                switch (event) {
                    case Manager.Event.carInfoUploadFailed:
                        getView().alert("设置失败");
                        break;
                    case Manager.Event.carInfoWriteDatabaseSucc:
                        getView().alert("设置成功");
                        getView().finishView();
                        break;
                    case Manager.Event.carInfoWriteDatabaseFailed:
                        getView().alert("设置失败");
                        break;
                }
            }
        };
        OBDSDKListenerManager.getInstance().addSdkListener(sdkListener);
    }

    @Override
    public void clear() {

    }

}
