package com.mapbar.android.obd.rearview.modules.vin.events;

import com.mapbar.android.obd.rearview.lib.eventbus.EventBusEvent;

/**
 * Eventbus事件：Vin 修改 成功
 * Created by zhangyunfei on 16/8/26.
 */
public class VinChangeSucccessEvent extends EventBusEvent {
    public int type;
    public int state;
    public String userId;
    public String token;

    public VinChangeSucccessEvent(int type, int state, String userId, String token) {
        this.type = type;
        this.state = state;
        this.userId = userId;
        this.token = token;
    }
}
