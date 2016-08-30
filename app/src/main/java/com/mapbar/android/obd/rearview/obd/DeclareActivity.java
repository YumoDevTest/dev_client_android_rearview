package com.mapbar.android.obd.rearview.obd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.obd.impl.SerialPortConnectionCreator;
import com.mapbar.android.obd.rearview.obd.util.FactoryTest;
import com.mapbar.obd.SerialPortManager;

import java.io.IOException;

public class DeclareActivity extends Activity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declare);
        sp = DeclareActivity.this.getSharedPreferences("DeclareActivity", Context.MODE_PRIVATE);
        isGoDeclareActivity = sp.getBoolean("isGoDeclareActivity", true);
        if (!isGoDeclareActivity) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        initView();

    }


    private void initView() {
        tv_declare_result = (TextView) findViewById(R.id.tv_declare_result);
        btn_declare_known = (Button) findViewById(R.id.btn_declare_known);
    }

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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //测试
                String[] result = FactoryTest.testSerialPortConnection(connection);
                tv_declare_result.setVisibility(View.VISIBLE);
                btn_declare_known.setVisibility(View.GONE);
                tv_declare_result.setText("转速" + result[0] + "车速" + result[1]);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (connection != null)
            connection.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            LayoutUtils.showPopWindow("退出", "您确定退出吗？", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                    System.exit(0);
//                }
//            });
//            }

        finish();
        System.exit(0);
        return true;

    }
}
