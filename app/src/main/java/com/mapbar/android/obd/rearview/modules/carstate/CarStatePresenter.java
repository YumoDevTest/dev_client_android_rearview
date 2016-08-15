package com.mapbar.android.obd.rearview.modules.carstate;

import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.modules.carstate.contract.ICarStateView;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;
import com.mapbar.android.obd.rearview.modules.permission.model.PermissionRepositoryChanged;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 车辆状态 presenter
 * Created by zhangyunfei on 16/8/4.
 */
public class CarStatePresenter extends BasePresenter<ICarStateView> {

    private PermissionManager permissionManager;

    public CarStatePresenter(ICarStateView view) {
        super(view);

        permissionManager = LogicFactory.createPermissionManager(getView().getContext());

        EventBusManager.register(this);
    }

    public void clear() {
        permissionManager = null;
        EventBusManager.unregister(this);
    }

    public void checkPermission() {
        //是否具有 车辆状态权限
        PermissionManager.PermissionResult permission4State = permissionManager.checkPermission(PermissionKey.PERMISSION_CAR_STATE);
        IPermissionAlertViewAble permissionAlertViewAble = getView();
        if (!permission4State.isValid) {
            permissionAlertViewAble.showPermissionAlertView_FreeTrial(permission4State.expired, permission4State.numberOfDay);
        }else {
            permissionAlertViewAble.hidePermissionAlertView_FreeTrial();
        }
        //检查 是否具有体检权限，如果有，才会显示 故障码
        PermissionManager.PermissionResult permission4Checkup = permissionManager.checkPermission(PermissionKey.PERMISSION_CHECK_UP);
        getView().setCarStateRecordVisiable(permission4Checkup.isValid);

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
