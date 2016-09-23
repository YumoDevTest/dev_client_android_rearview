package com.mapbar.demo.serial;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.mapbar.demo.R;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.foundation.utils.SafeHandler;
import com.mapbar.obd.serial.comond.ReadTimeoutException;
import com.mapbar.obd.serial.comond.SerialPortConnection;
import com.mapbar.obd.serial.comond.impl.SerialPortConnectionCreator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SerialportToolsActivity extends Activity {
    public static final int ALERT = 1;
    private static final int SEND = 2;
    private static final long LOOP_TIME = 100;
    private static final int MSG_APPEND_TEXT = 2;
    private static final int MSG_LOOP = 3;
    private SerialPortConnection connection;

//    public static final String SERIALPORT_PATH = "/dev/ttyMT2";
    private Button btnSend;
    private EditText edittext_input;
    private EditText txt_content;
    private android.os.Handler mHandler;
    private Switch switch1;
    private String serialPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serialporttools_activity);


        serialPort = getIntent().getStringExtra("SERIAL_PORT");
        if (TextUtils.isEmpty(serialPort)) {
            alert("未指定串口");
            return;
        } else {
            alert("选定了串口" + serialPort);
        }

        txt_content = (EditText) findViewById(R.id.txt_content);
        edittext_input = (EditText) findViewById(R.id.edittext_input);
        btnSend = (Button) findViewById(R.id.btnSend);
        switch1 = (Switch) findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                    stopLoop();
            }
        });
        mHandler = new MyHandler(this);

        //设置串口
        ObdContext.setSerialPortPath(serialPort);
        openSerialport();


        openSerialport();


    }

    private void openSerialport() {
        //打开串口
        if (connection == null) {
            try {
                connection = SerialPortConnectionCreator.create(serialPort, 115200);
                connection.start();
                appendLineText("串口打开成功,串口名称=" + serialPort);

                String cmd = "ATE0";
                writeString(cmd);

            } catch (Exception e) {
                e.printStackTrace();
                appendLineText("串口打开失败,串口名称=" + serialPort + ", 异常=" + e.getMessage());
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (connection != null)
            connection.stop();
        super.onDestroy();
    }

    private void appendLineTextReceive(String s) {
        appendLineText("收到:" + s + "\n\n");
    }

    private void appendLineTextSend(String s) {
        appendLineText("发送:" + s + "\r\n");
    }

    private void appendLineText(String str) {
        String msg = String.format(Locale.getDefault(), "[%s] %s", MySimpleDateFormat.format(new Date()), str);
        mHandler.obtainMessage(MSG_APPEND_TEXT, msg).sendToTarget();
    }

    SimpleDateFormat MySimpleDateFormat = new SimpleDateFormat("ss.SSS");

    private void alert(String str) {
        Toast.makeText(this, " " + str, 0).show();

    }

    public void onClick1(View v) {
        if (connection == null) return;
        String str = edittext_input.getText().toString();
        writeString(str);

        if (isLoop()) {
            mHandler.obtainMessage(MSG_LOOP).sendToTarget();
        }
    }

    public void writeString(String cmd) {
        if (TextUtils.isEmpty(cmd))
            return;
        cmd += "\r";
        try {
            byte[] res = connection.sendAndReceive(cmd.getBytes());

            appendLineTextSend(cmd);
            appendLineTextReceive(new String(res));


        } catch (ReadTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static final String CMD_03 = "03\r";
    static final String CMD_0100 = "0100\r";
    static final String CMD_010C = "010c\r";
    static final String CMD_010D = "010d\r";


    private void print(String str) {
        android.util.Log.e("TAG", str);
    }

    private void printError(String str) {
        android.util.Log.e("ERR", str);
    }

    public boolean isLoop() {
        return switch1.isChecked();
    }

    public void stopLoop() {
        mHandler.removeMessages(MSG_LOOP);
    }


    private static class MyHandler extends SafeHandler<SerialportToolsActivity> {

        public MyHandler(SerialportToolsActivity object) {
            super(object);
        }

        @Override
        public void handleMessage(Message msg) {
            if (getInnerObject() == null) return;
            if (msg.what == MSG_APPEND_TEXT) {
                if (msg.obj != null) {
                    String str = msg.obj.toString();
                    getInnerObject().txt_content.append(str);
                    getInnerObject().txt_content.setSelection(getInnerObject().txt_content.getText().length() - 1);
                }
            }
            if (msg.what == MSG_LOOP) {
                removeMessages(MSG_LOOP);
                if (TextUtils.isEmpty(getInnerObject().txt_content.getText())) {

                } else {
                    getInnerObject().btnSend.performClick();

                    sendMessageDelayed(obtainMessage(MSG_LOOP), 500);
                }


            }

        }
    }
}
