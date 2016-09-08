package com.mapbar.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onButtonClick(View view) {
        if (R.id.btn_external_broadcast_realtime == view.getId()) {
            startActivity(new Intent(getActivity(), External_broadcast_realtime2Activity.class));
        } else if (R.id.btn_external_broadcast_carstatus == view.getId()) {
            startActivity(new Intent(getActivity(), External_broadcast_car_statusActivity.class));
        }
    }

    public Context getActivity() {
        return this;
    }
}
