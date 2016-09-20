package com.mapbar.android.obd.rearview.modules.common;

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

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.util.EnvironmentMsgUtils;
import com.mapbar.obd.foundation.base.MyBaseActivity;
import com.mapbar.android.obd.rearview.lib.config.Constants;
import com.mapbar.android.obd.rearview.util.FactoryTest;
import com.mapbar.android.obd.rearview.util.TraceUtil;
import com.mapbar.mapdal.NativeEnv;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.TripSyncService;
import com.mapbar.obd.foundation.utils.SafeHandler;
import com.mapbar.obd.serial.comond.SerialPortConnection;
import com.mapbar.obd.serial.comond.impl.SerialPortConnectionCreator;
import com.ta.utdid2.android.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class LauncherActivity extends MyBaseActivity implements View.OnClickListener {
    public final String IS_GO_DECLARE_ACTIVITY = "isGoDeclareActivity";
    public static final int MSG_SHOW_SPEED = 1;
    public static final int MSG_APPEND_TEXT = 2;
    private final int DEFAULT_CLICK_NUM = 5;
    private final String TAG = LauncherActivity.class.getSimpleName();
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
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declare);
        printBaiscMsg();
        TraceUtil.start();
        stopBackgroundService();
        sp = LauncherActivity.this.getSharedPreferences("LauncherActivity", Context.MODE_PRIVATE);
        isGoDeclareActivity = sp.getBoolean("isGoDeclareActivity", true);
        if (!isGoDeclareActivity) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        mHandler = new MyHandler(this);
        initView();
    }

    private void printBaiscMsg() {
        LinkedHashMap<String, String> basicMsg = EnvironmentMsgUtils.getBasicMsg(this);
        Set<String> keys = basicMsg.keySet();
        for (String key :
                keys) {
            Log.e(TAG, key + basicMsg.get(key));
        }
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


    private static class MyHandler extends SafeHandler<LauncherActivity> {

        public MyHandler(LauncherActivity object) {
            super(object);
        }

        @Override
        public void handleMessage(Message msg) {
            if (getInnerObject() == null) return;
            if (msg.what == MSG_SHOW_SPEED) {
                if (msg.obj == null) {
                    getInnerObject().appendLineText("无法获得数据，请检查串口");
                    return;
                }
                String[] result = (String[]) msg.obj;
                if (StringUtils.isEmpty(result[0]) || StringUtils.isEmpty(result[1])) {
                    getInnerObject().appendLineText("无法获得数据，请检查串口");
                } else {
                    getInnerObject().appendLineText(String.format(Locale.getDefault(), "车速: %s km/h  转速: %s r/min  ", result[1], result[0]));
                }
            } else if (msg.what == MSG_APPEND_TEXT) {
                if (msg.obj != null) {
                    String str = msg.obj.toString();
                    if (getInnerObject().tv_declare_result.getText().length() > 900) {
                        getInnerObject().tv_declare_result.setText("");
                    }
                    getInnerObject().tv_declare_result.append(str + "\r\n");
                }
            }

        }
    }

    ;
}
