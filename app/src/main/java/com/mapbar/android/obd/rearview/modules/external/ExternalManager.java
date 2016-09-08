package com.mapbar.android.obd.rearview.modules.external;

import android.content.Context;

import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.CarStatusData;
import com.mapbar.obd.RealTimeData;

/**
 * 外部消息管理器。发送实时数据，车辆状态给外部的app使用
 * Created by zhangyunfei on 16/9/7.
 */
public class ExternalManager {
    private static final String TAG = "ExternalManager";
    private static CarStatusBean carStatusBean = new CarStatusBean();

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


    /**
     * post车辆状态
     *
     * @param context
     * @param carStatusData
     */
    public static void postCarStatus(Context context, CarStatusData carStatusData) {
        if (context == null)
            throw new NullPointerException();
        if (carStatusData == null)
            throw new NullPointerException();
        carStatusBean.clear();
        postCarStatus(context, CarStatusBeanConvert.convertToCarStatusBean(carStatusData, carStatusBean));
    }


    /**
     * post车辆状态
     *
     * @param context
     * @param carStatusBean
     */
    public static void postCarStatus(Context context, CarStatusBean carStatusBean) {
        if (context == null)
            throw new NullPointerException();
        if (carStatusBean == null)
            throw new NullPointerException();
        ExternalBroadcast.postCarStatus(context, CarStatusBeanConvert.convertToBundle(carStatusBean));
    }
}
