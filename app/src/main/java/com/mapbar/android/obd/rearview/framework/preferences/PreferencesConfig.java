package com.mapbar.android.obd.rearview.framework.preferences;


import android.content.Context;
import com.mapbar.android.obd.rearview.framework.preferences.item.BooleanPreferences;
import com.mapbar.android.obd.rearview.framework.preferences.item.IntPreferences;
import com.mapbar.android.obd.rearview.framework.preferences.item.LongPreferences;
import com.mapbar.android.obd.rearview.framework.preferences.item.StringPreferences;
import com.mapbar.android.obd.rearview.lib.config.MyApplication;


/**
 *
 */
public class PreferencesConfig {


    private static final SharedPreferencesWrapper SHARED_PREFERENCES_INIT = new SharedPreferencesWrapper(MyApplication.getInstance(), "init", Context.MODE_PRIVATE);
    public static final IntPreferences MESSAGE_COUNT = new IntPreferences(SHARED_PREFERENCES_INIT, "message_count", 4);
    public static final StringPreferences UTIME = new StringPreferences(SHARED_PREFERENCES_INIT, "utime", "");
    public static final StringPreferences TITLE = new StringPreferences(SHARED_PREFERENCES_INIT, "title", "");
    public static final IntPreferences DASH_COUNT = new IntPreferences(SHARED_PREFERENCES_INIT, "dash_count", 1);
    public static final IntPreferences DASH_COUNT_ELETRONIC = new IntPreferences(SHARED_PREFERENCES_INIT, "dash_count_eletronic", 1);
    public static final StringPreferences VIN = new StringPreferences(SHARED_PREFERENCES_INIT, "vin", "");
    public static final BooleanPreferences SHOW_MILEAGE = new BooleanPreferences(SHARED_PREFERENCES_INIT, "show", false);
    public static final BooleanPreferences OTA_SPEC = new BooleanPreferences(SHARED_PREFERENCES_INIT, "ISSPEC", false);
    /**
     * 每次行程结束后保存的行程开始时间
     */
    public static final LongPreferences LAST_MILEAGE_STARTTIME = new LongPreferences(SHARED_PREFERENCES_INIT, "last_mileage_starttime", 0);
    /**
     * 每次行程结束后保存的行驶里程
     */
    public static final IntPreferences LAST_MILEAGE = new IntPreferences(SHARED_PREFERENCES_INIT, "last_mileage", 0);
    /**
     * 签到年月日
     */
    public static final StringPreferences SIGNIN_SUCCESS_DATE = new StringPreferences(SHARED_PREFERENCES_INIT, "signin_success_date", "");
    public static final BooleanPreferences IS_SIGNIN = new BooleanPreferences(SHARED_PREFERENCES_INIT, "is_signin", false);
    public static final IntPreferences TOTAL_CREDITS = new IntPreferences(SHARED_PREFERENCES_INIT, "total_credits", 0);
    public static final StringPreferences RULE_ACTIVITY_STIME = new StringPreferences(SHARED_PREFERENCES_INIT, "activity_stime", "");
    public static final StringPreferences RULE_ACTIVITY_ETIME = new StringPreferences(SHARED_PREFERENCES_INIT, "activity_etime", "");
    /**
     * 积分活动的倍数
     */
    public static final IntPreferences RULE_ACTIVITY_DOUBLE = new IntPreferences(SHARED_PREFERENCES_INIT, "activity_double", 1);
    public static final LongPreferences TRIP_START_TIME = new LongPreferences(SHARED_PREFERENCES_INIT, "trip_start_time", System.currentTimeMillis());
    public static final StringPreferences IXINTUI_TOKEN = new StringPreferences(SHARED_PREFERENCES_INIT, "ixintui_token", "");
    /**
     * 电子眼的设置true 表示打开
     */

    public static final BooleanPreferences LIMIT_SPEED = new BooleanPreferences(SHARED_PREFERENCES_INIT, "limit_speed", true);
    public static final BooleanPreferences BREAK_TRAFFIC = new BooleanPreferences(SHARED_PREFERENCES_INIT, "break_traffic", true);
    public static final BooleanPreferences DANGER_AREA = new BooleanPreferences(SHARED_PREFERENCES_INIT, "danger_area", true);
    public static final BooleanPreferences BREAK_RULES = new BooleanPreferences(SHARED_PREFERENCES_INIT, "BREAK_RULES", true);
    public static final BooleanPreferences TOTAL_ELETRONIC = new BooleanPreferences(SHARED_PREFERENCES_INIT, "TOTAL_ELETRONIC", true);
    public static final StringPreferences ELETRONIC_UPDADATA_VERSION = new StringPreferences(SHARED_PREFERENCES_INIT, "eletronicUpdadaVersion", "");
    /**
     * 是否需要去更新电子眼数据，true需要更新
     */
    public static final BooleanPreferences ISELETRONICUPDATE = new BooleanPreferences(SHARED_PREFERENCES_INIT, "iseletronicUpdate", false);
    public static final BooleanPreferences IS_SHOW_NOFILE_DIALOG = new BooleanPreferences(SHARED_PREFERENCES_INIT, "ISSHOWNOFILEDIALOG", true);
    /**
     * 判断是否是同一次行程
     */
    public static final BooleanPreferences IS_SINGLE_JOURNEY = new BooleanPreferences(SHARED_PREFERENCES_INIT, "ISSHOW_NOFILE_DIA_LOG", true);
    /**
     * 是否需要弹出GPS对话框
     */
    public static final BooleanPreferences IS_SHOW_GPS_DIALOG = new BooleanPreferences(SHARED_PREFERENCES_INIT, "ISSHOW_GPS_DIALOG", true);
    public static final BooleanPreferences IS_VIOLATION_PUSH_OPEN = new BooleanPreferences(SHARED_PREFERENCES_INIT, "IS_VIOLATION_PUSH_OPEN", true);
    /**
     * 轨迹开关
     */
    public static final BooleanPreferences TRACK_SWITCH = new BooleanPreferences(SHARED_PREFERENCES_INIT, "track_switch", true);        //public static final BooleanPreferences SETTING_CELLlOCATION_STATE = new BooleanPreferences(SHARED_PREFERENCES_INIT_CONFIG_NAME, Config.SETTING_CELLlOCATION_STATE, true);
    //public static final IntPreferences DAY_NIGHT_MODE = new IntPreferences(SHARED_PREFERENCES_INIT, OptionConfigShareUtil.NIGHT_MODE, MapController.MODE_AUTO);
    //public static final IntPreferences CENTER_POSITION_LAT = new IntPreferences(SHARED_PREFERENCES_INIT, "center_position_lat", 0);
    //public static final StringPreferences FIRST_CITY_ANALYSIS = new StringPreferences(SHARED_PREFERENCES_INIT, Config.FIRST_CITY_ANALYSIS,null);
    public static final LongPreferences TRACK_LASTMODIFY = new LongPreferences(SHARED_PREFERENCES_INIT, "track_lastModify", 0);
    //是否关闭广告
    public static final BooleanPreferences ISCLOSEADVERTISEMENT = new BooleanPreferences(SHARED_PREFERENCES_INIT, "is_close_advertisement", true);
    //关闭广告次数
    public static final LongPreferences CLOSEADVERTISEMENTNUM = new LongPreferences(SHARED_PREFERENCES_INIT, "closeadvertisementnum", 1);
    //体检完成时间
    public static final StringPreferences PHYSICAL_CHECKEND_DATE = new StringPreferences(SHARED_PREFERENCES_INIT, "physical_checkend_date", "");
    public static final StringPreferences USERCAR_BTTYPE = new StringPreferences(SHARED_PREFERENCES_INIT, "bt_type", "");
    private static final SharedPreferencesWrapper SHARED_PREFERENCES_INIT_CONFIG_NAME = new SharedPreferencesWrapper(MyApplication.getInstance(), "navi_config", Context.MODE_PRIVATE);

}
