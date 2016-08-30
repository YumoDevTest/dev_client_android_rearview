package com.mapbar.android.obd.rearview.obd.impl;

import android.text.TextUtils;

import com.mapbar.android.obd.rearview.obd.util.LogHelper;
import com.mapbar.android.obd.rearview.obd.util.OutputStringUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;


/**
 * 串口通道
 * Created by zhangyunfei on 16/8/10.
 */
class SerailPortChannel {
    private static final String TAG = "COMMOND_SERAILPORT";
    public static byte TEMINAL_CMD = 0x3E;// 符号">" ，整数是62
    public static byte TEMINAL_OTA = 0x0A;//换行键 ，整数是10
    public static byte TEMINAL_R = 0x0d;//回车键 /r，整数是13,//发送指令时终结符， 回车键 ，整数是13
    private static boolean DEBUG = false;//是否开启日志
    protected OutputStream mOutputStream;
    protected SerialPort mSerialPort;
    protected InputStream mInputStream;
    private byte mTeminalByte = TEMINAL_CMD;//终结符
    private DataInputStream mDataInputStream;
    private int baudrate = 115200;
    private boolean isClosed = true;
    private boolean listening = false;
    private Thread thread;
    private SerailPortChannelCallback mSerailPortChannelCallback;

    private static void print(String tag, String str) {
        if (!DEBUG) return;
        LogHelper.d(tag, str);
    }

    public synchronized void open(String serialportName, int baudrate) throws IOException {
        if (TextUtils.isEmpty(serialportName)) {
            throw new InvalidParameterException("未设置串口名称");
        }
        if (baudrate < 0) {
            throw new InvalidParameterException("未设置波特率");
        }
        print(TAG, "#### 准备打开串口 name = " + serialportName + ", baudrate = " + baudrate);
        this.baudrate = baudrate;
        mSerialPort = new SerialPort(new File(serialportName), baudrate, 0);
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        mDataInputStream = new DataInputStream(mInputStream);
        isClosed = false;
        print(TAG, "#### 打开串口成功" + serialportName);
    }

    public synchronized boolean isClosed() {
        return isClosed;
    }

    public synchronized void close() {
        print(TAG, "#### 准备关闭串口");
        if (mDataInputStream != null)
            try {
                mDataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (mInputStream != null)
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (mOutputStream != null)
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        listening = false;
        isClosed = true;
        if (thread != null) {
            try {
                thread.interrupt();
                thread = null;
            } catch (Exception ex) {
                ex.printStackTrace();
                LogHelper.d(TAG, "#### 线程被强制终止");
            }
        }
    }

    public synchronized void write(byte[] bytes) throws IOException {
        if (bytes == null)
            throw new NullPointerException();
        if (bytes.length == 0)
            throw new InvalidParameterException("发送给串口的指令结束字节长度不能为0");
        if (bytes[bytes.length - 1] != TEMINAL_R) {
            throw new InvalidParameterException("发送给串口的指令结束字节必须是0x0d");
        }
        String outString = OutputStringUtil.transferForPrint(bytes);
        print(TAG, "#### [写入] len = " + bytes.length + ", 内容是: " + outString);
        if (mOutputStream == null)
            throw new IOException("#### 写入数据时，发现串口已经关闭");
        if (isClosed)
            throw new IOException("#### 写入数据时，发现串口已经关闭");
        mOutputStream.write(bytes, 0, bytes.length);
    }

    public synchronized void startListen(SerailPortChannelCallback channelCallback) throws InvalidObjectException {
        print(TAG, "#### 开启监听");
        if (listening) {
            throw new InvalidObjectException("#### 已经启动 startListen");
        }
        if (isClosed) {
            throw new InvalidObjectException("#### 串口未打开，请先打开串口");
        }
        this.mSerailPortChannelCallback = channelCallback;
        if (thread == null || thread.isInterrupted()) {
            thread = new Thread(new KeepReadRunnable(this, getTeminalByte()));
            thread.start();
        }
    }

    /**
     * 获得 终结符，接收消息时，收到该字符（字节）即表示结束
     *
     * @return
     */
    private byte getTeminalByte() {
        return mTeminalByte;//TEMINAL_CMD;//return TEMINAL_R;
    }

    private void setTeminalByte(byte teminalByte) throws InvalidObjectException {
        if (listening || !isClosed)
            throw new InvalidObjectException("### 监听已经启动，无法更改终结符");
        mTeminalByte = teminalByte;
    }

    private synchronized void raiseCallback(byte[] bytesReaded) {
        if (mSerailPortChannelCallback != null) {
            mSerailPortChannelCallback.onBytesReadComplete(bytesReaded);
//            print(TAG, "#### 触发回调");
        }
    }

    public interface SerailPortChannelCallback {
        void onBytesReadComplete(byte[] bytes0);
    }

    private static class KeepReadRunnable implements Runnable {
        private SerailPortChannel serailPortChannel;
        private byte teminalByte;

        public KeepReadRunnable(SerailPortChannel serailPortChannel1, byte teminal) {
            if (serailPortChannel1 == null)
                throw new NullPointerException();
            this.serailPortChannel = serailPortChannel1;
            this.teminalByte = teminal;
        }

        @Override
        public synchronized void run() {
            print(TAG, "#### 监听线程已经启动");
            if (this.serailPortChannel.listening)
                return;
            if (this.serailPortChannel.mDataInputStream == null)
                return;
            this.serailPortChannel.listening = true;
            try {
                byte[] buffer = new byte[1024];
                int cursor = 0;
                while (this.serailPortChannel.listening) {
//                    print(TAG, "#### 串口监听LOOP once ");
                    if (this.serailPortChannel.mDataInputStream == null) {
                        throw new IOException("#### 串口mDataInputStream已经关闭");
                    }
                    if (this.serailPortChannel.mDataInputStream.available() > 0) {
                        byte b = this.serailPortChannel.mDataInputStream.readByte();
                        buffer[cursor] = b;
                        cursor++;
                        //遇到结束符
                        if (b == teminalByte) {
                            byte[] newArray = new byte[cursor];
                            System.arraycopy(buffer, 0, newArray, 0, newArray.length);
                            cursor = 0;
                            print(TAG, "#### [收到] len=" + newArray.length + " 字符= " + OutputStringUtil.transferForPrint(newArray));
                            this.serailPortChannel.raiseCallback(newArray);
                        }
                    } else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                print(TAG, "#### 监听线程异常终止");
            } finally {
                if (this.serailPortChannel != null)
                    this.serailPortChannel.listening = false;
            }
        }
    }

}
