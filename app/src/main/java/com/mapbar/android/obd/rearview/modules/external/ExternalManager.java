package com.mapbar.android.obd.rearview.modules.external;

import android.content.Context;

import com.mapbar.obd.RealTimeData;

/**
 * 外部消息管理器。发送实时数据，车辆状态给外部的app使用
 * Created by zhangyunfei on 16/9/7.
 */
public class ExternalManager {

    /**
     * post实时数据广播
     *
     * @param context
     */
    public static void postRealTimeData(Context context, RealTimeData realTimeData) {
        if (context == null)
            throw new NullPointerException();
        if (realTimeData == null)
            throw new NullPointerException();
        ExternalBroadcast.postRealTimeData(context, RealTimeDataConverter.convertFrom(realTimeData));
    }
}
