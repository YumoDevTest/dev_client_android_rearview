package com.mapbar.android.obd.rearview.modules.permission;

import com.mapbar.android.obd.rearview.obd.util.LogUtil;

import java.io.InvalidObjectException;

/**
 * 伪造的权限管理类。在停止进行权限管理的试用采用此类
 * Created by zhangyunfei on 16/8/5.
 */
public class PermissionManagerFake implements PermissionManager {
    private static final String TAG = PermissionManagerFake.class.getSimpleName();

    @Override
    public void downloadPermissionList(DownloadPermissionCallback callback) throws InvalidObjectException {
        LogUtil.d(TAG, "采用PermissionManagerFake，停止使用权限管理");
    }

    @Override
    public PermissionResult checkPermission(int permissionKey) {
        //都返回有权限
        return new PermissionResult(true, false, Integer.MAX_VALUE, 0);
    }
}
