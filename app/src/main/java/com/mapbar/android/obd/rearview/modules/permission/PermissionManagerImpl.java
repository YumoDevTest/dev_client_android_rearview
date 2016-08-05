package com.mapbar.android.obd.rearview.modules.permission;

import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.obd.util.SDKConnection;
import com.mapbar.obd.Manager;
import com.mapbar.obd.OBDFuncRightData;
import com.mapbar.obd.UserCenterError;

import java.io.InvalidObjectException;

/**
 * 权限管理类
 * Created by zhangyunfei on 16/8/4.
 */
public class PermissionManagerImpl implements PermissionManager {

    public PermissionManagerImpl() {
    }


    @Override
    public void downloadPermissionList(final DownloadPermissionCallback callback) throws InvalidObjectException {
        final SDKConnection sdkConnection = new SDKConnection();
        sdkConnection.startConnectSDK(new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object data) {
                switch (event) {
                    case Manager.Event.FuncRightSucc: {
                        OBDFuncRightData result = (OBDFuncRightData) data;
                        if (result == null) {
                            //无法获取有效授权信息
                            if (callback != null)
                                callback.onSuccess(result);
                        } else {
                            //获取授权信息成功
                            if (callback != null)
                                callback.onSuccess(result);

                        }
                    }
                    case Manager.Event.FuncRightServerFailed: {
                        UserCenterError userCenterError = (UserCenterError) data;
                        //远程获取授权信息失败
                        if (callback != null)
                            callback.onFuncRightServerFailed(userCenterError);
                    }
                    case Manager.Event.FuncRightFailed: {
                        UserCenterError userCenterError = (UserCenterError) data;
                        // "获取授权信息失败:
                        if (callback != null)
                            callback.onFailure(userCenterError);
                    }
                    if (sdkConnection != null)
                        sdkConnection.stopConnectSDK();
                }
            }
        });


        String imei = Utils.getImei(MainActivity.getInstance());
        Manager.getInstance().GetFuncRightInfo(imei);
    }

    /**
     * 检查权限
     *
     * @param permissionKey
     * @return
     */
    @Override
    public PermissionResult checkPermission(int permissionKey) {
        if (permissionKey == PermissionKey.PERMISSION_CAR_STATE)
            return new PermissionResult(true, true, 10, permissionKey);
        return new PermissionResult(false, true, 10, permissionKey);
    }


}
