//package com.mapbar.android.obd.rearview.modules.external;
//
//import android.os.Bundle;
//
//import com.mapbar.obd.CarStatusData;
//
///**
// * 转换
// * Created by zhangyunfei on 16/9/7.
// */
//public class CarsStatusConverter {
//    private static CarStatusBean TMP = new CarStatusBean();
//
//    private CarsStatusConverter() {
//    }
//
//
//    /**
//     * 从车辆状态数据 转换成bundle格式实时数据
//     *
//     * @param carStatusData
//     * @return
//     */
//    public static Bundle convertToBundle(CarStatusData carStatusData) {
//        return convertToBundle(carStatusData, null);
//    }
//
//    /**
//     * 从车辆状态数据 转换成bundle格式实时数据
//     *
//     * @param data
//     * @param bundleArg
//     * @return
//     */
//    public static Bundle convertToBundle(CarStatusData data, Bundle bundleArg) {
//        if (data == null)
//            throw new NullPointerException();
//        TMP.clear();
//        TMP = CarStatusBeanConvert.convertToCarStatusBean(data, TMP);
//
//        Bundle bundle = bundleArg == null ? new Bundle() : new Bundle(bundleArg);
//        bundle.putBoolean("is_support_lock", TMP.is_support_lock);//是否支持中控锁
//        bundle.putBoolean("is_locked", TMP.is_locked);//是否 锁
//
//        bundle.putBoolean("is_support_sunroof", TMP.is_support_sunroof);//是否支持天窗
//        bundle.putBoolean("is_open_sunroof", TMP.is_open_sunroof);//是否 打开
//
//        bundle.putBoolean("is_support_trunk", TMP.is_support_trunk);//后备箱
//        bundle.putBoolean("is_open_trunk", TMP.is_open_trunk);//是否 打开后备箱
//
//        bundle.putBoolean("is_support_lights", TMP.is_support_lights);//是否支持 灯
//        bundle.putBoolean("is_support_windows", TMP.is_support_windows);//是否支持 车窗
//
//
//        return bundle;
//    }
//}
