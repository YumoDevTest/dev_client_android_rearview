package com.mapbar.android.obd.rearview.obd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.common.LayoutUtils;
import com.mapbar.android.obd.rearview.framework.common.TimeUtils;
import com.mapbar.android.obd.rearview.obd.impl.SerialPortConnectionCreator;
import com.mapbar.android.obd.rearview.obd.util.FactoryTest;
import com.mapbar.obd.SerialPortManager;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DeclareActivity extends Activity implements View.OnClickListener {
    public final String IS_GO_DECLARE_ACTIVITY = "isGoDeclareActivity";
    private final int DEFAULT_CLICK_NUM = 5;
    private long firstTime = 0;
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
    private String[] result;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_declare_result.setVisibility(View.VISIBLE);
            btn_declare_known.setVisibility(View.GONE);
            String time = TimeUtils.getDateHHMMss(System.currentTimeMillis());
            tv_declare_result.setText(String.format(Locale.getDefault(), "转速:%s;车速:%s;时间:%s", result[0], result[1], time));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declare);
        initView();
        sp = DeclareActivity.this.getSharedPreferences("DeclareActivity", Context.MODE_PRIVATE);
        isGoDeclareActivity = sp.getBoolean("isGoDeclareActivity", true);
        if (!isGoDeclareActivity) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }


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
                SerialPortManager.getInstance().setPath(Constants.SERIALPORT_PATH);
                //打开串口
                if (connection == null) {
                    connection = SerialPortConnectionCreator.create(Constants.SERIALPORT_PATH, 115200);
                    try {
                        connection.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(DeclareActivity.this, "串口不通", Toast.LENGTH_SHORT).show();
                    }
                }
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        result = FactoryTest.testSerialPortConnection(connection);
                        mHandler.sendEmptyMessage(0);

                    }
                }, 1000, 1000);


            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (connection != null)
            connection.stop();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LayoutUtils.showPopWindow("退出", "您确定退出吗？", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    System.exit(0);
                }
            }, rl_declare_containt);
        }

        return true;

    }
}
