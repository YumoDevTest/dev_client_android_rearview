package com.mapbar.android.obd.rearview.modules.carstate;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.mapbar.android.obd.rearview.modules.carstate.contract.ICarStateView;
import com.mapbar.android.obd.rearview.modules.carstate.contract.IVinChangeView;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.ota.OtaAlertActivity;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;
import com.mapbar.android.obd.rearview.modules.permission.model.PermissionKey;
import com.mapbar.android.obd.rearview.modules.push.events.PermissionChangedEvent;
import com.mapbar.android.obd.rearview.modules.push.events.VinChangeFailureEvent;
import com.mapbar.android.obd.rearview.modules.push.events.VinChangeSucccessEvent;
import com.mapbar.android.obd.rearview.modules.push.events.VinScanEvent;
import com.mapbar.obd.Manager;
import com.mapbar.obd.developkit.ota.CheckVersionBean;
import com.mapbar.obd.developkit.ota.FirmwareVersionChecker;
import com.mapbar.obd.developkit.vin.VinManager;
import com.mapbar.obd.foundation.eventbus.EventBusManager;
import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.foundation.mvp.BasePresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * 车辆状态 presenter
 * Created by zhangyunfei on 16/8/4.
 */
public class CarStatePresenter extends BasePresenter<ICarStateView> {

    private static final String TAG = CarStatePresenter.class.getSimpleName();
    private PermissionManager permissionManager;
    private IVinChangeView vinChangeView;
    private FirmwareVersionChecker firmwareVersionChecker;
    private boolean hasNewVersionLastCheck = false;//是否有新版,上次检查的结果
    private boolean hasCheckedVersion = false;//是否检查过版本
    private Handler handler = new Handler();
    public CarStatePresenter(ICarStateView view) {
        super(view);
        if (view instanceof IVinChangeView) {
            vinChangeView = (IVinChangeView) view;
        }
        permissionManager = LogicFactory.createPermissionManager(getView().getContext());

        firmwareVersionChecker = new FirmwareVersionChecker();
        EventBusManager.register(this);

        showOtaAlertText();
        checkVinIsNull();
    }

    /**
     * 检查提示 “当前车型是否支持车辆控制功能”
     */
    public void showOtaAlertText() {
        if (firmwareVersionChecker.isV3SpecialOta()) {
            getView().showOtaAlert_SupportControl();
        } else {
            getView().showOtaAlert_NoSupportControl();
        }
        if (hasNewVersionLastCheck) {
            getView().showOtaAlert_can_upgrade();
        }
    }

    /**
     * 检查vin,如果为空，则弹出输入vin的二维码对话框
     */
    private void checkVinIsNull() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("VinManager", "## 检查二维码");
                VinManager vinManager = new VinManager();
                //如果 vin为空，则弹窗给用户促使用户录入vin。否则开始启动检测version
                if (TextUtils.isEmpty(vinManager.getVin())) {
                    if (vinChangeView != null)
                        vinChangeView.showVinInputDialog();
                }
            }
        }, 2000);

    }

    /**
     * 清理
     */
    public void clear() {
        permissionManager = null;
        EventBusManager.unregister(this);
    }

    /**
     * 检查权限
     */
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
        beginCheckFirmwareVersion();
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
        VinManager vinManager = new VinManager();
        if (TextUtils.isEmpty(vinManager.getVin())) {
            if (vinChangeView != null) vinChangeView.showVinScanOK();
        }
    }


    /**
     * 检查固件版本,是否需要更新固件
     */
    public void beginCheckFirmwareVersion() {
        if (firmwareVersionChecker == null) return;
        if (getView() == null) return;
        LogUtil.d("OTA", "## 准备检查固件版本");
        //发送请求，检查版本，如果有新版本则默默下载，并通知 。如果已下载过，则通知
        try {
            firmwareVersionChecker.checkVersion(getView().getContext(), new FirmwareVersionChecker.VersionCheckCallback() {
                @Override
                public void onFoundNewVersion(File binFile, CheckVersionBean versionBean) {
                    LogUtil.d(TAG, "## 页面跳转：发现新的固件版本");
                    Intent intent = new Intent(getView().getContext(), OtaAlertActivity.class);
                    intent.putExtra("firewware_bin_file", binFile.getPath());
                    intent.putExtra("is_fouce_upgreade", versionBean.bin_must_update == 1);
                    getView().getContext().startActivity(intent);

                    hasCheckedVersion = true;
                    hasNewVersionLastCheck = true;
                    showOtaAlertText();
                }

                @Override
                public void noNothing() {
                    hasCheckedVersion = true;
                    hasNewVersionLastCheck = false;
                }

                @Override
                public void onError(Exception e) {
                    getView().alert(e.getMessage());
                    hasNewVersionLastCheck = false;
                }
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            getView().alert(e.getMessage());
        }
    }

}
