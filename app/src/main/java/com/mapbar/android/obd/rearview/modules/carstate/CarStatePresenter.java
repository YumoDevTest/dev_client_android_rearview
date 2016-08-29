package com.mapbar.android.obd.rearview.modules.carstate;

import android.content.Intent;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.lib.ota.CheckVersionBean;
import com.mapbar.android.obd.rearview.lib.ota.FirmwareVersionChecker;
import com.mapbar.android.obd.rearview.lib.vin.VinManager;
import com.mapbar.android.obd.rearview.lib.vin.events.VinChangeFailureEvent;
import com.mapbar.android.obd.rearview.lib.vin.events.VinChangeSucccessEvent;
import com.mapbar.android.obd.rearview.lib.vin.events.VinScanEvent;
import com.mapbar.android.obd.rearview.modules.carstate.contract.ICarStateView;
import com.mapbar.android.obd.rearview.modules.carstate.contract.IVinChangeView;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.ota.OtaAlertActivity;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;
import com.mapbar.android.obd.rearview.modules.permission.model.PermissionChangedEvent;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.Manager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * 车辆状态 presenter
 * Created by zhangyunfei on 16/8/4.
 */
public class CarStatePresenter extends BasePresenter<ICarStateView> {

    private PermissionManager permissionManager;
    private IVinChangeView vinChangeView;
    private FirmwareVersionChecker firmwareVersionChecker;
    private boolean isCheckedVersion = false;//是否检查过版本

    public CarStatePresenter(ICarStateView view) {
        super(view);
        if (view instanceof IVinChangeView) {
            vinChangeView = (IVinChangeView) view;
        }
        permissionManager = LogicFactory.createPermissionManager(getView().getContext());

        firmwareVersionChecker = new FirmwareVersionChecker();
        EventBusManager.register(this);


        beginCheckFirmwareVersion();
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
        } else {
            permissionAlertViewAble.hidePermissionAlertView_FreeTrial();
        }
        //检查 是否具有体检权限，如果有，才会显示 故障码
        PermissionManager.PermissionResult permission4Checkup = permissionManager.checkPermission(PermissionKey.PERMISSION_CHECK_UP);
        getView().setCarStateRecordVisiable(permission4Checkup.isValid);

    }

    /**
     * Eventbus 订阅者：当本地权限发生变化时，这是一个Eventbus订阅者
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PermissionChangedEvent event) {
        checkPermission();
    }

    /**
     * Eventbus 订阅者：VIN 修改成功
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VinChangeSucccessEvent event) {
        Manager.getInstance().queryRemoteUserCar();
//        getView().alert("VIN修改成功");
        if (vinChangeView != null) vinChangeView.hideVinInputDialog();
    }

    /**
     * Eventbus 订阅者：VIN 修改 失败
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VinChangeFailureEvent event) {
        Manager.getInstance().queryRemoteUserCar();
        getView().alert("VIN修改失败");
        if (vinChangeView != null) vinChangeView.showVinInputDialog();
    }

    /**
     * Eventbus 订阅者：VIN 扫码成功
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VinScanEvent event) {
        if (vinChangeView != null) vinChangeView.showVinScanOK();
    }

    public void notifyBeginCheckFirmwareVersion() {
        if (isCheckedVersion)
            return;
        LogUtil.d("OTA", "## 准备检查固件版本");
//        beginCheckFirmwareVersion();
    }

    /**
     * 检查固件版本,是否需要更新固件
     */
    private void beginCheckFirmwareVersion() {
//        try {
//            VinManager vinManager = new VinManager();
//            //如果 vin为空，则弹窗给用户促使用户录入vin。否则开始启动检测version
//            if (TextUtils.isEmpty(vinManager.getVin())) {
//                if (vinChangeView != null)
//                    vinChangeView.showVinInputDialog();
//            } else {
        //发送请求，检查版本，如果有新版本则默默下载，并通知 。如果已下载过，则通知
        firmwareVersionChecker.checkVersion(new FirmwareVersionChecker.VersionCheckCallback() {
            @Override
            public void onFoundNewVersion(File binFile, CheckVersionBean versionBean) {
                Intent intent = new Intent(getView().getContext(), OtaAlertActivity.class);
                intent.putExtra("firewware_bin_file", binFile.getPath());
                intent.putExtra("is_fouce_upgreade", versionBean.bin_must_update == 1);
                getView().getContext().startActivity(intent);

                isCheckedVersion = true;
            }

            @Override
            public void noNothing() {
                isCheckedVersion = true;
            }

            @Override
            public void onError(Exception e) {
                getView().alert(e.getMessage());
            }
        });
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            getView().alert(ex.getMessage());
//        }
    }

}
