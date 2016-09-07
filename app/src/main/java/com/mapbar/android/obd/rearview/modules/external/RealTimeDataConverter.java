package com.mapbar.android.obd.rearview.modules.external;

import android.os.Bundle;

import com.mapbar.obd.RealTimeData;

/**
 * 转换
 * Created by zhangyunfei on 16/9/7.
 */
public class RealTimeDataConverter {
    private RealTimeDataConverter() {
    }


    /**
     * 从实时数据 转换成bundle格式实时数据
     *
     * @param data
     * @return
     */
    public static Bundle convertFrom(RealTimeData data) {
        return convertFrom(data, null);
    }

    /**
     * 从实时数据 转换成bundle格式实时数据
     *
     * @param data
     * @param bundleArg
     * @return
     */
    public static Bundle convertFrom(RealTimeData data, Bundle bundleArg) {
        if (data == null)
            throw new NullPointerException();
        Bundle bundle = bundleArg == null ? new Bundle() : new Bundle(bundleArg);
        bundle.putInt("speed", data.speed);//车速，单位：km/h
        bundle.putInt("rpm", data.rpm);//转速，单位: r/min
        bundle.putFloat("voltage", data.voltage);//电池电压，单位：V。
        bundle.putInt("engineCoolantTemperature", data.engineCoolantTemperature);//水温”)，单位：℃。
        bundle.putFloat("gasConsumInLPerHour", data.gasConsumInLPerHour);//瞬时油耗，单位：L/h
        bundle.putFloat("averageGasConsum", data.averageGasConsum);//平均油耗，单位：L/100km
        bundle.putLong("tripTime", data.tripTime);//行程耗时，单位：毫秒
        bundle.putInt("tripLength", data.tripLength);//行程里程，单位：m
        bundle.putFloat("driveCost", data.driveCost);//行程花销，单位：元
        return bundle;
    }
}



/*
2016-09-07 客户
我们要  车速 转速 电压 水温 瞬时油耗 平均油耗 本次时间 本次行程 本次花费等数据  如果车辆支持控制也要把车门，天窗，后备
 */
