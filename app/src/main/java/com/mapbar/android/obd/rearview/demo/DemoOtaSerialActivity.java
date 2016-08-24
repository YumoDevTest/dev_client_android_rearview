package com.mapbar.android.obd.rearview.demo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.mapbar.android.log.Log;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.obd.Constants;
import com.mapbar.obd.Manager;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.Upgrade;
import com.mapbar.obd.serial.ota.FirmwareUpdateManager;
import com.mapbar.obd.serial.ota.OtaSerailPortConnection;

import java.io.File;

/**
 * Created by zhangyunfei on 16/8/12.
 */
public class DemoOtaSerialActivity extends Activity {
    private static final String TAG = DemoOtaSerialActivity.class.getSimpleName();
    String binFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_ota_serial_activity);

        ObdContext.getObdContext();

        String path = Environment.getExternalStorageDirectory().getPath();
        binFile = path + "/obdv3h_v1.6.1039.bin";

        runOTA();
    }

    private void runOTA() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
//                    beginUpgradeBox();
                    beginUpgradeBox2();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void beginUpgradeBox2() {
        String binfile_path = binFile;
        android.util.Log.d(TAG, "## beginUpgradeBox2 " + binfile_path);
        File file = new File(binfile_path);
        if (!file.exists() || file.length() <= 0) {
            android.util.Log.e(TAG, "## FileNotFoundException " + binfile_path);
            return;
        }
        //串口连接
        OtaSerailPortConnection connection = new OtaSerailPortConnection(Constants.SERIALPORT_PATH);
        //刷固件的业务类
        FirmwareUpdateManager firmwareUpdateManager;
        firmwareUpdateManager = new FirmwareUpdateManager(connection);
        try {
            firmwareUpdateManager.updateFirware(file, new FirmwareUpdateManager.FirmwareUpdateCallback() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "## " + e.getMessage(), e);
        }
    }

    private void beginUpgradeBox() {
        android.util.Log.i("TAG", "### ============准备启动beginUpgradeBox");
        String binfile_path = binFile;
        File file = new File(binfile_path);
        if (!file.exists() || file.length() <= 0) {
            alert("FileNotFoundException");
            return;
        }
        Manager.getInstance().upgradeBox(binfile_path /*testFilePath*/, new Upgrade() {
            public void onEvent(int event, int progress) {
                switch (event) {
                    case Upgrade.Event.started: {
                        Log.e("zc", "Flash started");
                    }
                    break;
                    case Upgrade.Event.progress: {
                        Log.e("zc", "progress = " + progress);
                    }
                    break;
                    case Upgrade.Event.succ:
                        Log.e("zc", "升级成功!");
                        break;
                    case Upgrade.Event.failed_readFile:
                        Log.e("zc", "读取升级文件失败");
                        break;
                    case Upgrade.Event.failed:
                        Log.e("zc", "升级失败");
                        break;

                    default:
                        break;
                }
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
