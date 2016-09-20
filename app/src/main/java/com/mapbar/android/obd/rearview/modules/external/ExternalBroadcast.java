package com.mapbar.android.obd.rearview.modules.external;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 外部消息管理器。发送实时数据，车辆状态给外部的app使用
 * Created by zhangyunfei on 16/9/5.
 */
class ExternalBroadcast {
    private static final String TAG = "ExternalBroadcast";
    /**
     * 车辆数据,实时数据
     */
    public static final String ACTION_EXTERNAL_REALTIMEDATA = "com.mapbar.android.obd.rearview.action.exernal.CARDATA";

    /**
     * 车辆状态
     */
    public static final String ACTION_EXTERNAL_CARSTATE = "com.mapbar.android.obd.rearview.action.exernal.CARSTATE";


    /**
     * post实时数据广播
     *
     * @param context
     */
    public static void postRealTimeData(Context context, Bundle arguments) {
        if (context == null)
            throw new NullPointerException();
        Intent intent = new Intent(ACTION_EXTERNAL_REALTIMEDATA);
        intent.putExtras(arguments);
        context.sendBroadcast(intent);
//        LogUtil.d(TAG, "## 准备发送外部广播（实时数据）： " + arguments);
    }


    /**
     * post车辆状态
     *
     * @param context
     */
    public static void postCarStatus(Context context, Bundle arguments) {
        if (context == null)
            throw new NullPointerException();
        Intent intent = new Intent(ACTION_EXTERNAL_CARSTATE);
        intent.putExtras(arguments);
        context.sendBroadcast(intent);
//        LogUtil.d(TAG, "## 发送外部广播（车辆状态）： " + arguments);
    }

}


