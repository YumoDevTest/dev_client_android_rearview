package com.mapbar.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.serial.ota.FirmwareUpdateCallback;
import com.mapbar.obd.serial.ota.FirmwareUpdateManager;

import java.io.File;

/**
 * 演示刷固件
 * 可自主刷固件： 步骤：1，拷贝固件文件到sd卡，2.修改本类的binFile路径，3.执行
 * Created by zhangyunfei on 16/8/12.
 */
public class DemoOtaSerialActivity extends Activity {
    private static final String TAG = DemoOtaSerialActivity.class.getSimpleName();
    String binFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_ota_serial_activity);

        ObdContext.getInstance();

        String path = Environment.getExternalStorageDirectory().getPath();
        binFile = path + "/obdv3h_v1.6.1039.bin";

        runOTA();
    }

    private void runOTA() {
        beginUpgradeBox2();
    }

    private void beginUpgradeBox2() {
        String binfile_path = binFile;
        android.util.Log.d(TAG, "## beginUpgradeBox2 " + binfile_path);
        final File file = new File(binfile_path);
        if (!file.exists() || file.length() <= 0) {
            android.util.Log.e(TAG, "## FileNotFoundException " + binfile_path);
            return;
        }
        //刷固件的业务类
        FirmwareUpdateManager firmwareUpdateManager;
        firmwareUpdateManager = ObdContext.getInstance().getFirmwareUpdateManager();
        firmwareUpdateManager.updateFirware(file, new FirmwareUpdateCallback() {
            @Override
            public void onStart(File filePath) {
                android.util.Log.i("TAG", "### ============ start!   " + filePath);

            }

            @Override
            public void onProgress(int progress) {
                android.util.Log.i("TAG", "### ============ " + progress);
            }

            @Override
            public void onError(Exception exception) {
                android.util.Log.e("TAG", "### ============ERROR: " + exception.getMessage());

            }

            @Override
            public void onComplete() {
                android.util.Log.i("TAG", "### ============ complete");

            }
        });
    }


    public void onClick1(View v) {
        runOTA();
    }

    public void alert(String str) {
        Toast.makeText(this, str, 0).show();
    }
}
