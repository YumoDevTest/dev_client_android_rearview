package com.mapbar.demo.serial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mapbar.demo.R;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.serial.ota.FirmwareUpdateCallback;
import com.mapbar.obd.serial.ota.FirmwareUpdateManager;

import java.io.File;

/**
 * 演示刷固件
 * 可自主刷固件： 步骤：1，拷贝固件文件到sd卡，2.修改本类的binFile路径，3.执行
 * Created by zhangyunfei on 16/8/12.
 */
public class OtaFlushActivity extends Activity {
    private static final String TAG = OtaFlushActivity.class.getSimpleName();
    private File binFile;
    public String serialPort;//= "/dev/ttyMT1";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_ota_serial_activity);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

//        String path = Environment.getExternalStorageDirectory().getPath();

        Intent arg = getIntent();
        String binFileStr = null;
        if (arg == null) {
            alert("缺少指定的参数");
            return;
        }
        binFileStr = arg.getStringExtra("FILE");
        serialPort = arg.getStringExtra("SERIAL_PORT");
        if (TextUtils.isEmpty(serialPort)) {
            alert("未指定串口");
            return;
        } else {
            alert("选定了串口" + serialPort);
        }
        if (TextUtils.isEmpty(binFileStr)) {
            alert("没有指定bin文件");
            return;
        }
        if (!binFileStr.endsWith(".bin")) {
            alert("不是有效的bin文件");
            return;
        }
        binFile = new File(binFileStr);
        if (!binFile.exists()) {
            alert("指定的bin文件不存在");
            return;
        }
//        binFile = path + "/obdv3h_v1.6.1039.bin";

        ObdContext.setSerialPortPath(serialPort);

        runOTA();
    }

    private void runOTA() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否确认刷固件?"); //设置内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                beginUpgradeBox2(binFile);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    private void beginUpgradeBox2(File file) {
        android.util.Log.d(TAG, "## beginUpgradeBox2 " + file.getPath());
        if (!file.exists() || file.length() <= 0) {
            android.util.Log.e(TAG, "## FileNotFoundException " + file.getPath());

            alert("## FileNotFoundException " + file.getPath());

            return;
        }
        //刷固件的业务类
        FirmwareUpdateManager firmwareUpdateManager;
        firmwareUpdateManager = ObdContext.getInstance().getFirmwareUpdateManager();
        firmwareUpdateManager.updateFirware(file, new FirmwareUpdateCallback() {
            @Override
            public void onStart(File filePath) {
                android.util.Log.i("TAG", "### ============ start!   " + filePath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(0);
                    }
                });
            }

            @Override
            public void onProgress(final int progress) {
                android.util.Log.i("TAG", "### ============ " + progress);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }

            @Override
            public void onError(Exception exception) {
                android.util.Log.e("TAG", "### ============ERROR: " + exception.getMessage());
                alert("ERROR: " + exception.getMessage());
            }

            @Override
            public void onComplete() {
                android.util.Log.i("TAG", "### ============ complete");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(100);
                        alert("成功");
                    }
                });
            }
        });
    }


    public void onClick1(View v) {
        runOTA();
    }

    public void alert(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OtaFlushActivity.this, str, 0).show();

            }
        });
    }
}
