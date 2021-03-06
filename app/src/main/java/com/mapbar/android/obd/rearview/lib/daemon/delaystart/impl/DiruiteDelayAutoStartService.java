//package com.mapbar.android.obd.rearview.lib.daemon.delaystart.impl;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Message;
//import android.widget.Toast;
//
//import com.mapbar.android.obd.rearview.lib.daemon.delaystart.contract.DelayAutoStartService;
//import com.mapbar.android.obd.rearview.util.SafeHandler;
//
///**
// * 迪瑞特，开机自启管理器
// * Created by zhangyunfei on 16/9/1.
// */
//public class DiruiteDelayAutoStartService extends DelayAutoStartService {
//    //    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
//    private static final String TAG = "DelayAutoStartService";
//    private static final boolean DEBUG = true;
//    private static final float DELAY_SECOND = 10f;// 延迟时间,单位分钟
//    private static final long DELAY_HEARTBEAT = 2000;//毫秒
//
//    private static final int MSG_START_APP = 1;
//    private static final int MSG_ALERT = 2;
//    private static final int MSG_HEARTBEAT = 3;
//    private Handler handler;
//    private boolean isDelayTimeStarted = false;
//
//    public DiruiteDelayAutoStartService() {
//        handler = new MyHandler(this);
//    }
//
//    private static void runApp(Context context) {
//        if (context == null) return;
//        android.util.Log.i(TAG, "## [准备启动程序] " + context.getPackageName());// + intent.getAction());
//        //启动应用，参数为需要自动启动的应用的包名
//        Intent newIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//        context.startActivity(newIntent);
//    }
//
//    private void runApp() {
//        runApp(getContext());
//    }
//
//    private void alert() {
//        Toast.makeText(getContext(), String.format("将在%s分钟后启动图吧汽车卫士", DELAY_SECOND), Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void start(Context context) {
//        super.start(context);
////        if (DEBUG) {//触发心跳检测
////            handler.sendMessageDelayed(handler.obtainMessage(MSG_HEARTBEAT), DELAY_HEARTBEAT);
////        }
//    }
//
//    /**
//     * * 当接收到消息时
//     *
//     * @param intent
//     */
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        android.util.Log.i(TAG, "## 迪瑞特自启动程序收到action " + intent.getAction());// );
//        if (intent.getAction().equals(DelayAutoStartService.ACTION_DELAY_START_APP)) {
//            if (isDelayTimeStarted) return;//确保只启动一次
//            startDelayTime();
//            isDelayTimeStarted = true;
//        } else if (intent.getAction().equals(DelayAutoStartService.ACTION_STOP_DELAY_RUN)) {
//            clearAllMessage();
//        }
//    }
//
//    private void startDelayTime() {
//        android.util.Log.i(TAG, String.format("## 将在%s分钟后启动图吧汽车卫士", DELAY_SECOND));
//        handler.obtainMessage(MSG_ALERT).sendToTarget();
//        handler.sendMessageDelayed(handler.obtainMessage(MSG_START_APP), (int) (DELAY_SECOND * 60 * 1000));
//    }
//
//    /**
//     * 释放资源
//     */
//    @Override
//    public void clear() {
//        if (handler != null) {
//            clearAllMessage();
//        }
//        super.clear();
//    }
//
//    private void clearAllMessage() {
//        if (handler == null) return;
//        handler.removeMessages(MSG_START_APP);
//        handler.removeMessages(MSG_ALERT);
//    }
//
//    private static class MyHandler extends SafeHandler<DiruiteDelayAutoStartService> {
//
//        public MyHandler(DiruiteDelayAutoStartService object) {
//            super(object);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            if (getInnerObject() == null) return;
//            if (MSG_START_APP == msg.what) {
//                removeMessages(MSG_START_APP);
//                getInnerObject().runApp();
//            } else if (MSG_ALERT == msg.what) {
//                removeMessages(MSG_ALERT);
//                getInnerObject().alert();
//            } else if (MSG_HEARTBEAT == msg.what) {
//                removeMessages(MSG_HEARTBEAT);
//                android.util.Log.i(TAG, "##[心跳],是否已经执行过延迟自启动=" + getInnerObject().isDelayTimeStarted);
//                sendMessageDelayed(obtainMessage(MSG_HEARTBEAT), DELAY_HEARTBEAT);
//            }
//        }
//    }
//
//
//}
