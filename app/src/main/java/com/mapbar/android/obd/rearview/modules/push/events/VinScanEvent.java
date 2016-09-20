package com.mapbar.android.obd.rearview.modules.push.events;

import com.mapbar.obd.foundation.eventbus.EventBusEvent;

/**
 * Eventbus事件：Vin扫码成功
 * Created by zhangyunfei on 16/8/26.
 */
public class VinScanEvent extends EventBusEvent {
    public int type;
    public int state;
    public String userId;
    public String token;

    public VinScanEvent(int type, int state, String userId, String token) {
        this.type = type;
        this.state = state;
        this.userId = userId;
        this.token = token;
    }
}
