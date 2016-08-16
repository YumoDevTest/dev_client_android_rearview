package com.mapbar.android.obd.rearview.modules.common;

import android.app.Activity;
import android.os.Bundle;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.modules.permission.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;

public class Demo1Activity extends Activity {
    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo1_activity);

        permissionManager = LogicFactory.createPermissionManager(this);

        //检查 是否具有体检权限，如果有，才会显示 故障码
        PermissionManager.PermissionResult permission4Checkup =
                permissionManager.checkPermission(PermissionKey.PERMISSION_CHECK_UP);
//        getView().setCarStateRecordVisiable(permission4Checkup.isValid);

    }
}
