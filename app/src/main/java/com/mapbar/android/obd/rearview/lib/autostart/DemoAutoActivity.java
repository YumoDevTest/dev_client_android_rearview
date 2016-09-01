package com.mapbar.android.obd.rearview.lib.autostart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.common.LayoutUtils;
import com.mapbar.android.obd.rearview.framework.common.TimeUtils;
import com.mapbar.android.obd.rearview.lib.autostart.contract.DelayAutoStartService;
import com.mapbar.android.obd.rearview.obd.Constants;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.SerialPortConnection;
import com.mapbar.android.obd.rearview.obd.impl.SerialPortConnectionCreator;
import com.mapbar.android.obd.rearview.obd.util.FactoryTest;
import com.mapbar.obd.SerialPortManager;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DemoAutoActivity extends Activity {
    private static final String ACTION_DAEMON_SERVICE = "com.mapbar.android.obd.rearview.action.DEAMON";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declare);

        //发送延迟启动消息
        Intent intentDelayStart = new Intent(DelayAutoStartService.ACTION_STOP_START_APP);
        sendBroadcast(intentDelayStart);

//        Intent intentService = new Intent(ACTION_DAEMON_SERVICE);
//        startService(intentService);
    }


}
