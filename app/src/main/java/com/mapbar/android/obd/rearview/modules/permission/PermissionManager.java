package com.mapbar.android.obd.rearview.modules.permission;

import com.mapbar.box.protobuf.bean.ObdRightBean;
import com.mapbar.obd.OBDFuncRightData;
import com.mapbar.obd.UserCenterError;

import java.io.InvalidObjectException;
import java.util.List;

/**
 * 权限管理类
 * Created by zhangyunfei on 16/8/5.
 */
public interface PermissionManager {


    void downloadPermissionList(DownloadPermissionCallback callback);

    public PermissionResult checkPermission(int permissionKey);

    public PermissionSummary getPermissionSummary();


    public interface DownloadPermissionCallback {
        void onSuccess(List<ObdRightBean.ObdRight> permissionList);

        void onFailure(Exception ex);
    }


    /**
     * 权限摘要
     */
    public static class PermissionSummary {
        public static final int NO_PERSSION = 0;
        public static final int HAS_PAY = 1;
        public static final int TRAIL = 2;

        public int summary;
        public boolean expired;
        public int numberOfDay;

        public PermissionSummary() {
        }

        public PermissionSummary(int summary) {
            this.summary = summary;
        }
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


