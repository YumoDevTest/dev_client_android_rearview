package com.mapbar.android.obd.rearview.lib.serialportsearch;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.android.obd.rearview.modules.common.MainActivity;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.foundation.base.BaseActivity;
import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.foundation.utils.SafeHandler;


/**
 * Created by zhangyh on 2016/10/18.
 * 串口扫描的Activity
 */
public class SerialportFinderActivity extends BaseActivity {
    private final static String TAG = "SerialportFinderActivity";
    private ServiceConnection serialportFinderconn;
    private Messenger serialportMessenger;
    private Handler mHandler;
    private ProgressBar serialport_pb;
    private TextView serialport_tv;
    private Messenger serviceMessenger;
    private RelativeLayout contentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = (RelativeLayout) View.inflate(this, R.layout.serialport_finder, null);
        setContentView(contentView);
        initView();
        mHandler = new MyHandler(this);
        bindService();
    }

    /**
     * 绑定服务
     */
    private void bindService() {
        Intent intent = new Intent(this, SerialportFinderService.class);
        bindService(intent, serialportFinderconn = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                LogUtil.d(TAG, "绑定成功");
                serialportMessenger = new Messenger(mHandler);
                serviceMessenger = new Messenger(iBinder);
                Message msg = Message.obtain();
                msg.what = SerialportConstants.CONNECTIONSUCCESS;
                msg.replyTo = serialportMessenger;
                try {
                    serviceMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                LogUtil.d(TAG, "解除绑定");
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        serialport_pb = (ProgressBar) findViewById(R.id.serialport_pb);
        serialport_tv = (TextView) findViewById(R.id.serialport_tv);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serialportFinderconn != null) {
            unbindService(serialportFinderconn);
        }
    }

    /**
     * 通过handler进行传递消息
     */
    private static class MyHandler extends SafeHandler<SerialportFinderActivity> {

        public MyHandler(SerialportFinderActivity object) {
            super(object);
        }

        @Override
        public void handleMessage(Message msg) {
            if (getInnerObject() == null) return;
            //串口服务发送过来的消息
            if (msg.what == SerialportConstants.SERIALPORT_FIND_SUCCESS) {
                LogUtil.d(TAG, "从服务获取串口号成功");
                getInnerObject().serialport_tv.setText("扫描串口成功!");
                Bundle bundle = msg.getData();
                String serialport = bundle.getString("serialport");
                //获取串口之后
                ObdContext.configSerialport(serialport, SerialportConstants.BAUDRATE_DEFAULT, SerialportConstants.TIMEOUT_DEFAULT, SerialportConstants.IS_DEBUG_SERIALPORT);
                getInnerObject().startActivity(new Intent(getInnerObject(), MainActivity.class));
                getInnerObject().finish();
            }
            //扫描失败退出或者重试
            else if (msg.what == SerialportConstants.SERIALPORT_FIND_FAILURE) {
                getInnerObject().serialport_tv.setText("查找串口失败!");
                AlertDialog.Builder dialog = new AlertDialog.Builder(getInnerObject());
                dialog.setMessage("扫描失败,是否重试?");
                dialog.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        getInnerObject().finish();
                        MyApplication.getInstance().exitApplication();
                    }
                });
                dialog.setPositiveButton("重试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        getInnerObject().serialport_tv.setText("扫描中...");
                        Message msg = Message.obtain();
                        msg.what = SerialportConstants.SERIALPORT_FIND_RESCAN;
                        try {
                            getInnerObject().serviceMessenger.send(msg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();
            }
        }
    }
}
