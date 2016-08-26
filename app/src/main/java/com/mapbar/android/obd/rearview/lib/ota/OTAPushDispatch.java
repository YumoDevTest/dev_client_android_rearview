//package com.mapbar.android.obd.rearview.lib.ota;
//
//import com.mapbar.android.obd.rearview.framework.log.Log;
//import com.mapbar.android.obd.rearview.framework.log.LogTag;
//import com.mapbar.android.obd.rearview.lib.push.PushState;
//import com.mapbar.android.obd.rearview.lib.push.PushType;
//import com.mapbar.android.obd.rearview.lib.push.events.ChangePhoneEvent_ScanOK;
//import com.mapbar.obd.Manager;
//import com.mapbar.obd.serial.utils.LogHelper;
//
//import org.greenrobot.eventbus.EventBus;
//
///**
// * Created by zhangyunfei on 16/8/25.
// */
//public class OTAPushDispatch {
//
//
//    private static final String TAG = "VIN";
//
//    public static void onReiceivePushData(int type, int state, String userId, String token) {
//        /**
//         * type  0 扫码  1 注册  2 绑定vin 3 vin扫码
//         * state 1 成功  2 失败  3 已注册
//         */
//        switch (type) {
//            case 3:
//                if (state == 1) {
//                    LogHelper.d(TAG, String.format("## 收到推送,type=%s,state=%s", type, state));
//                    showRegQr(scan_succ);
//                }
//                break;
//            case 2:
//                if (state == 1) {
//
//                    Manager.getInstance().queryRemoteUserCar();
//                    baseObdListener.onEvent(EVENT_OBD_USER_BINDVIN_SUCC, null);
//                } else {
//                    baseObdListener.onEvent(EVENT_OBD_USER_BINDVIN_FAILED, null);
//                }
//                break;
//
//        }
//    }
//
//
//    public static void onReceivePushMessage(int type, int state, String userId, String token) {
//        if (type == PushType.SCAN_OK && state == PushState.SUCCESS) {
//            //收到推送 扫码成功
//            //在修改手机号，会订阅 ChangePhoneEvent_ScanOK 类型的 eventbus消息
//            EventBus.getDefault().post(new ChangePhoneEvent_ScanOK(type, state, userId, token));
//        }
//    }
//}
