package com.mapbar.android.obd.rearview.lib.push;

import com.mapbar.android.obd.rearview.framework.manager.UserCenterManager;
import com.mapbar.android.obd.rearview.modules.setting.ChangePhoneEvent_RegisterOK;
import com.mapbar.android.obd.rearview.modules.setting.ChangePhoneEvent_ScanOK;

import org.greenrobot.eventbus.EventBus;

/**
 * 更改手机号，推送消息 handler
 * 判断消息，根据不同，分发消息。
 *
 * 在本类收到 用户注册成功 的消息时
 *
 * 使用了 eventbus
 * Created by zhangyunfei on 16/7/29.
 */
public class ChangePhonePushMessageDispatcher {

    public static void handlePushMessage(int type, int state, String userId, String token) {
        if (type == PushType.SCAN_OK && state == PushState.SUCCESS) {
            //收到推送 扫码成功
            //在修改手机号，会订阅 ChangePhoneEvent_ScanOK 类型的 eventbus消息
            EventBus.getDefault().post(new ChangePhoneEvent_ScanOK(type, state, userId, token));
        } else if (type == PushType.SCAN_REGISTER && state == PushState.SUCCESS) {
            //更新本地用户信息
//            UserCenterManager.getInstance().updateUserInfoByRemoteLogin(userId, null, token, "zs");

            //收到推送 填写并注册成功
            //在修改手机号，会订阅 ChangePhoneEvent_ScanOK 类型的 eventbus消息
            EventBus.getDefault().post(new ChangePhoneEvent_RegisterOK(type, state, userId, token));
        }
    }
}
