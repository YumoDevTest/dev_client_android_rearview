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

        StringBuilder sb = new StringBuilder("\r\n");
        sb.append(String.format("是否支持中控锁: %s， ", is_support_lock));
        sb.append(String.format("是否锁了中控锁: %s， ", is_locked));
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
