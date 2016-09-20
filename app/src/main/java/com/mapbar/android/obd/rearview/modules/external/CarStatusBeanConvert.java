package com.mapbar.android.obd.rearview.modules.external;

import android.os.Bundle;

import com.mapbar.android.obd.rearview.util.IntegerToBooleanParser;
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

        bean.is_support_sunroof = parseBoolean(data.windowsdetail, 17);//是否支持天窗
        bean.is_open_sunroof = parseBoolean(data.windowsdetail, 16);//是否 打开

        bean.is_support_trunk = parseBoolean(data.doorsdetail, 17);//后备箱
        bean.is_open_trunk = parseBoolean(data.doorsdetail, 16);//是否 打开后备箱

        bean.is_support_lights = parseBoolean(data.lightsdetail, 31);//是否支持 灯
//        bean.is_support_windows =  parseBoolean(data.lightsdetail, 31);//是否支持 车窗
        bean.is_on_light_small = parseBoolean(data.lightsdetail, 0);//小灯
        bean.is_on_light_near = parseBoolean(data.lightsdetail, 1);//近光灯
        bean.is_on_light_far = parseBoolean(data.lightsdetail, 2);//远光灯
        bean.is_on_light_fog_front = parseBoolean(data.lightsdetail, 3);//前雾灯
        bean.is_on_light_fog_back = parseBoolean(data.lightsdetail, 4);//后雾灯
        bean.is_on_light_turn_left = parseBoolean(data.lightsdetail, 5);//左转灯
        bean.is_on_light_turn_right = parseBoolean(data.lightsdetail, 6);//右转灯
        bean.is_on_light_dangerous = parseBoolean(data.lightsdetail, 7);//危险报警双闪
        bean.is_on_light_switch = parseBoolean(data.lightsdetail, 30);//总开关
        //锁
        bean.is_support_left_front_lock = parseBoolean(data.doorsdetail, 15);//是否支持 左前门锁状态
        bean.is_on_left_front_lock = parseBoolean(data.doorsdetail, 14);//是否锁 左前门锁
        bean.is_support_right_front_lock = parseBoolean(data.doorsdetail, 11);//是否支持 右前门锁状态
        bean.is_on_righ_front_lock = parseBoolean(data.doorsdetail, 10);//是否锁 右前门锁
        bean.is_support_left_back_lock = parseBoolean(data.doorsdetail, 7);//是否支持 左后门锁状态
        bean.is_on_left_back_lock = parseBoolean(data.doorsdetail, 6);//是否锁 左后门锁
        bean.is_support_right_back_lock = parseBoolean(data.doorsdetail, 3);//是否支持 右后门锁状态
        bean.is_on_right_back_lock = parseBoolean(data.doorsdetail, 2);//是否锁 右后门锁
        //门
        bean.is_support_left_front_door = parseBoolean(data.doorsdetail, 13);//是否支持 左前门开关状态
        bean.is_on_left_front_door = parseBoolean(data.doorsdetail, 12);//是否打开 左前门
        bean.is_support_right_front_door = parseBoolean(data.doorsdetail, 9);//是否支持 右前开关状态
        bean.is_on_righ_front_door = parseBoolean(data.doorsdetail, 8);//是否打开 右前门
        bean.is_support_left_back_door = parseBoolean(data.doorsdetail, 4);//是否支持 左后门开关状态
        bean.is_on_left_back_door = parseBoolean(data.doorsdetail, 5);//是否打开 左后门
        bean.is_support_right_back_door = parseBoolean(data.doorsdetail, 1);//是否支持 右后门开关状态
        bean.is_on_right_back_door = parseBoolean(data.doorsdetail, 0);//是否打开 右后门

        bean.is_support_left_front_window = parseBoolean(data.windowsdetail, 13);//是否支持 左前门车窗状态
        bean.is_on_left_front_window = parseBoolean(data.windowsdetail, 12);//是否打开 左前门车窗
        bean.is_support_right_front_window = parseBoolean(data.windowsdetail, 9);//是否支持 右前车窗状态
        bean.is_on_righ_front_window = parseBoolean(data.windowsdetail, 8);//是否打开 右前门车窗
        bean.is_support_left_back_window = parseBoolean(data.windowsdetail, 5);//是否支持 左后门车窗状态
        bean.is_on_left_back_window = parseBoolean(data.windowsdetail, 4);//是否打开 左后门车窗
        bean.is_support_right_back_window = parseBoolean(data.windowsdetail, 1);//是否支持 右后门车窗状态
        bean.is_on_right_back_window = parseBoolean(data.windowsdetail, 0);//是否打开 右后门车窗
        return bean;
    }

    public static boolean parseBoolean(int value, int index) {
        return IntegerToBooleanParser.parseBoolean(value, index);
    }


    public static Bundle convertToBundle(CarStatusBean data) {
        return convertToBundle(data, null);
    }

    public static Bundle convertToBundle(CarStatusBean data, Bundle bundleArg) {
        if (data == null)
            throw new NullPointerException();
        Bundle bundle = bundleArg == null ? new Bundle() : new Bundle(bundleArg);
        bundle.putBoolean("is_support_lock", data.is_support_lock);//是否支持中控锁
        bundle.putBoolean("is_locked", data.is_locked);//是否 锁了 中控锁

        bundle.putBoolean("is_support_sunroof", data.is_support_sunroof);
        bundle.putBoolean("is_open_sunroof", data.is_open_sunroof);

        bundle.putBoolean("is_support_trunk", data.is_support_trunk);
        bundle.putBoolean("is_open_trunk", data.is_open_trunk);

        bundle.putBoolean("is_support_lights", data.is_support_lights);
//        bundle.putBoolean("is_support_windows", data.is_support_windows);

        bundle.putBoolean("is_on_light_small", data.is_on_light_small);
        bundle.putBoolean("is_on_light_near", data.is_on_light_near);
        bundle.putBoolean("is_on_light_far", data.is_on_light_far);
        bundle.putBoolean("is_on_light_fog_front", data.is_on_light_fog_front);
        bundle.putBoolean("is_on_light_fog_back", data.is_on_light_fog_back);
        bundle.putBoolean("is_on_light_turn_left", data.is_on_light_turn_left);
        bundle.putBoolean("is_on_light_turn_right", data.is_on_light_turn_right);
        bundle.putBoolean("is_on_light_dangerous", data.is_on_light_dangerous);
        bundle.putBoolean("is_on_light_switch", data.is_on_light_switch);

//        bundle.putBoolean("is_support_left_front_lock", data.is_support_left_front_lock);
        bundle.putBoolean("is_support_left_front_lock", data.is_support_left_front_lock);
        bundle.putBoolean("is_on_left_front_lock", data.is_on_left_front_lock);
        bundle.putBoolean("is_support_right_front_lock", data.is_support_right_front_lock);
        bundle.putBoolean("is_on_righ_front_lock", data.is_on_righ_front_lock);
        bundle.putBoolean("is_support_left_back_lock", data.is_support_left_back_lock);
        bundle.putBoolean("is_on_left_back_lock", data.is_on_left_back_lock);
        bundle.putBoolean("is_support_right_back_lock", data.is_support_right_back_lock);
        bundle.putBoolean("is_on_right_back_lock", data.is_on_right_back_lock);//是否锁 右后门锁

        bundle.putBoolean("is_support_left_front_door", data.is_support_left_front_door);
        bundle.putBoolean("is_on_left_front_door", data.is_on_left_front_door);
        bundle.putBoolean("is_support_right_front_door", data.is_support_right_front_door);
        bundle.putBoolean("is_on_righ_front_door", data.is_on_righ_front_door);
        bundle.putBoolean("is_support_left_back_door", data.is_support_left_back_door);
        bundle.putBoolean("is_on_left_back_door", data.is_on_left_back_door);
        bundle.putBoolean("is_support_right_back_door", data.is_support_right_back_door);
        bundle.putBoolean("is_on_right_back_door", data.is_on_right_back_door);

        bundle.putBoolean("is_support_left_front_window", data.is_support_left_front_window);
        bundle.putBoolean("is_on_left_front_window", data.is_on_left_front_window);
        bundle.putBoolean("is_support_right_front_window", data.is_support_right_front_window);
        bundle.putBoolean("is_on_righ_front_window", data.is_on_righ_front_window);
        bundle.putBoolean("is_support_left_back_window", data.is_support_left_back_window);
        bundle.putBoolean("is_on_left_back_window", data.is_on_left_back_window);
        bundle.putBoolean("is_support_right_back_window", data.is_support_right_back_window);
        bundle.putBoolean("is_on_right_back_window", data.is_on_right_back_window);
        return bundle;
    }
}