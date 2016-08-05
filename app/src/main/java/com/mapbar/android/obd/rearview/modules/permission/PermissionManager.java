package com.mapbar.android.obd.rearview.modules.permission;

import com.mapbar.obd.OBDFuncRightData;
import com.mapbar.obd.UserCenterError;

import java.io.InvalidObjectException;

/**
 * 权限管理类
 * Created by zhangyunfei on 16/8/5.
 */
public interface PermissionManager {
    void downloadPermissionList(DownloadPermissionCallback callback) throws InvalidObjectException;

    public PermissionResult checkPermission(int permissionKey);


    public interface DownloadPermissionCallback {
        void onSuccess(OBDFuncRightData result);

        void onFuncRightServerFailed(UserCenterError userCenterError);

        void onFailure(UserCenterError userCenterError);
    }


    /**
     * 权限结果
     */
    public static class PermissionResult {

        public boolean expired;//是否试用过期
        public int numberOfDay;//剩余试用天数
        public int permissionKey;//权限id
        public boolean isValid;//是否具有此权限

        public PermissionResult() {
        }

        public PermissionResult(boolean isValid, boolean expired, int numberOfDay, int permissionKey) {
            this.expired = expired;
            this.numberOfDay = numberOfDay;
            this.permissionKey = permissionKey;
            this.isValid = isValid;
        }
    }
}


