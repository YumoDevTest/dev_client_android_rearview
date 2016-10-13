package com.mapbar.android.obd.rearview.modules.maintenance;

import android.content.Context;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.services.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.modules.maintenance.contract.IMaintenanceReviseView;
import com.mapbar.android.obd.rearview.modules.maintenance.contract.MaintenanceErrorCode;
import com.mapbar.android.obd.rearview.modules.maintenance.model.MaintenanceData;
import com.mapbar.android.obd.rearview.util.StringUtil;
import com.mapbar.mapdal.DateTime;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCar;
import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.foundation.mvp.BasePresenter;

/**
 * 保养校正页面Presenter类
 * Created by zhangyh on 2016/9/23.
 */
public class CarMaintenanceRevisePresenter extends BasePresenter<IMaintenanceReviseView> {
    private UserCar userCar;
    private OBDSDKListenerManager.SDKListener sdkListener;
    private Context context;

    public CarMaintenanceRevisePresenter(IMaintenanceReviseView view) {
        super(view);
        context = getView().getContext();
        setOnListener();
    }

    /**
     * 获取数据及判断是否有效时间,并设置给view
     * @return
     */
    public MaintenanceData loadLastData() {
        MaintenanceData maintenanceData = new MaintenanceData("0", "0", "未设置", "未设置");
        LocalUserCarResult localUserCar = Manager.getInstance().queryLocalUserCar();
        UserCar[] userCars = localUserCar.userCars;
        if (userCars.length > 0) {
            userCar = localUserCar.userCars[0];
            if (userCar.purchaseDate.isValid()) {
                maintenanceData.setBoolPurchaseDate(true);
                maintenanceData.setPurchaseDate(String.valueOf(userCar.purchaseDate.year + "-" + userCar.purchaseDate.month + "-" + userCar.purchaseDate.day));
            } else {
                maintenanceData.setBoolPurchaseDate(false);
                maintenanceData.setPurchaseDate("未设置");
            }

            if (userCar.lastMaintenanceDate.isValid()) {
                maintenanceData.setBoolLastMaintenanceDate(true);
                maintenanceData.setLastMaintenanceDate(String.valueOf(userCar.lastMaintenanceDate.year + "-" + userCar.lastMaintenanceDate.month + "-" + userCar.lastMaintenanceDate.day));
            } else {
                maintenanceData.setBoolLastMaintenanceDate(false);
                maintenanceData.setLastMaintenanceDate("未设置");
            }
            if (userCar.totalMileage > 0)
                maintenanceData.setTotalMileage(String.valueOf(userCar.totalMileage / 1000));
            maintenanceData.setLastMaintenanceMileage(String.valueOf(userCar.lastMaintenanceMileage / 1000));
        }
        getView().setMaintenanceData(maintenanceData);
        return maintenanceData;
    }


    public void saveData() {
        MaintenanceData maintenanceData = getView().getMaintenanceData();
        if (maintenanceData.getPurchaseDate().trim().equals(context.getResources().getString(R.string.isnotset)) ||
                maintenanceData.getLastMaintenanceDate().trim().equals(context.getResources().getString(R.string.isnotset))
                || TextUtils.isEmpty(maintenanceData.getTotalMileage()) || TextUtils
                .isEmpty(maintenanceData.getLastMaintenanceMileage())) {
            StringUtil.toastStringShort("信息不完整");
        } else if (Integer.valueOf(maintenanceData.getTotalMileage().trim()) > 192500) {
            StringUtil.toastStringShort("行驶里程超出最大范围");
        } else {
            userCar.totalMileage = Integer.valueOf(maintenanceData.getTotalMileage().trim()) * 1000;
            userCar.lastMaintenanceMileage = Integer.valueOf(maintenanceData.getLastMaintenanceMileage().trim()) * 1000;
            userCar.purchaseDate = parseToDateTime(maintenanceData.getPurchaseDate());
            userCar.lastMaintenanceDate = parseToDateTime(maintenanceData.getLastMaintenanceDate());
            Manager.getInstance().setUserCar(userCar);
        }
    }
    private DateTime parseToDateTime(String timeStr) {
        String time[] = timeStr.split("-");
        int year = Integer.parseInt(time[0]);
        int monty = (Integer.parseInt(time[1]));
        int date = Integer.parseInt(time[2]);
        DateTime lastTime = new DateTime();
        lastTime.year = (short) year;
        lastTime.month = (short) monty;
        lastTime.day = (short) date;
        return lastTime;
    }

    private void setOnListener() {
        if(sdkListener == null)
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                switch (event) {
                    //车辆信息上传服务器失败
                    case Manager.Event.carInfoUploadFailed:
                        getView().alert("设置失败,"+ MaintenanceErrorCode.getErrorInfo(context,(Integer) o));
                        break;
                    //车辆信息写入数据库成功
                    case Manager.Event.carInfoWriteDatabaseSucc:
                        getView().alert("设置成功");
                        getView().onSaveDataSuccess();
                        break;
                    //车辆信息写入数据库失败
                    case Manager.Event.carInfoWriteDatabaseFailed:
                        getView().alert("设置失败,"+ MaintenanceErrorCode.getErrorInfo(context,(Integer) o));
                        break;
                }
            }
        };
        OBDSDKListenerManager.getInstance().addSdkListener(sdkListener);
    }

    @Override
    public void clear() {
        OBDSDKListenerManager.getInstance().removeSdkListener(sdkListener);

    }

}
