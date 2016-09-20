package com.mapbar.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;

public class VoiceControlDemoActivity extends Activity {
    public static final String VOICE_ACTION = "mapbar.obd.intent.action.VOICE_CONTROL";
//    private int[] commands = {201001, 201000, 202000, 202001, 207001, 207000};
    private Timer timer;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = new Timer();
    }
    public void startExamination(View view){
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(VOICE_ACTION);
//                intent.putExtra("command", commands[count%6]);
//                if (android.os.Build.VERSION.SDK_INT >= 12) {
//                    intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);//3.1以后的版本需要设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES
//                }
//                sendBroadcast(intent);
//                count++;
//            }
//        },0,2000);
        Intent intent = new Intent(VOICE_ACTION);
        intent.putExtra("command", 103000);
        if (android.os.Build.VERSION.SDK_INT >= 12) {
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);//3.1以后的版本需要设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        }
        sendBroadcast(intent);
        System.exit(0);
//        Toast.makeText(getApplicationContext(), "发送广播成功", Toast.LENGTH_SHORT).show();
    }
}
