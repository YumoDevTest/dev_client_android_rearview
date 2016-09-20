package com.mapbar.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void onClickView1(View view) {
        if (view.getId() == R.id.btn1_ota) {
            startActivity(new Intent(self(), DemoOtaSerialActivity.class));
        } else if (view.getId() == R.id.btn_external_realtime) {
            startActivity(new Intent(self(), External_broadcast_realtime2Activity.class));
        } else if (view.getId() == R.id.btn_external_status) {
            startActivity(new Intent(self(), External_broadcast_car_statusActivity.class));
        } else if (view.getId() == R.id.btn_voice) {
            startActivity(new Intent(self(), VoiceControlDemoActivity.class));
        }
    }

    private Context self() {
        return this;
    }
}
