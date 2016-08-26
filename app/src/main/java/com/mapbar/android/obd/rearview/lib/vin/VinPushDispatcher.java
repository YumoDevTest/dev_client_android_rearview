package com.mapbar.android.obd.rearview.lib.vin;

import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.lib.vin.events.VinChangeFailureEvent;
import com.mapbar.android.obd.rearview.lib.vin.events.VinChangeSucccessEvent;
import com.mapbar.android.obd.rearview.lib.vin.events.VinScanEvent;

/**
 * Vin相关的推送消息 分发器
 * Created by zhangyunfei on 16/8/26.
 */
public class VinPushDispatcher {
    private static final String TAG = "PUSH";

    public static void onReceivePushMessage(int type, int state, String userId, String token) {
        /**
         * type  0 扫码  1 注册  2 绑定vin 3 vin扫码
         * state 1 成功  2 失败  3 已注册
         */
        if (type == 3) {
            if (state == 1) {
                EventBusManager.post(new VinScanEvent());
            }
        } else if (type == 2) {
            if (state == 1) {
                EventBusManager.post(new VinChangeSucccessEvent());
            } else {
                EventBusManager.post(new VinChangeFailureEvent());
            }
        }
    }
}
