package com.mapbar.demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class External_broadcast_realtime2Activity extends Activity {
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
                showCarStatus(intent);
            } else if (intent.getAction().equals(ACTION_EXTERNAL_CARSTATE)) {
            }
        }

    };

    private void showCarStatus(Intent intent) {
        int speed = intent.getIntExtra("speed", 0);//车速，单位：km/h
        int rpm = intent.getIntExtra("rpm", 0);//转速，单位: r/min
        float voltage = intent.getFloatExtra("voltage", 0);//电池电压，单位：V。
        int engineCoolantTemperature = intent.getIntExtra("engineCoolantTemperature", 0);//水温”)，单位：℃。
        float gasConsumInLPerHour = intent.getFloatExtra("gasConsumInLPerHour", 0);//瞬时油耗，单位：L/h
        float averageGasConsum = intent.getFloatExtra("averageGasConsum", 0);//平均油耗，单位：L/100km
        long tripTime = intent.getLongExtra("tripTime", 0);//行程耗时，单位：毫秒
        int tripLength = intent.getIntExtra("tripLength", 0);//行程里程，单位：m
        float driveCost = intent.getFloatExtra("driveCost", 0);//行程花销，单位：元

        StringBuilder sb = new StringBuilder("\r\n");
        sb.append(String.format("车速，单位：km/h: %s， ", speed));
        sb.append(String.format("转速，单位: r/min: %s， ", rpm));
        sb.append(String.format("电池电压，单位：V: %s， ", voltage));
        sb.append(String.format("水温”)，单位：℃。: %s， ", engineCoolantTemperature));
        sb.append(String.format("瞬时油耗，单位：L/h: %s， ", gasConsumInLPerHour));
        sb.append(String.format("平均油耗，单位：L/100km: %s， ", averageGasConsum));
        sb.append(String.format("行程耗时，单位：分钟: %s， ", tripTime / 1000 / 60));
        sb.append(String.format("行程里程，单位：m: %s， ", tripLength));
        sb.append(String.format("行程花销，单位：元: %s， ", driveCost));

//                android.util.Log.i(TAG, "## " + sb.toString());
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
