package com.mapbar.android.obd.rearview.framework.ixintui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ixintui.pushsdk.SdkConstants;
import com.mapbar.android.obd.rearview.framework.control.SDKListenerManager;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;


/**
 * Created by THINKPAD on 2016/3/8.
 */
public class AixintuiBroadcastReceiver extends BroadcastReceiver {

    private SDKListenerManager.SDKListener sdkListener;
    private String token;


    /***
     * 推送消息接收器
     ***/
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // 日志
        if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
            Log.d(LogTag.PUSH, " action " + action);
        }

        // 透传消息
        if (action.equals(SdkConstants.MESSAGE_ACTION)) {
            String msg = intent.getStringExtra(SdkConstants.MESSAGE);
            String extra = intent.getStringExtra(SdkConstants.ADDITION);

            // 日志
            if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                Log.d(LogTag.PUSH, "透传 message received, msg is: " + msg);
                Log.d(LogTag.PUSH, "透传 message received, extra is: " + extra);


            }

            // 处理透传内容
            AixintuiPushManager.getInstance().onAppearMsg(context, msg, extra);

        }
        // SDK API的异步返回结果
        else if (action.equals(SdkConstants.RESULT_ACTION)) {
            // API 名称
            String cmd = intent.getStringExtra(SdkConstants.COMMAND);
            // 返回值，0为成功，否则失败
            int code = intent.getIntExtra(SdkConstants.CODE, 0);
            if (code != 0) {
                // 错误信息
                String error = intent.getStringExtra(SdkConstants.ERROR);
                // 日志
                if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                    Log.d(LogTag.PUSH, "command is: " + cmd + " result error: " + error);
                }
            } else {
                // 日志
                if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                    Log.d(LogTag.PUSH, " command is:-->> " + cmd + "result OK");
                }
            }
            // 附加结果，比如添加成功的tag， 比如推送是否暂停等
            String extra = intent.getStringExtra(SdkConstants.ADDITION);
            if (extra != null) {
                // 日志
                if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                    Log.d(LogTag.PUSH, "result extra: -->> " + extra);
                }
            }
            if (SdkConstants.REGISTER.equals(cmd)) {
                token = intent.getStringExtra(SdkConstants.ADDITION);
                if (!TextUtils.isEmpty(token)) {
                    AixintuiConfigs.push_token = token;
                    // 日志
                    if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                        Log.d(LogTag.PUSH, " 调用绑定接口-->> " + " aixtoken-->> " + token);
                    }
                    AixintuiPushManager.getInstance().setAixintui_token(token);
                    // 绑定爱心推token
//                    AixintuiPushManager.getInstance().bindPush();
                    //弹出二维码
//                    QRUtils.showRegQr(Global.getAppContext().getResources().getString(R.string.reg_info));
                }
            }
        }

        // 通知点击事件
        else if (action.equals(SdkConstants.NOTIFICATION_CLICK_ACTION)) {

            String msg = intent.getStringExtra(SdkConstants.MESSAGE);
            // 日志
            if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                Log.e(LogTag.PUSH, " notification click received, msg is:-->> " + msg);
            }
            AixintuiPushManager.getInstance().onNofificationClick(context, msg);
        }
    }
}
