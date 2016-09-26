package com.mapbar.demo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mapbar.demo.serial.ChooseSerialPortActivity;
import com.mapbar.demo.serial.OtaChooseBinFileActivity;
import com.mapbar.demo.serial.Serialport010CActivity;
import com.mapbar.demo.serial.SerialportToolsActivity;

public class Main2Activity extends Activity {

    private static final int REQUEST_CODE_OTA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void onClickView1(View view) {
        if (view.getId() == R.id.btn_serialport) {
            //串口工具
            showChooseSerialportAndGoNext(SerialportToolsActivity.class);
        } else if (view.getId() == R.id.btn1_ota) {
            //OTA 刷固件
            showChooseSerialportAndGoNext(OtaChooseBinFileActivity.class);
        } else if (view.getId() == R.id.btn_external_realtime) {
            //车辆实时数据广播接收
            startActivity(new Intent(self(), External_broadcast_realtime2Activity.class));
        } else if (view.getId() == R.id.btn_external_status) {
            //车辆状态广播接收
            startActivity(new Intent(self(), External_broadcast_car_statusActivity.class));
        } else if (view.getId() == R.id.btn_voice) {
            //声控
            startActivity(new Intent(self(), VoiceControlDemoActivity.class));
        } else if (view.getId() == R.id.btn_010c) {
            //演示连续发串口指令
            showChooseSerialportAndGoNext(Serialport010CActivity.class);
        }
    }

    /**
     * 展示 选择串口对话框。如果用户在该对话框里点击了某个串口，则跳转到 nextActivityClass
     * 目标activity会收到 intent中参数 [SERIAL_PORT] string类型
     * @param nextActivityClass 目标activity
     */
    private void showChooseSerialportAndGoNext(Class nextActivityClass) {
        Intent targetIntent = new Intent(self(), nextActivityClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(self(), 0, targetIntent, 0);

        Intent intent = new Intent(self(), ChooseSerialPortActivity.class);
        intent.putExtra("TARGET", pendingIntent);
        startActivity(intent);
    }

    private Context self() {
        return this;
    }
}
