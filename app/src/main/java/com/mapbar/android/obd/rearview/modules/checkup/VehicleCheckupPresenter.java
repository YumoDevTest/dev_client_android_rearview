package com.mapbar.android.obd.rearview.modules.checkup;

import com.mapbar.obd.foundation.eventbus.EventBusManager;
import com.mapbar.obd.foundation.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.modules.checkup.contract.IVehicleCheckupView;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.model.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;
import com.mapbar.android.obd.rearview.modules.push.events.PermissionChangedEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zhangyunfei on 16/8/4.
 */
public class VehicleCheckupPresenter extends BasePresenter<IVehicleCheckupView>{
    private PermissionManager permissionManager;

    public VehicleCheckupPresenter(IVehicleCheckupView view) {
        super(view);

        permissionManager = LogicFactory.createPermissionManager(getView().getContext());
        EventBusManager.register(this);
    }

    public void clear() {
        permissionManager = null;
        EventBusManager.unregister(this);
    }

    public void checkPermission() {
        PermissionManager.PermissionResult permission4State = permissionManager.checkPermission(PermissionKey.PERMISSION_CHECK_UP);
        IPermissionAlertViewAble permissionAlertViewAble = getView();
        if (!permission4State.isValid) {
            permissionAlertViewAble.showPermissionAlertView_FreeTrial(permission4State.expired, permission4State.numberOfDay);
        }else {
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
