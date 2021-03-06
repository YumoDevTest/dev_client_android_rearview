package com.mapbar.android.obd.rearview.modules.common;

import android.content.Context;

import com.mapbar.android.obd.rearview.BuildConfig;
import com.mapbar.android.obd.rearview.lib.notify.NotificationManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManagerFake;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManagerImpl;

/**
 * 业务工厂类。以构建 各个业务类。试用了 静态工厂模式
 * Created by zhangyunfei on 16/8/5.
 */
public class LogicFactory {
    private static NotificationManager notificationManager;

    private LogicFactory() {
    }

//    /**
//     * 获得 胎压 业务操作类
//     *
//     * @return
//     */
//    public static TirePressureManager createTirePressureManager() {
//        return new TirePressureManager();
//    }

    /**
     * 获得 权限管理 业务操作类
     *
     * @return
     */
    public static PermissionManager createPermissionManager(Context context) {
        if (BuildConfig.IS_ENABLE_PERMISSION)
            return new PermissionManagerImpl(context);
        else
            return new PermissionManagerFake();

    }

    public static NotificationManager getNotifycationManager() {
        if (notificationManager == null) {
            notificationManager = new NotificationManager();
        }
        return notificationManager;
    }
}
