package com.mapbar.android.obd.rearview.obd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.common.LayoutUtils;
import com.mapbar.android.obd.rearview.framework.common.TimeUtils;
import com.mapbar.android.obd.rearview.lib.demon.delaystart.contract.DelayAutoStartService;
import com.mapbar.android.obd.rearview.obd.impl.SerialPortConnectionCreator;
import com.mapbar.android.obd.rearview.obd.util.FactoryTest;
import com.mapbar.obd.SerialPortManager;
import com.ta.utdid2.android.utils.StringUtils;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DeclareActivity extends Activity implements View.OnClickListener {
    public final String IS_GO_DECLARE_ACTIVITY = "isGoDeclareActivity";
    private final int DEFAULT_CLICK_NUM = 5;
    private final String TAG = DeclareActivity.class.getSimpleName();
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
            if (StringUtils.isEmpty(result[0]) || StringUtils.isEmpty(result[1])) {
                Toast.makeText(DeclareActivity.this, "数据异常", Toast.LENGTH_SHORT).show();
                tv_declare_result.setText(String.format(Locale.getDefault(), "车速:-- 转速:-- 刷新时间:%s", time));
            } else {
                tv_declare_result.setText(String.format(Locale.getDefault(), "车速:%skm/h  转速:%sr/min  刷新时间:%s", result[1], result[0], time));
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //发送停止延迟启动消息
        Intent intentDelayStart = new Intent(DelayAutoStartService.ACTION_STOP_START_APP);
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
        SerialPortManager.getInstance().setPath(Constants.SERIALPORT_PATH);
        //打开串口
        if (connection == null) {
            connection = SerialPortConnectionCreator.create(Constants.SERIALPORT_PATH, 115200);
            try {
                connection.start();
            } catch (Exception e) {
                e.printStackTrace();
                alert("串口打开失败," + e.getMessage());
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
                result = FactoryTest.testSerialPortConnection(connection);
                mHandler.sendEmptyMessage(0);
                Log.d(TAG, "刷新数据");
            }
        }, 1000, 1000);
    }

    private void alert(final String msg) {
        mHandler.post(new Runnable() {
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
