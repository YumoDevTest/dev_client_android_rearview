package com.mapbar.android.obd.rearview.modules.setting;

/**
 * 更改手机号，eventbus 事件
 * Created by zhangyunfei on 16/7/26.
 */
public class ChangePhoneMessageEvent {
    int type;
    int state;
    String userId;
    String token;

    public ChangePhoneMessageEvent(int type, int state, String userId, String token) {
        this.type = type;
        this.state = state;
        this.userId = userId;
        this.token = token;
    }
}
