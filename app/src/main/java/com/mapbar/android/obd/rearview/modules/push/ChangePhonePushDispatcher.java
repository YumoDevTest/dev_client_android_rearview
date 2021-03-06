package com.mapbar.android.obd.rearview.modules.push;

import com.mapbar.obd.foundation.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.modules.push.events.ChangePhoneEvent_RegisterFailure;
import com.mapbar.android.obd.rearview.modules.push.events.ChangePhoneEvent_RegisterOK;
import com.mapbar.android.obd.rearview.modules.push.events.ChangePhoneEvent_ScanOK;
import com.mapbar.android.obd.rearview.modules.push.events.PermissionBuyEvent;
import com.mapbar.obd.foundation.push.PushState;
import com.mapbar.obd.foundation.push.PushType;

/**
 * 更改手机号，推送消息 handler
 * 判断消息，根据不同，分发消息。
 * <p/>
 * 在本类收到 用户注册成功 的消息时
 * <p/>
 * 使用了 eventbus
 * Created by zhangyunfei on 16/7/29.
 */
public class ChangePhonePushDispatcher {

    public static void onReceivePushMessage(int type, int state, String userId, String token) {
        if (type == PushType.SCAN_OK && state == PushState.SUCCESS) {
            //收到推送 扫码成功
            //在修改手机号，会订阅 ChangePhoneEvent_ScanOK 类型的 eventbus消息
            EventBusManager.post(new ChangePhoneEvent_ScanOK(type, state, userId, token));
        } else if (type == PushType.SCAN_REGISTER && state == PushState.SUCCESS) {
            //更新本地用户信息
//            UserCenterManager.create().updateUserInfoByRemoteLogin(userId, null, token, "zs");

            //收到推送 填写并注册成功
            //在修改手机号，会订阅 ChangePhoneEvent_ScanOK 类型的 eventbus消息
            EventBusManager.post(new ChangePhoneEvent_RegisterOK(type, state, userId, token));
        } else if (type == PushType.SCAN_REGISTER && (state == PushState.FAILURE) || state == PushState.REGISTERED) {
            EventBusManager.post(new ChangePhoneEvent_RegisterFailure(type, state, userId, token));
        } else if (type == PushType.BUY) {
            //功能收费 购买 是否成功
            boolean isSUccess = false;
            if (state == PushState.SUCCESS)
                isSUccess = true;
            EventBusManager.post(new PermissionBuyEvent(isSUccess));
        }
    }

}
