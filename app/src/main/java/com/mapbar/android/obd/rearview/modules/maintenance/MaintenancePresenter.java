package com.mapbar.android.obd.rearview.modules.maintenance;

import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.maintenance.contract.IMaintenanceView;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;
import com.mapbar.android.obd.rearview.modules.permission.model.PermissionRepositoryChanged;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 保养 presenter
 * Created by zhangyunfei on 16/8/4.
 */
public class MaintenancePresenter extends BasePresenter<IMaintenanceView> {

    private PermissionManager permissionManager;

    public MaintenancePresenter(IMaintenanceView view) {
        super(view);

        permissionManager = LogicFactory.createPermissionManager(getView().getContext());
        EventBusManager.register(this);
    }

    public void clear() {
        permissionManager = null;
        EventBusManager.unregister(this);
    }

    public void checkPermission() {
        PermissionManager.PermissionResult permission4State = permissionManager.checkPermission(PermissionKey.PERMISSION_MAINTENANCE);
        IPermissionAlertViewAble permissionAlertViewAble = getView();
        if (!permission4State.isValid) {
            permissionAlertViewAble.showPermissionAlertView_FreeTrial(permission4State.expired, permission4State.numberOfDay);
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
    public void onEvent(PermissionRepositoryChanged event) {
        checkPermission();
    }
}
