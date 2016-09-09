package com.mapbar.demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class External_broadcast_car_statusActivity extends Activity {
    public static final String TAG = "External_broadcast_car_statusActivity";

    /**
     * Action,车辆数据,实时数据
     */
    public static final String ACTION_EXTERNAL_REALTIMEDATA = "com.mapbar.android.obd.rearview.action.exernal.CARDATA";

    /**
     * Action,
     * 车辆状态
     */
    public static final String ACTION_EXTERNAL_CARSTATE = "com.mapbar.android.obd.rearview.action.exernal.CARSTATE";
    private final SimpleDateFormat MySimpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public static final int MSG_APPEND_TEXT = 1;
    private TextView textview1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.externaldata_demo_activity);

        textview1 = (TextView) findViewById(R.id.textview1);

        IntentFilter intentFilter = new IntentFilter(ACTION_EXTERNAL_REALTIMEDATA);
        intentFilter.addAction(ACTION_EXTERNAL_CARSTATE);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void appendLineText(String str) {
        String msg = String.format(Locale.getDefault(), "[%s] %s", MySimpleDateFormat.format(new Date()), str);
        mHandler.obtainMessage(MSG_APPEND_TEXT, msg).sendToTarget();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_EXTERNAL_REALTIMEDATA)) {
            } else if (intent.getAction().equals(ACTION_EXTERNAL_CARSTATE)) {
                showCarRealTimeData(intent);
            }
        }

    };

    private void showCarRealTimeData(Intent intent) {
        boolean is_support_lock = intent.getBooleanExtra("is_support_lock", false);//是否支持中控锁
        boolean is_locked = intent.getBooleanExtra("is_locked", false);//是否锁了中控锁

        boolean is_support_sunroof = intent.getBooleanExtra("is_support_sunroof", false);//
        boolean is_open_sunroof = intent.getBooleanExtra("is_open_sunroof", false);//

        boolean is_support_trunk = intent.getBooleanExtra("is_support_trunk", false);//
        boolean is_open_trunk = intent.getBooleanExtra("is_open_trunk", false);//
        boolean is_support_lights = intent.getBooleanExtra("is_support_lights", false);//

        boolean is_on_light_small = intent.getBooleanExtra("is_on_light_small", false);//
        boolean is_on_light_near = intent.getBooleanExtra("is_on_light_near", false);//
        boolean is_on_light_far = intent.getBooleanExtra("is_on_light_far", false);//
        boolean is_on_light_fog_front = intent.getBooleanExtra("is_on_light_fog_front", false);//
        boolean is_on_light_fog_back = intent.getBooleanExtra("is_on_light_fog_back", false);//
        boolean is_on_light_turn_left = intent.getBooleanExtra("is_on_light_turn_left", false);//
        boolean is_on_light_turn_right = intent.getBooleanExtra("is_on_light_turn_right", false);//
        boolean is_on_light_dangerous = intent.getBooleanExtra("is_on_light_dangerous", false);//
        boolean is_on_light_switch = intent.getBooleanExtra("is_on_light_switch", false);//

        boolean is_support_left_front_lock = intent.getBooleanExtra("is_support_left_front_lock", false);//
        boolean is_on_left_front_lock = intent.getBooleanExtra("is_on_left_front_lock", false);//
        boolean is_support_right_front_lock = intent.getBooleanExtra("is_support_right_front_lock", false);//
        boolean is_on_righ_front_lock = intent.getBooleanExtra("is_on_righ_front_lock", false);//
        boolean is_support_left_back_lock = intent.getBooleanExtra("is_support_left_back_lock", false);//
        boolean is_on_left_back_lock = intent.getBooleanExtra("is_on_left_back_lock", false);//
        boolean is_support_right_back_lock = intent.getBooleanExtra("is_support_right_back_lock", false);//
        boolean is_on_right_back_lock = intent.getBooleanExtra("is_on_right_back_lock", false);//

        boolean is_support_left_front_door = intent.getBooleanExtra("is_support_left_front_door", false);//
        boolean is_on_left_front_door = intent.getBooleanExtra("is_on_left_front_door", false);//
        boolean is_support_right_front_door = intent.getBooleanExtra("is_support_right_front_door", false);//
        boolean is_on_righ_front_door = intent.getBooleanExtra("is_on_righ_front_door", false);//
        boolean is_support_left_back_door = intent.getBooleanExtra("is_support_left_back_door", false);//
        boolean is_on_left_back_door = intent.getBooleanExtra("is_on_left_back_door", false);//
        boolean is_support_right_back_door = intent.getBooleanExtra("is_support_right_back_door", false);//
        boolean is_on_right_back_door = intent.getBooleanExtra("is_on_right_back_door", false);//

        boolean is_support_left_front_window = intent.getBooleanExtra("is_support_left_front_window", false);//
        boolean is_on_left_front_window = intent.getBooleanExtra("is_on_left_front_window", false);//
        boolean is_support_right_front_window = intent.getBooleanExtra("is_support_right_front_window", false);//
        boolean is_on_righ_front_window = intent.getBooleanExtra("is_on_righ_front_window", false);//
        boolean is_support_left_back_window = intent.getBooleanExtra("is_support_left_back_window", false);//
        boolean is_on_left_back_window = intent.getBooleanExtra("is_on_left_back_window", false);//
        boolean is_support_right_back_window = intent.getBooleanExtra("is_support_right_back_window", false);//
        boolean is_on_right_back_window = intent.getBooleanExtra("is_on_right_back_window", false);//

        StringBuilder sb = new StringBuilder("\r\n");
        sb.append(String.format("中控锁是否支持: %s， ", is_support_lock));
        sb.append(String.format("中控锁是否锁: %s， ", is_locked));

        sb.append(String.format("是否支持天窗: %s， ", is_support_sunroof));
        sb.append(String.format("是否打开天窗: %s， ", is_open_sunroof));

        sb.append(String.format("是否支持后备箱: %s， ", is_support_trunk));
        sb.append(String.format("是否打开后备箱: %s， ", is_open_trunk));

        sb.append(String.format("是否支持 灯: %s， ", is_support_lights));

        sb.append(String.format("小灯: %s， ", is_on_light_small));
        sb.append(String.format("近光灯: %s， ", is_on_light_near));
        sb.append(String.format("远光灯: %s， ", is_on_light_far));
        sb.append(String.format("前雾灯: %s， ", is_on_light_fog_front));
        sb.append(String.format("后雾灯: %s， ", is_on_light_fog_back));
        sb.append(String.format("左转灯: %s， ", is_on_light_turn_left));
        sb.append(String.format("右转灯: %s， ", is_on_light_turn_right));
        sb.append(String.format("危险报警双闪: %s， ", is_on_light_dangerous));
        sb.append(String.format("总开关: %s， ", is_on_light_switch));

//        sb.append(String.format(": %s， ", is_support_left_front_lock));
        sb.append(String.format("是否支持 左前门锁状态: %s， ", is_support_left_front_lock));
        sb.append(String.format("是否锁 左前门锁: %s， ", is_on_left_front_lock));
        sb.append(String.format("是否支持 右前门锁状态: %s， ", is_support_right_front_lock));
        sb.append(String.format("是否锁 右前门锁: %s， ", is_on_righ_front_lock));
        sb.append(String.format("是否支持 左后门锁状态: %s， ", is_support_left_back_lock));
        sb.append(String.format("是否锁 左后门锁: %s， ", is_on_left_back_lock));
        sb.append(String.format("是否支持 右后门锁状态: %s， ", is_support_right_back_lock));
        sb.append(String.format("是否锁 右后门锁: %s， ", is_on_right_back_lock));

        sb.append(String.format("是否支持 左前门开关状态: %s， ", is_support_left_front_door));
        sb.append(String.format("是否打开 左前门: %s， ", is_on_left_front_door));
        sb.append(String.format("是否支持 右前开关状态: %s， ", is_support_right_front_door));
        sb.append(String.format("是否打开 右前门: %s， ", is_on_righ_front_door));
        sb.append(String.format("是否支持 左后门开关状态: %s， ", is_support_left_back_door));
        sb.append(String.format("是否打开 左后门: %s， ", is_on_left_back_door));
        sb.append(String.format("是否支持 右后门开关状态: %s， ", is_support_right_back_door));
        sb.append(String.format("是否打开 右后门: %s， ", is_on_right_back_door));

        sb.append(String.format("是否支持 左前门车窗状态: %s， ", is_support_left_front_window));
        sb.append(String.format("是否打开 左前门车窗: %s， ", is_on_left_front_window));
        sb.append(String.format("是否支持 右前车窗状态: %s， ", is_support_right_front_window));
        sb.append(String.format("是否打开 右前门车窗: %s， ", is_on_righ_front_window));
        sb.append(String.format("是否支持 左后门车窗状态: %s， ", is_support_left_back_window));
        sb.append(String.format("是否打开 左后门车窗: %s， ", is_on_left_back_window));
        sb.append(String.format("是否支持 右后门车窗状态: %s， ", is_support_right_back_window));
        sb.append(String.format("是否打开 右后门车窗: %s， ", is_on_right_back_window));

        appendLineText(sb.toString());
    }


    private Handler mHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_APPEND_TEXT) {
                if (msg.obj != null) {
                    String str = msg.obj.toString();
                    if (textview1.getText().length() > 1000) {
                        textview1.setText("");
                    }
                    textview1.append(str + "\r\n");
                }
            }

        }
    };
}
