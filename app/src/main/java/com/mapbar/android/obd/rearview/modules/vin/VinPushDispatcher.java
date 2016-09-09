package com.mapbar.android.obd.rearview.modules.vin;

import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.lib.push.PushState;
import com.mapbar.android.obd.rearview.lib.push.PushType;
import com.mapbar.android.obd.rearview.modules.vin.events.VinChangeFailureEvent;
import com.mapbar.android.obd.rearview.modules.vin.events.VinChangeSucccessEvent;
import com.mapbar.android.obd.rearview.modules.vin.events.VinScanEvent;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.Manager;

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
        if (type == PushType.SCAN_BIND_VIN_CODE) {
            if (state == PushState.SUCCESS) {
                LogUtil.d("PUSH", "## 收到推送：扫描VIN");
                EventBusManager.post(new VinScanEvent(type,state,userId,token));
            }
        } else if (type == PushType.SCAN_BIND_VIN) {
            if (state == PushState.SUCCESS) {
                LogUtil.d("PUSH", "## 收到推送：绑定VIN成功");
                EventBusManager.post(new VinChangeSucccessEvent(type,state,userId,token));
//                Manager.getInstance().queryRemoteUserCar();//更新车辆信息
            } else {
                LogUtil.d("PUSH", "## 收到推送：绑定VIN失败");
                EventBusManager.post(new VinChangeFailureEvent(type,state,userId,token));
            }
        }
    }
}
