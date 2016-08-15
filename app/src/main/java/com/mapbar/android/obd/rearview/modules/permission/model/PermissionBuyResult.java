package com.mapbar.android.obd.rearview.modules.permission.model;

import java.io.Serializable;

/**
 * 本地权限的存储发生了变化
 * Created by zhangyunfei on 16/8/15.
 */
public class PermissionBuyResult implements Serializable {
    private boolean isSuccess;

    /**
     * 购买成功
     * @return
     */
    public boolean isBuySuccess() {
        return isSuccess;
    }

    public PermissionBuyResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
