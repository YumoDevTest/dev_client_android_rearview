package com.mapbar.android.obd.rearview.modules.permission;

/**
 * 权限管理类
 * Created by zhangyunfei on 16/8/4.
 */
public class PermissionManager {

    /**
     * 检查权限
     *
     * @param permissionKey
     * @return
     */
    public PermissionResult checkPermission(int permissionKey) {
        return new PermissionResult(false, true, 10, permissionKey);
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
