package com.mapbar.android.obd.rearview.demo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.mapbar.android.log.Log;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.obd.Constants;
import com.mapbar.mapdal.Logger;
import com.mapbar.obd.CandidateDeviceInfo;
import com.mapbar.obd.Config;
import com.mapbar.obd.ExtraTripInfo;
import com.mapbar.obd.Manager;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.Upgrade;
import com.mapbar.obd.serial.ota.FirmwareUpdateManager;
import com.mapbar.obd.serial.ota.OtaSerailPortManager;

import java.io.File;
import java.io.IOException;

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

//        // Initialize SDK
//        try {
//            Manager.getInstance().init(
//                    this,
//                    new Manager.Listener() {
//                        @Override
//                        public void onEvent(int event, Object result) {
//                            android.util.Log.i(TAG, "receive: " + event);
//                        }
//                    },
//                    Environment.getExternalStorageDirectory().getPath()
//                            + "/mapbar/example/obd", "obdua;channel");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Manager.getInstance().setExtraTripInfo(
//                new ExtraTripInfo("0", "ObdDemo"));
//        if (Config.DEBUG) {
//            Logger.d(TAG, "ExtraTripInfo: "
//                    + Manager.getInstance().getExtraTripInfo());
//        }


//        Manager.getInstance().stopReadThreadForUpgrage();

        connection();

        String path = Environment.getExternalStorageDirectory().getPath();
        binFile = path + "/obdv3h_v1.6.1039_factory.bin";


        runOTA();
    }

    private void connection() {
        CandidateDeviceInfo dis = Manager.getInstance()
                .getCandidateDeviceInfo();
        if (dis != null && !TextUtils.isEmpty(dis.mac)) {
            if (!Manager.getInstance().openDevice(dis.mac)) {
                Toast.makeText(this,
                        "当前正在尝试连接 ... ", Toast.LENGTH_LONG)
                        .show();
                if (Config.DEBUG) {
                    Logger.e(TAG, "正在连接中，无法再次发起连接 ... ");
                }
            }
        } else {
            Toast.makeText(this, "没有设备连接历史",
                    Toast.LENGTH_SHORT).show();
        }
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
        String sn = "";

        File file = new File(binfile_path);
        if (!file.exists() || file.length() <= 0) {
            android.util.Log.i(TAG, "FileNotFoundException");
            return;
        }

        FirmwareUpdateManager firmwareUpdateManager;
        OtaSerailPortManager otaSerailPortManager = new OtaSerailPortManager();
        otaSerailPortManager.setmSerialportName(Constants.SERIALPORT_PATH);
        firmwareUpdateManager = new FirmwareUpdateManager(otaSerailPortManager);
        try {
            firmwareUpdateManager.updateFirware(file, sn, new FirmwareUpdateManager.FirmwareUpdateCallback() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            android.util.Log.e(TAG, e.getMessage(), e);
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
