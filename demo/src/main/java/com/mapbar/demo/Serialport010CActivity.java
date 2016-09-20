package com.mapbar.demo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mapbar.obd.ConnectManager;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.serial.comond.CommondSerialPortManager;
import com.mapbar.obd.serial.comond.ReadTimeoutException;
import com.mapbar.obd.serial.utils.OutputStringUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Serialport010CActivity extends Activity {
    public static final int ALERT = 1;
    private static final int SEND = 2;
    private static final long LOOP_TIME = 100;
    private CommondSerialPortManager mSerialPortManager;
    EditText textView2;

    public static final String SERIALPORT_PATH = "/dev/ttyMT2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serialport010_cactivity);
        textView2 = (EditText) findViewById(R.id.textView2);
        //设置串口
        ObdContext.setSerialPortPath(SERIALPORT_PATH);

        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), 0).show();
        }

//        sendTestMessage();

        testReadAndReceiveMore();
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss.SSS");


    private void showText(String s) {
        if (textView2.getText().length() > 4096) {
            textView2.setText("");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(simpleDateFormat.format(new Date()));
        sb.append("> ");
        sb.append(s);
        textView2.append(sb.toString());
        textView2.setSelection(textView2.getText().length() - 1);
    }

    private void alert(String str) {
        Toast.makeText(this, " " + str, 0).show();

    }


    public void sendTestMessage() {
        Message msg = handler.obtainMessage(SEND);
        handler.sendMessageDelayed(msg, LOOP_TIME);
    }

    private void start() throws IOException {
        mSerialPortManager = ObdContext.getInstance().getSerialPortManager();
        mSerialPortManager.init(this, new ConnectManager.Listener() {
            @Override
            public void onEvent(int event, Object data, Object userData) {

            }
        }, null);
    }


    public void onClick1(View v) {
        testReadAndReceive1();
    }


    static final String CMD_03 = "03\r";
    static final String CMD_0100 = "0100\r";
    static final String CMD_010C = "010c\r";
    static final String CMD_010D = "010d\r";


    private void testReadAndReceive1() {
        testReadAndReceiveArg(CMD_03);
    }

    private void testReadAndReceiveArg(final String cmd) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    sendAndReceiveCMD(cmd);
//                    sendAndReceiveCMD("0100\r");
//                    sendAndReceiveCMD("010c\r");
//                    sendAndReceiveCMD("010d\r");
//                    sendOnlyCMD("ati\r");
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.obtainMessage(ALERT, "ERR: " + e.getMessage()).sendToTarget();
                }
                return null;
            }
        }.execute();

    }


    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
    final String[] cmds = new String[]{CMD_03, CMD_0100, CMD_010C, CMD_010D};
    final Random random = new Random();

    private void testReadAndReceiveMore() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                testThreadMore();
                testReadAndReceiveMore();
            }
        }, LOOP_TIME);
    }

    private void testThreadMore() {
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int index = random.nextInt(cmds.length);
                    String cmd = cmds[index];
                    String res1 = mSerialPortManager.sendAndReceive(cmd, (byte) 0);

                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("发送: %s, ", cmd));
                    if (TextUtils.isEmpty(res1))
                        sb.append(" => ERROR: 收到空!!!!!!!!!!");
                    else
                        sb.append(String.format(" => 收到: %s,", res1));

                    String err = " OK ";
                    if (cmd.equals(CMD_03)) {
                        if (res1.length() < 4) {
                            err = "指令错误!!!!" + cmd;
                        }
                    } else if (cmd.equals(CMD_0100)) {
                        if (res1.length() < 5) {
                            err = "指令错误!!!!" + cmd;
                        }
                    } else if (cmd.equals(CMD_010C)) {
                        if (res1.length() < 5) {
                            err = "指令错误!!!!" + cmd;
                        }
                    } else if (cmd.equals(CMD_010D)) {
                        if (res1.length() < 5) {
                            err = "指令错误!!!!" + cmd;
                        }
                    } else {
                        err = " 意外结果!!";
                    }

                    String str = OutputStringUtil.transferForPrint(sb.toString());
                    str += ", 结果 = " + err;
                    print(str);

                    print(str);


                    handler.obtainMessage(ALERT, str).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                    printError(e.getMessage());
                    handler.obtainMessage(ALERT, "ERR: " + e.getMessage()).sendToTarget();
                }
            }


        });

        try {
            Thread.sleep(LOOP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void print(String str) {
        android.util.Log.e("TAG", str);
    }

    private void printError(String str) {
        android.util.Log.e("ERR", str);
    }

    /**
     * 依次发送 ，发送和接收
     *
     * @throws IOException
     * @throws ReadTimeoutException
     */
    private void testSimple1() throws IOException, ReadTimeoutException {
        String cmd = "ati\r";
        sendAndReceiveCMD(cmd);
        sendOnlyCMD(cmd);
    }

    private void sendAndReceiveCMD(String cmd) throws IOException, ReadTimeoutException {
        String res;
        res = mSerialPortManager.sendAndReceive(cmd, (byte) 0);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("发送: %s", cmd));
        if (TextUtils.isEmpty(res))
            sb.append(" => ERROR: 收到空!!!!!!!!!!");
        else
            sb.append(String.format(" => 收到: %s", res));
        String str = OutputStringUtil.transferForPrint(sb.toString());
        handler.obtainMessage(ALERT, str).sendToTarget();
    }

    private void sendOnlyCMD(String cmd) throws IOException, ReadTimeoutException {
//        mSerialPortManager.sendOnly(cmd);
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(String.format("仅发送: %s", cmd));
//        String str = OutputStringUtil.transferForPrint(sb.toString());
//        handler.obtainMessage(ALERT, str).sendToTarget();
    }


    //    private static int baudrate = 115200;
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ALERT) {
//                alert("" + msg.obj);
                showText(msg.obj.toString());
            }
            if (msg.what == SEND) {
                testReadAndReceive1();

                sendTestMessage();
            }
            super.handleMessage(msg);
        }
    };

}