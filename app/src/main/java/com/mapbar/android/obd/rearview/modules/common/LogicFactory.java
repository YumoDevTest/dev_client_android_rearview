package com.mapbar.android.obd.rearview.modules.common;

import com.mapbar.android.obd.rearview.modules.cardata.TirePressureManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;

/**
 * 业务工厂类。以构建 各个业务类。试用了 静态工厂模式
 * Created by zhangyunfei on 16/8/5.
 */
public class LogicFactory {
    private LogicFactory() {
    }

    /**获得 胎压 业务操作类
     * @return
     */
    public static TirePressureManager createTirePressureManager() {
        return new TirePressureManager();
    }

    /**获得 权限管理 业务操作类
     * @return
     */
    public static PermissionManager createPermissionManager() {
        return new PermissionManager();
    }
}
