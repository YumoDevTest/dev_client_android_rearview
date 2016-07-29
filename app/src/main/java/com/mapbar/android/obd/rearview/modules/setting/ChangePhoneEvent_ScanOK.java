package com.mapbar.android.obd.rearview.modules.setting;

/**
 * 收到推送 扫码成功
 * 更改手机号，eventbus 事件
 * Created by zhangyunfei on 16/7/26.
 */
public class ChangePhoneEvent_ScanOK {
    public int type;
    public int state;
    public String userId;
    public String token;

    public ChangePhoneEvent_ScanOK(int type, int state, String userId, String token) {
        this.type = type;
        this.state = state;
        this.userId = userId;
        this.token = token;
    }
}
