package com.mapbar.android.obd.rearview.modules.push.events;

import com.mapbar.obd.foundation.eventbus.EventBusEvent;

/**
 * 收到推送 填写并注册成功
 * 更改手机号，eventbus 事件
 * Created by zhangyunfei on 16/7/26.
 */
public class ChangePhoneEvent_RegisterOK extends EventBusEvent {
    public int type;
    public int state;
    public String userId;
    public String token;

    public ChangePhoneEvent_RegisterOK(int type, int state, String userId, String token) {
        this.type = type;
        this.state = state;
        this.userId = userId;
        this.token = token;
    }
}
