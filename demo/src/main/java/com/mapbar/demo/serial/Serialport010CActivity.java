package com.mapbar.demo.serial;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mapbar.demo.R;
import com.mapbar.obd.ConnectManager;
import com.mapbar.obd.serial.comond.CommondSerialPortManager;
import com.mapbar.obd.serial.comond.IOSecurityException;
import com.mapbar.obd.serial.comond.ReadTimeoutException;
import com.mapbar.obd.serial.comond.SerialPortManagerFactory;
import com.mapbar.obd.serial.utils.OutputStringUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 连续发指令测试
 */
public class Serialport010CActivity extends Activity {
    public static final int ALERT = 1;
    private static final int SEND = 2;
    private static final long LOOP_TIME = 100;
    private static final long INTERVAL = 300;
    private ConnectManager mSerialPortManager;
    private EditText textView2;

    private static final String CMD_03 = "03\r";
    private static final String CMD_0100 = "0100\r";
    private static final String CMD_010C = "010c\r";
    private static final String CMD_010D = "010d\r";


    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
    private List<String> cmds;// = new String[]{, , , };
    final Random random = new Random();

    //    public static final String SERIALPORT_PATH
    private String serialPort = "/dev/ttyMT2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serialport010_cactivity);
        textView2 = (EditText) findViewById(R.id.textView2);

        serialPort = getIntent().getStringExtra("SERIAL_PORT");
        if (TextUtils.isEmpty(serialPort)) {
            alert("未指定串口");
            return;
        } else {
            alert("选定了串口" + serialPort);
        }

        cmds = new ArrayList<>();
        cmds.add(CMD_03);
        cmds.add(CMD_0100);
        cmds.add(CMD_010C);
        cmds.add(CMD_010D);
        cmds.add("ATI\r");
        cmds.add("ATE0\r");
        //设置串口
        mSerialPortManager = SerialPortManagerFactory.create(serialPort, 115200, 10000);
        try {
            mSerialPortManager.init(this, new ConnectManager.Listener() {
                @Override
                public void onEvent(int event, Object data, Object userData) {

                }
            }, null);
        } catch (IOSecurityException ex) {
            ex.printStackTrace();
            alert("SecurityException:" + ex.getMessage());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            alert("Exception:" + e.getMessage());
            return;
        }

//        sendTestMessage();

        runLoop();
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

    private void alert(final String str) {
        if (isFinishing()) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Serialport010CActivity.this, " " + str, Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    protected void onDestroy() {
        stopLoop();
        if (mSerialPortManager != null)
            mSerialPortManager.cleanup();
        fixedThreadPool.shutdownNow();
        super.onDestroy();
    }

//    public void sendTestMessage() {
//        Message msg = handler.obtainMessage(SEND);
//        handler.sendMessageDelayed(msg, LOOP_TIME);
//    }


    public void onClick1(View v) {
//        testReadAndReceive1();
    }

//
//    private void testReadAndReceive1() {
//        testReadAndReceiveArg(CMD_03);
//    }

//    private void testReadAndReceiveArg(final String cmd) {
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//                    sendAndReceiveCMD(cmd);
////                    sendAndReceiveCMD("0100\r");
////                    sendAndReceiveCMD("010c\r");
////                    sendAndReceiveCMD("010d\r");
////                    sendOnlyCMD("ati\r");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    handler.obtainMessage(ALERT, "ERR: " + e.getMessage()).sendToTarget();
//                }
//                return null;
//            }
//        }.execute();
//
//    }

    private void runLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                testThreadPoolMore();
                runLoop();
            }
        }, LOOP_TIME);
    }

    private void stopLoop() {
        handler.removeCallbacksAndMessages(null);
    }

    private void testThreadPoolMore() {
        if (this.isFinishing()) return;
        if (fixedThreadPool != null)
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
//                    try {
//                        Thread.sleep(INTERVAL);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    try {
                        int index = random.nextInt(cmds.size());
                        String cmd = cmds.get(index);
                        String res1 = mSerialPortManager.sendAndReceive(cmd, (byte) 0);

                        StringBuilder sb = new StringBuilder();
                        sb.append(String.format("发送: %s, ", cmd));
                        if (TextUtils.isEmpty(res1)) {
                            sb.append(" => ERROR: 收到空!!!!!!!!!!");
                            handler.obtainMessage(ALERT, sb.toString()).sendToTarget();
                        } else {
                            sb.append(String.format(" => 收到: %s,", res1));
                            String err = "";
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
                            }

                            String str = OutputStringUtil.transferForPrint(sb.toString());
                            if (!TextUtils.isEmpty(err)) {
                                str += ", ERR = " + err;
                            }
                            print(str);
                            handler.obtainMessage(ALERT, str).sendToTarget();
                        }

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
//                testReadAndReceive1();

//                sendTestMessage();
            }
            super.handleMessage(msg);
        }
    };

}
