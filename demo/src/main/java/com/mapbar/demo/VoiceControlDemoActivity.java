package com.mapbar.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class VoiceControlDemoActivity extends Activity {
    public static final String VOICE_ACTION = "mapbar.obd.intent.action.VOICE_CONTROL";
//    private int[] commands = {201001, 201000, 202000, 202001, 207001, 207000};
    private Timer timer;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_control_demo_activity);
        timer = new Timer();
    }
    public void startExamination(View view){
        Intent intent = new Intent(VOICE_ACTION);
        switch (view.getId()) {
            case R.id.button1:
                intent.putExtra("command", 103000);
                break;
            case R.id.button3:
                intent.putExtra("command", 201001);
                break;
            case R.id.button4:
                intent.putExtra("command", 201000);
                break;
            case R.id.button5:
                intent.putExtra("command", 202001);
                break;
            case R.id.button6:
                intent.putExtra("command", 202000);
                break;
            case R.id.button7:
                intent.putExtra("command", 207001);
                break;
            case R.id.button8:
                intent.putExtra("command", 207000);
                break;
            case R.id.button9:
                intent.putExtra("command", 203001);
                break;
            case R.id.button10:
                intent.putExtra("command", 203000);
                break;

        }
        if (android.os.Build.VERSION.SDK_INT >= 12) {
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);//3.1以后的版本需要设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        }
        sendBroadcast(intent);
    }

    public void nowdata(View view) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(VOICE_ACTION);
                intent.putExtra("command", 102000 + count % 9);
                if (android.os.Build.VERSION.SDK_INT >= 12) {
                    intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);//3.1以后的版本需要设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                }
                sendBroadcast(intent);
                count++;
            }
        }, 0, 2000);

    }
}
