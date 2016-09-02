package com.mapbar.android.obd.rearview.obd.impl;


import com.mapbar.android.obd.rearview.obd.ReadTimeoutException;
import com.mapbar.android.obd.rearview.obd.SerialPortConnection;
import com.mapbar.android.obd.rearview.obd.util.OutputStringUtil;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Calendar;

/**
 * 串口连接
 * Created by zhangyunfei on 16/8/11.
 */
class SerialPortConnectionImpl extends SerialPortConnection {
    private static final String TAG = SerialPortConnectionImpl.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static final Object LOCK = new Object();
    private static final int TIMEOUT_INTEVAL = 30000;//超时时间
    private SerailPortChannel mSerailPortChannel;
    private byte[] lastBytesReaded;//上次读到的消息
    //串口监听回调
    private SerailPortChannel.SerailPortChannelCallback callback1 = new SerailPortChannel.SerailPortChannelCallback() {
        @Override
        public void onBytesReadComplete(byte[] bytesRead) {
            lastBytesReaded = bytesRead;
            synchronized (LOCK) {
                LOCK.notify();
            }
        }
    };

    public SerialPortConnectionImpl(String serialportName, int baudrate) {
        super(serialportName, baudrate);
    }

    /**
     * 日志
     *
     * @param tag
     * @param logString
     */
    private static void print(String tag, String logString) {
        if (!DEBUG) return;
        android.util.Log.d(tag, OutputStringUtil.transferForPrint(logString));
    }

    private static void printError(String tag, String logString) {
        if (!DEBUG) return;
        android.util.Log.e(tag, OutputStringUtil.transferForPrint(logString));
    }

    /**
     * 开启
     *
     * @throws IOException
     */
    @Override
    public void start() throws IOException, SecurityException {
        if (getBaudrate() < 0) {
            throw new InvalidParameterException("未设置波特率");
        }
        if (mSerailPortChannel != null)
            throw new IOException("串口已经打开");
        mSerailPortChannel = new SerailPortChannel();
        mSerailPortChannel.open(getSerialportName(), getBaudrate());
        mSerailPortChannel.startListen(callback1);
    }

    /**
     * 关闭
     */
    @Override
    public void stop() {
        if (!mSerailPortChannel.isClosed()) {
            mSerailPortChannel.close();
            mSerailPortChannel = null;
        }
    }

    @Override
    public synchronized byte[] sendAndReceive(byte[] cmd) throws ReadTimeoutException, IOException {
        Calendar start = Calendar.getInstance();
        synchronized (LOCK) {
            try {
                mSerailPortChannel.write(cmd);
                try {
                    LOCK.wait(TIMEOUT_INTEVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (lastBytesReaded != null) {
                    return lastBytesReaded;
                } else {
                    throw new ReadTimeoutException("指令执行超时" + new String(cmd));
                }
            } finally {
                Calendar end = Calendar.getInstance();
                long millis = (end.getTimeInMillis() - start.getTimeInMillis());
                printLogSendAndReceive(cmd, lastBytesReaded, millis);

                lastBytesReaded = null;
            }
        }
    }

    private void printLogSendAndReceive(byte[] cmd, byte[] response, long millis) {
        if (response == null || response.length == 0) {
            printError(TAG, "### 指令回复异常 NULL ,可能发生了超时");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("### 发送: %s", new String(cmd)));
        sb.append(String.format(" \t => \t 收到: %s", OutputStringUtil.transferForPrint(response)));
        sb.append(String.format(" \t耗时:%s", millis));
        print(TAG, sb.toString());
    }

}
