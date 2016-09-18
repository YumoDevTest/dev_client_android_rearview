//package com.mapbar.android.obd.rearview.lib.daemon;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//
//import com.mapbar.android.obd.rearview.R;
//
//public class DemoAutoActivity extends Activity {
//    private static final String ACTION_DAEMON_SERVICE = "com.mapbar.android.obd.rearview.action.DEAMON";
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_declare);
//
////        //发送延迟启动消息
////        Intent intentDelayStart = new Intent(DelayAutoStartService.ACTION_STOP_DELAY_RUN);
////        sendBroadcast(intentDelayStart);
//
//        Intent intentService = new Intent(ACTION_DAEMON_SERVICE);
//        startService(intentService);
//    }
//
//
//}
