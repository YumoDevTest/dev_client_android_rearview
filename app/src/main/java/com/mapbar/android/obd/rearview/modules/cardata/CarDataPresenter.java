package com.mapbar.android.obd.rearview.modules.cardata;

import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.modules.cardata.contract.ICarDataView;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;
import com.mapbar.android.obd.rearview.modules.permission.model.PermissionChangedEvent;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.obd.Manager;
import com.mapbar.obd.RealTimeData;
import com.mapbar.obd.RealTimeDataTPMSAll;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 车辆数据，呈现器
 * Created by zhangyunfei on 16/8/3.
 */
public class CarDataPresenter extends BasePresenter<ICarDataView> {
    private PermissionManager permissionManager;
    private OBDSDKListenerManager.SDKListener sdkListener;

    public CarDataPresenter(ICarDataView view) {
        super(view);
        permissionManager = LogicFactory.createPermissionManager(getView().getContext());

        EventBusManager.register(this);
    }

    public void clear() {
        permissionManager = null;
        EventBusManager.unregister(this);
    }

    /**
     * 检查权限： OBD普通数据
     */
    public void checkPermission() {
        //检查是否有车辆数据权限，如果没有，则弹出 试用提醒浮层
        PermissionManager.PermissionResult result2 = permissionManager.checkPermission(PermissionKey.PERMISSION_CAR_DATA);
        IPermissionAlertViewAble permissionAlertViewAble = getView();
        if (!result2.isValid) {
            permissionAlertViewAble.showPermissionAlertView_FreeTrial(result2.expired, result2.numberOfDay);
        } else {
            permissionAlertViewAble.hidePermissionAlertView_FreeTrial();
        }
    }

    /**
     * 当本地权限发生变化时，这是一个Eventbus订阅者
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PermissionChangedEvent event) {
        checkPermission();
    }
}