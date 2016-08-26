package com.mapbar.android.obd.rearview.modules.setting.events;

import com.mapbar.android.obd.rearview.lib.eventbus.EventBusEvent;

import java.io.Serializable;

/**
 * 收到推送 填写并注册失败
 * 更改手机号，eventbus 事件
 * Created by zhangyunfei on 16/7/26.
 */
public class ChangePhoneEvent_RegisterFailure extends EventBusEvent{
    public int type;
    public int state;
    public String userId;
    public String token;

    public ChangePhoneEvent_RegisterFailure(int type, int state, String userId, String token) {
        this.type = type;
        this.state = state;
        this.userId = userId;
        this.token = token;
    }
}
