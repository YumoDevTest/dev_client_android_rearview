package com.mapbar.android.obd.rearview.modules.external;

/**
 * 车辆状态
 * Created by zhangyunfei on 16/9/7.
 */
public class CarStatusBean {

    public boolean is_support_lock;//是否支持中控锁
    public boolean is_locked;//是否 锁

    public boolean is_support_sunroof;//是否支持天窗
    public boolean is_open_sunroof;//是否 打开天窗

    public boolean is_support_trunk;//后备箱
    public boolean is_open_trunk;//是否 打开后备箱

    public boolean is_support_lights;//是否支持 灯
//    public boolean is_support_windows;//是否支持 车窗

    public boolean is_on_light_small;//小灯
    public boolean is_on_light_near;//近光灯
    public boolean is_on_light_far;//远光灯
    public boolean is_on_light_fog_front;//前雾灯
    public boolean is_on_light_fog_back;//前雾灯
    public boolean is_on_light_turn_left;//左转灯
    public boolean is_on_light_turn_right;//右转灯
    public boolean is_on_light_dangerous;//危险报警双闪
    public boolean is_on_light_switch;//总开关

    public boolean is_support_left_front_lock;//是否支持 左前门锁状态
    public boolean is_on_left_front_lock;//是否锁 左前门锁
    public boolean is_support_right_front_lock;//是否支持 右前门锁状态
    public boolean is_on_righ_front_lock;//是否锁 右前门锁
    public boolean is_support_left_back_lock;//是否支持 左后门锁状态
    public boolean is_on_left_back_lock;//是否锁 左后门锁
    public boolean is_support_right_back_lock;//是否支持 右后门锁状态
    public boolean is_on_right_back_lock;//是否锁 右后门锁

    public boolean is_support_left_front_door;//是否支持 左前门开关状态
    public boolean is_on_left_front_door;//是否打开 左前门
    public boolean is_support_right_front_door;//是否支持 右前开关状态
    public boolean is_on_righ_front_door;//是否打开 右前门
    public boolean is_support_left_back_door;//是否支持 左后门开关状态
    public boolean is_on_left_back_door;//是否打开 左后门
    public boolean is_support_right_back_door;//是否支持 右后门开关状态
    public boolean is_on_right_back_door;//是否打开 右后门

    public boolean is_support_left_front_window;//是否支持 左前门车窗状态
    public boolean is_on_left_front_window;//是否打开 左前门车窗
    public boolean is_support_right_front_window;//是否支持 右前车窗状态
    public boolean is_on_righ_front_window;//是否打开 右前门车窗
    public boolean is_support_left_back_window;//是否支持 左后门车窗状态
    public boolean is_on_left_back_window;//是否打开 左后门车窗
    public boolean is_support_right_back_window;//是否支持 右后门车窗状态
    public boolean is_on_right_back_window;//是否打开 右后门车窗

    public void clear() {
        is_support_lock = false;
        is_locked = false;

        is_support_sunroof = false;
        is_open_sunroof = false;

        is_support_trunk = false;
        is_open_trunk = false;

        is_support_lights = false;
//        is_support_windows = false;

        is_on_light_small = false;
        is_on_light_near = false;
        is_on_light_far = false;
        is_on_light_fog_front = false;
        is_on_light_fog_back = false;
        is_on_light_turn_left = false;
        is_on_light_turn_right = false;
        is_on_light_dangerous = false;
        is_on_light_switch = false;

//        is_open_sunroof = false;
    }

    @Override
    public String toString() {
        return toString(this);
    }

    private static String toString(CarStatusBean bean) {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("中控锁是否支持: %s， ", bean.is_support_lock));
        sb.append(String.format("中控锁是否锁: %s， ", bean.is_locked));

        sb.append(String.format("是否支持天窗: %s， ", bean.is_support_sunroof));
        sb.append(String.format("是否打开天窗: %s， ", bean.is_open_sunroof));

        sb.append(String.format("是否支持后备箱: %s， ", bean.is_support_trunk));
        sb.append(String.format("是否打开后备箱: %s， ", bean.is_open_trunk));

        sb.append(String.format("是否支持 灯: %s， ", bean.is_support_lights));

        sb.append(String.format("小灯: %s， ", bean.is_on_light_small));
        sb.append(String.format("近光灯: %s， ", bean.is_on_light_near));
        sb.append(String.format("远光灯: %s， ", bean.is_on_light_far));
        sb.append(String.format("前雾灯: %s， ", bean.is_on_light_fog_front));
        sb.append(String.format("后雾灯: %s， ", bean.is_on_light_fog_back));
        sb.append(String.format("左转灯: %s， ", bean.is_on_light_turn_left));
        sb.append(String.format("右转灯: %s， ", bean.is_on_light_turn_right));
        sb.append(String.format("危险报警双闪: %s， ", bean.is_on_light_dangerous));
        sb.append(String.format("总开关: %s， ", bean.is_on_light_switch));

//        sb.append(String.format(": %s， ",bean.is_support_left_front_lock));
        sb.append(String.format("是否支持 左前门锁状态: %s， ", bean.is_support_left_front_lock));
        sb.append(String.format("是否锁 左前门锁: %s， ", bean.is_on_left_front_lock));
        sb.append(String.format("是否支持 右前门锁状态: %s， ", bean.is_support_right_front_lock));
        sb.append(String.format("是否锁 右前门锁: %s， ", bean.is_on_righ_front_lock));
        sb.append(String.format("是否支持 左后门锁状态: %s， ", bean.is_support_left_back_lock));
        sb.append(String.format("是否锁 左后门锁: %s， ", bean.is_on_left_back_lock));
        sb.append(String.format("是否支持 右后门锁状态: %s， ", bean.is_support_right_back_lock));
        sb.append(String.format("是否锁 右后门锁: %s， ", bean.is_on_right_back_lock));

        sb.append(String.format("是否支持 左前门开关状态: %s， ", bean.is_support_left_front_door));
        sb.append(String.format("是否打开 左前门: %s， ", bean.is_on_left_front_door));
        sb.append(String.format("是否支持 右前开关状态: %s， ", bean.is_support_right_front_door));
        sb.append(String.format("是否打开 右前门: %s， ", bean.is_on_righ_front_door));
        sb.append(String.format("是否支持 左后门开关状态: %s， ", bean.is_support_left_back_door));
        sb.append(String.format("是否打开 左后门: %s， ", bean.is_on_left_back_door));
        sb.append(String.format("是否支持 右后门开关状态: %s， ", bean.is_support_right_back_door));
        sb.append(String.format("是否打开 右后门: %s， ", bean.is_on_right_back_door));

        sb.append(String.format("是否支持 左前门车窗状态: %s， ", bean.is_support_left_front_window));
        sb.append(String.format("是否打开 左前门车窗: %s， ", bean.is_on_left_front_window));
        sb.append(String.format("是否支持 右前车窗状态: %s， ", bean.is_support_right_front_window));
        sb.append(String.format("是否打开 右前门车窗: %s， ", bean.is_on_righ_front_window));
        sb.append(String.format("是否支持 左后门车窗状态: %s， ", bean.is_support_left_back_window));
        sb.append(String.format("是否打开 左后门车窗: %s， ", bean.is_on_left_back_window));
        sb.append(String.format("是否支持 右后门车窗状态: %s， ", bean.is_support_right_back_window));
        sb.append(String.format("是否打开 右后门车窗: %s， ", bean.is_on_right_back_window));

        return sb.toString();
    }
}
