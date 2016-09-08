package com.mapbar.android.obd.rearview.modules.external;

import android.os.Bundle;

import com.mapbar.obd.CarStatusData;

/**
 * 车辆状态
 * Created by zhangyunfei on 16/9/7.
 */
public class CarStatusBeanConvert {

    public static CarStatusBean convertToCarStatusBean(CarStatusData data, CarStatusBean carStatusBean) {
        if (data == null)
            throw new NullPointerException();
        CarStatusBean bean = carStatusBean;
        if (bean == null)
            bean = new CarStatusBean();

        bean.is_support_lock = data.lock > 0;//是否支持中控锁
        bean.is_locked = data.lock == 1;//是否 锁

        bean.is_support_sunroof = data.sunroof > 0;//是否支持天窗
        bean.is_open_sunroof = data.sunroof == 2;//是否 打开

        bean.is_support_trunk = data.trunk > 0;//后备箱
        bean.is_open_trunk = data.trunk == 1;//是否 打开后备箱

        bean.is_support_lights = data.trunk > 0;//是否支持 灯
        bean.is_support_windows = data.trunk > 0;//是否支持 车窗

        return bean;
//    public boolean is_on_light_small;//小灯
//    public boolean is_on_light_near;//近光灯
//    public boolean is_on_light_far;//远光灯
//    public boolean is_on_light_fog_front;//前雾灯
//    public boolean is_on_light_fog_back;//前雾灯
//    public boolean is_on_light_turn_left;//左转灯
//    public boolean is_on_light_turn_right;//右转灯
//    public boolean is_on_light_dangerous;//危险报警双闪
//    public boolean is_on_light_switch;//总开关
//
//    public boolean is_support_left_front_lock;//是否支持 左前门锁状态
//    public boolean is_on_left_front_lock;//是否锁 左前门锁
//    public boolean is_support_right_front_lock;//是否支持 右前门锁状态
//    public boolean is_on_righ_front_lock;//是否锁 右前门锁
//    public boolean is_support_left_back_lock;//是否支持 左后门锁状态
//    public boolean is_on_left_back_lock;//是否锁 左后门锁
//    public boolean is_support_right_back_lock;//是否支持 右后门锁状态
//    public boolean is_on_right_back_lock;//是否锁 右后门锁
//
//    public boolean is_support_left_front_door;//是否支持 左前门开关状态
//    public boolean is_on_left_front_door;//是否打开 左前门
//    public boolean is_support_right_front_door;//是否支持 右前开关状态
//    public boolean is_on_righ_front_door;//是否打开 右前门
//    public boolean is_support_left_back_door;//是否支持 左后门开关状态
//    public boolean is_on_left_back_door;//是否打开 左后门
//    public boolean is_support_right_back_door;//是否支持 右后门开关状态
//    public boolean is_on_right_back_door;//是否打开 右后门
//
//    public boolean is_support_left_front_window;//是否支持 左前门车窗状态
//    public boolean is_on_left_front_window;//是否打开 左前门车窗
//    public boolean is_support_right_front_window;//是否支持 右前车窗状态
//    public boolean is_on_righ_front_window;//是否打开 右前门车窗
//    public boolean is_support_left_back_window;//是否支持 左后门车窗状态
//    public boolean is_on_left_back_window;//是否打开 左后门车窗
//    public boolean is_support_right_back_window;//是否支持 右后门车窗状态
//    public boolean is_on_right_back_window;//是否打开 右后门车窗
    }


    public static Bundle convertToBundle(CarStatusBean data) {
        return convertToBundle(data,null);
    }

    public static Bundle convertToBundle(CarStatusBean data, Bundle bundleArg) {
        if (data == null)
            throw new NullPointerException();
        Bundle bundle = bundleArg == null ? new Bundle() : new Bundle(bundleArg);
        bundle.putBoolean("is_support_lock", data.is_support_lock);//是否支持中控锁
        bundle.putBoolean("is_locked", data.is_locked);//是否 锁了 中控锁

        return bundle;
    }
}