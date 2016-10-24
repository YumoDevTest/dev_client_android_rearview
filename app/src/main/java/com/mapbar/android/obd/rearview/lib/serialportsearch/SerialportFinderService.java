package com.mapbar.android.obd.rearview.lib.serialportsearch;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.foundation.utils.SafeHandler;

/**
 * Created by zhangyh on 2016/10/18.
 * 查找串口的服务,app和服务都通过打开这个服务找到相应串口
 */
public class SerialportFinderService extends Service {

    private static final String TAG = "SerialportFinderService";
    private SerialportFinderHandler handler = new SerialportFinderHandler(this);
    private Messenger serviceMessenger = new Messenger(handler);
    private Messenger clientMessenger = null;
    private MyAsyncTask myAsyncTask;
    private int flag = 0;

    @Override
    public IBinder onBind(Intent intent) {
        myAsyncTask = getAsyncTask();
        if (flag == 0) {
            myAsyncTask.execute();
            flag = 1;
        }
        return serviceMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myAsyncTask != null && myAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            myAsyncTask.cancel(true);
            flag = 0;
        }
    }

    private MyAsyncTask getAsyncTask() {
        return new MyAsyncTask();
    }

    //接收客户端发送过来的连接消息和客户端的Messenger,通过这个Messenger里面的handler给客户端联通
    private static class SerialportFinderHandler extends SafeHandler<SerialportFinderService> {
        public SerialportFinderHandler(SerialportFinderService object) {
            super(object);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SerialportConstants.CONNECTIONSUCCESS:
                    LogUtil.d(TAG, "接收到客户端发送过来连接成功的消息");
                    getInnerObject().clientMessenger = msg.replyTo;
                    break;
                case SerialportConstants.SERIALPORT_FIND_RESCAN:
                    if (getInnerObject().flag == 0) {
                        getInnerObject().myAsyncTask = getInnerObject().getAsyncTask();
                        getInnerObject().myAsyncTask.execute();
                        getInnerObject().flag = 1;
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            new SerialportFinder().findAvailableSerialPort(new SerialportFinder.OnOpenSerialPortLinstener() {

                @Override
                public void onOpenSerialPortStart(String serialPortName, int baudrate, int timeout) {
                    LogUtil.d(TAG, "开始检测的串口号:" + serialPortName);
                }

                @Override
                public void onOpenSerialPortSuccess(final String serialPortName) {
                    LogUtil.d(TAG, "打开串口成功:" + serialPortName);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //获取结果之后通过clientMessenger中的handler发送到客户端
                            LogUtil.d(TAG, "最后的结果:::::" + serialPortName);
                            SerialportSPUtils.setSerialport(SerialportFinderService.this, serialPortName);
                            Message msg = Message.obtain();
                            msg.what = SerialportConstants.SERIALPORT_FIND_SUCCESS;
                            Bundle bundle = new Bundle();
                            bundle.putString("serialport", serialPortName);
                            msg.setData(bundle);
                            try {
                                clientMessenger.send(msg);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    myAsyncTask.cancel(true);
                    flag = 0;
                }

                @Override
                public void onOpenSerialPortFailure(String result) {
                    try {
                        clientMessenger.send(Message.obtain(null, SerialportConstants.SERIALPORT_FIND_FAILURE));
                        myAsyncTask.cancel(true);
                        flag = 0;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            //判断标志位,如果AysncTask取消了就退出子线程
            while (true) {
                if (isCancelled()) {
                    return null;
                }
            }
        }
    }
}
