package com.mapbar.android.obd.rearview.modules.permission.model;

import com.mapbar.android.obd.rearview.lib.eventbus.EventBusEvent;

import java.io.Serializable;

/**
 * 本地权限的存储发生了变化
 * 这是一个 EventBus消息
 * Created by zhangyunfei on 16/8/15.
 */
public class PermissionBuyEvent extends EventBusEvent {
    private boolean isSuccess;

    /**
     * 购买成功
     *
     * @return
     */
    public boolean isBuySuccess() {
        return isSuccess;
    }

    public PermissionBuyEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
