package com.mapbar.android.obd.rearview.obd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.daemon.delaystart.contract.DelayAutoStartService;
import com.mapbar.android.obd.rearview.obd.util.FactoryTest;
import com.mapbar.mapdal.NativeEnv;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.TripSyncService;
import com.mapbar.obd.serial.comond.SerialPortConnection;
import com.mapbar.obd.serial.comond.impl.SerialPortConnectionCreator;
import com.ta.utdid2.android.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DeclareActivity extends Activity implements View.OnClickListener {
    public final String IS_GO_DECLARE_ACTIVITY = "isGoDeclareActivity";
    public static final int MSG_SHOW_SPEED = 1;
    public static final int MSG_APPEND_TEXT = 2;
    private final int DEFAULT_CLICK_NUM = 5;
    private final String TAG = DeclareActivity.class.getSimpleName();
    private long firstTime = 0;
    private final SimpleDateFormat MySimpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    /**
     * 点击次数
     */
    private int clickNum = 0;
    private TextView tv_declare_result;
    private Button btn_declare_known;
    private SharedPreferences sp;
    private boolean isGoDeclareActivity = true;
    private SerialPortConnection connection;
    private Timer timer;
    private RelativeLayout rl_declare_containt;
    private TextView tv_declare_test;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SHOW_SPEED) {
                if (msg.obj == null) {
                    appendLineText("无法获得数据，请检查串口");
                    return;
                }
                String[] result = (String[]) msg.obj;
                if (StringUtils.isEmpty(result[0]) || StringUtils.isEmpty(result[1])) {
                    appendLineText("无法获得数据，请检查串口");
                } else {
                    appendLineText(String.format(Locale.getDefault(), "车速: %s km/h  转速: %s r/min  ", result[1], result[0]));
                }
            } else if (msg.what == MSG_APPEND_TEXT) {
                if (msg.obj != null) {
                    String str = msg.obj.toString();
                    if (tv_declare_result.getText().length() > 900) {
                        tv_declare_result.setText("");
                    }
                    tv_declare_result.append(str + "\r\n");
                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopBackgroundService();

        //发送停止延迟启动消息
        Intent intentDelayStart = new Intent(DelayAutoStartService.ACTION_STOP_DELAY_RUN);
        sendBroadcast(intentDelayStart);

        setContentView(R.layout.activity_declare);
        initView();
        sp = DeclareActivity.this.getSharedPreferences("DeclareActivity", Context.MODE_PRIVATE);
        isGoDeclareActivity = sp.getBoolean("isGoDeclareActivity", true);
        if (!isGoDeclareActivity) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        Log.d(TAG, "onCreate");

    }


    private void stopBackgroundService() {
        Intent intent = new Intent("com.mapbar.obd.stopservice");
        sendBroadcast(intent);
        if (!NativeEnv.isServiceRunning(TripSyncService.class.getName())) {
            Intent intent1 = new Intent(this, TripSyncService.class);
            stopService(intent1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onCreate");
    }

    private void initView() {
        tv_declare_result = (TextView) findViewById(R.id.tv_declare_result);
        btn_declare_known = (Button) findViewById(R.id.btn_declare_known);
        rl_declare_containt = (RelativeLayout) findViewById(R.id.rl_declare_containt);
        btn_declare_known = ((Button) findViewById(R.id.btn_declare_known));
        tv_declare_test = ((TextView) findViewById(R.id.tv_declare_test));
        tv_declare_test.setOnClickListener(this);
        btn_declare_known.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.tv_declare_test:
                    test();
                    break;
                case R.id.btn_declare_known:
                    startActivity(new Intent(this, MainActivity.class));
                    //记录是否进入声明页
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(IS_GO_DECLARE_ACTIVITY, false);
                    editor.commit();
                    finish();
            }

        }
    }

    /**
     * 工厂测试数据
     */
    private void test() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) { //如果两次按键时间间隔大于2秒，则不退出
            firstTime = secondTime;//更新firstTime
            clickNum = 0;
        } else {//两次按键小于2秒时
            clickNum++;
            if (clickNum == DEFAULT_CLICK_NUM - 1) {
                clickNum = 0;
                entryFactoryMode();


            }
        }
    }


    private void entryFactoryMode() {
        alert("进入工厂测试模式");
        appendLineText("进入工厂测试模式");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_declare_result.setVisibility(View.VISIBLE);
                btn_declare_known.setVisibility(View.GONE);
            }
        });

        ObdContext.setSerialPortPath(Constants.SERIALPORT_PATH);
        //打开串口
        if (connection == null) {
            try {
                connection = SerialPortConnectionCreator.create(Constants.SERIALPORT_PATH, 115200);
                connection.start();
                appendLineText("串口打开成功,串口名称=" + Constants.SERIALPORT_PATH);
            } catch (Exception e) {
                e.printStackTrace();
                appendLineText("串口打开失败,串口名称=" + Constants.SERIALPORT_PATH + ", 异常=" + e.getMessage());
                return;
            }
        }
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String[] result = FactoryTest.testSerialPortConnection(connection);
                mHandler.obtainMessage(MSG_SHOW_SPEED, result).sendToTarget();
            }
        }, 1000, 1000);
    }

    /**
     * 展示 工厂测试模式的测试结果
     *
     * @param str
     */
    private void appendLineText(String str) {
        String msg = String.format(Locale.getDefault(), "[%s] %s", MySimpleDateFormat.format(new Date()), str);
        mHandler.obtainMessage(MSG_APPEND_TEXT, msg).sendToTarget();
    }

    private void alert(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DeclareActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connection != null)
            connection.stop();
        if (timer != null) {
            timer.cancel();
        }
        Log.d(TAG, "onDestroy()");
    }

}
