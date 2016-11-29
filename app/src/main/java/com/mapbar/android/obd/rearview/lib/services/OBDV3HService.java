package com.mapbar.android.obd.rearview.lib.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.VoiceReceiver;
import com.mapbar.android.obd.rearview.lib.config.Configs;
import com.mapbar.android.obd.rearview.lib.config.Constants;
import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.android.obd.rearview.lib.serialportsearch.SerialportConstants;
import com.mapbar.android.obd.rearview.lib.serialportsearch.SerialportFinderService;
import com.mapbar.android.obd.rearview.lib.serialportsearch.SerialportSPUtils;
import com.mapbar.android.obd.rearview.modules.external.ExternalManager;
import com.mapbar.android.obd.rearview.util.Utils;
import com.mapbar.obd.CarStatusData;
import com.mapbar.obd.Firmware;
import com.mapbar.obd.LocalCarModelInfoResult;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.Manager;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.RealTimeData;
import com.mapbar.obd.UserCar;
import com.mapbar.obd.UserCenter;
import com.mapbar.obd.UserCenterError;
import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.foundation.utils.SafeHandler;
import com.mapbar.obd.serial.comond.IOSecurityException;

import java.io.IOException;


/**
 * 与app同一时间只能有一个在操作obd盒子,并保证一直存在后台
 * 策略：app开启
 * Created by liuyy on 2016/5/26.
 */
public class OBDV3HService extends Service {
    public static final String OBDV3SERVICE_CLASS_NAME = "com.mapbar.android.obd.rearview.lib.services.OBDV3HService";
    /**
     * 精简版后台服务ACTION名称
     */
    public static final String ACTION_COMPACT_SERVICE = "com.mapbar.obd.LOGIN_SERVICE";
    /**
     * 是否自动重启后台服务, 缺省值: true
     */
    public static final String EXTRA_AUTO_RESTART = "autoRestart";
    /**
     * 是否等待通知重新连接, 缺省值: false<br />
     * 如果设置为true，即使当前为连接，或者尝试连接状态，都会被断开
     */
    public static final String EXTRA_WAIT_FOR_SIGNAL = "needWaitForSignal";
    /**
     * 是否需要尝试连接设备, 缺省值: false
     */

    public static final String EXTRA_NEED_CONNECT = "needConnect";
    /**
     * 需要延迟启动的时间, 缺省值: 0
     */
    public static final String EXTRA_DELAY = "delay";
    /**
     * 设置行程上传时使用的渠道
     */
    public static final String EXTRA_CHANNEL = "channel";
    public static final String TAG = "OBDV3HService";
    private static final int MSG_GET_CAR_STATUS_DATA = 1;
    private static final long INTERVAL_GET_CAR_STATUS = 3000;
    private static final int CONNECTION_STATE_DISCONNECTED = 0;
    private static final int CONNECTION_STATE_CONNECTING = 1;
    private static final int CONNECTION_STATE_CONNECTED = 2;
    private static String mObdChannel = Constants.COMAPCT_SERVICE_CHANNEL_NAME;
    private static boolean mNeedWaitForSignal = false;
    private static volatile int mCurrentStatus = CONNECTION_STATE_DISCONNECTED;
    private static boolean mNeedAutoRestart = true;
    private static boolean mNeedConnect = true;
    private static long mDelay = 0L;
    private static boolean isShowToast = false;
    public Manager.Listener sdkListener;
    public LocalCarModelInfoResult localCarModelInfoResult;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isShowToast)
                Toast.makeText(OBDV3HService.this, "关闭V3服务", Toast.LENGTH_SHORT).show();
            Log.e(LogTag.OBD, "whw OBDV3HService receiver stopservice");
            LogUtil.d(TAG, "## 关闭V3服务,onReceive action " + intent.getAction());
            stopSelf();
            //手动关闭行程,false:保留当前行程
            Manager.getInstance().stopTrip(false);
            MyApplication.getInstance().exitApplication(false);
        }
    };
    private Manager manager;
    private MyHandler mHandler = new MyHandler(this);
    private int times;
    private VoiceReceiver mVoiceReceiver;
    private ServiceConnection serialportFinderconn;
    private Intent startCommandIntent;
    private Messenger V3HMessenger = new Messenger(mHandler);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "## onCreate");
        //初始化语音广播接收器
        mVoiceReceiver = new VoiceReceiver();
        manager = Manager.getInstance();
        //捕捉异常注册
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(OBDV3HService.this, 0);

        sdkListener = new Manager.Listener() {

            @Override
            public void onEvent(int event, Object o) {
                //token失效处理走设备登陆
                boolean isTokenInvalid = tokenInvalid(event, o);
                if (isTokenInvalid) {
                    UserCenter.getInstance().DeviceLoginlogin(Utils.getImei(getApplication()));
                    return;
                }
                android.util.Log.e("uuuuuu", " 有回调+" + event);
                switch (event) {
                    case Manager.Event.queryCarSucc:
                        UserCar[] cars = (UserCar[]) o;
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> 查询远程车辆成功");
                        }
                        if (cars != null && cars.length > 0) {
                            times = 0;
                            if (TextUtils.isEmpty(cars[0].carGenerationId)) {
                                // 日志
                                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                                    Log.d(LogTag.OBD, " -->> 未注册，数据无效");
                                }
                                goApp();
                            } else {
                                // 日志
                                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                                    Log.d(LogTag.OBD, " -->> 已注册，数据有效");
                                    Log.d(LogTag.OBD, "carGenerationId -->> " + cars[0].carGenerationId);
                                }
                                queryLocalCarModelInfo(cars[0].carGenerationId);
                            }
                        } else {
                            // 日志
                            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                                Log.d(LogTag.OBD, " -->> 未注册，数据无效");
                            }

                            goApp();


                        }
                        break;
                    case Manager.Event.queryCarFailed:
                        if (times < 4) {
                            times++;
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Manager.getInstance().queryRemoteUserCar();
                                }
                            }, 200);
                        }
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> 查询远程车辆失败");
                        }
                        //打开App
                        goApp();
                        break;
                    case Manager.Event.DeviceloginSucc:
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> 设备登录成功");
                        }
                        login2();
                        break;
                    case Manager.Event.DeviceloginFailed:
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> 设备登录失败");
                        }
                        //延迟，设备登录
                        //延迟，设备登录
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                UserCenter.getInstance().DeviceLoginlogin(Utils.getImei(getApplication()));
                            }
                        }, 5 * 1000);
                        break;
                    case Manager.Event.queryRemoteCarModelInfoSucc:
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> 查询远程车型信息成功");
                        }

                        startServer();
                        break;
                    case Manager.Event.queryRemoteCarModelInfoFailed:
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> 查询远程车型信息失败");
                        }
                        break;
                    case Manager.Event.obdConnectSucc:
                        mCurrentStatus = CONNECTION_STATE_CONNECTED;

                        break;
                    case Manager.Event.obdConnectFailed:
                        mCurrentStatus = CONNECTION_STATE_DISCONNECTED;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openDevice();
                            }
                        }, 500);
                        break;
                    case Manager.Event.disconnectTimeout:
                    case Manager.Event.disconnectManually:
                    case Manager.Event.disconnectInfoIncomplete:
                        mCurrentStatus = CONNECTION_STATE_DISCONNECTED;
                        break;

                    case Manager.Event.dataUpdate:
                        RealTimeData data = (RealTimeData) o;
                        if (isShowToast)
                            Toast.makeText(OBDV3HService.this, "有数据", Toast.LENGTH_SHORT).show();
                        Log.v(LogTag.FRAMEWORK, "whw OBDV3HService RealtimeData speed:" + data.speed);
                        Log.v(LogTag.FRAMEWORK, "whw OBDV3HService RealtimeData gasConsumInLPerHour:" + data.gasConsumInLPerHour);
                        Log.v(LogTag.FRAMEWORK, "whw OBDV3HService RealtimeData driveCost:" + data.driveCost);
                        Log.v(LogTag.FRAMEWORK, "whw OBDV3HService RealtimeData rpm:" + data.rpm);

                        //发送广播，传出实时数据
                        ExternalManager.postRealTimeData(self(), data);
                        break;

                    case Manager.Event.dataCollectSucc:
                        //走自动注册
                        login1();
                        break;
                    case Manager.Event.dataCollectFailed:
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openDevice();
                            }
                        }, 500);
                        break;
                    case Manager.Event.obdCarStatusgetSucc: {
                        CarStatusData carStatusData = (CarStatusData) o;
                        //发送外部消息广播
                        ExternalManager.postCarStatus(self(), carStatusData);
                    }
                    break;
                }
            }
        };
        //
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.mapbar.obd.stopservice");
        registerReceiver(receiver, filter);
        IntentFilter voice_filter = new IntentFilter("mapbar.obd.intent.action.VOICE_CONTROL");
        registerReceiver(mVoiceReceiver, voice_filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startCommandIntent = intent;
        //设置串口
        String serialport = SerialportSPUtils.getSerialport(this);
        if (!TextUtils.isEmpty(serialport)) {
            //配置串口,初始化
            readyStartCommond(serialport);
            return onInitSueess();
        } else {
            configSerialport();
            return Service.START_STICKY;
        }
    }

    /**
     * 开启服务查找串口,通过handler接收
     */
    private void configSerialport() {
        Intent intent = new Intent(this, SerialportFinderService.class);
        bindService(intent, serialportFinderconn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                LogUtil.d(TAG, "绑定成功");
                Messenger messenger = new Messenger(iBinder);
                Message msg = Message.obtain();
                msg.what = SerialportConstants.CONNECTIONSUCCESS;
                msg.replyTo = V3HMessenger;
                try {
                    messenger.send(msg);
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

    /**
     * 配置串口,初始化
     *
     * @param serialport
     */
    private void readyStartCommond(String serialport) {
        ObdContext.configSerialport(serialport, SerialportConstants.BAUDRATE_DEFAULT, SerialportConstants.TIMEOUT_DEFAULT, SerialportConstants.IS_DEBUG_SERIALPORT);
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Configs.FILE_PATH;
        try {
            manager.init(this, sdkListener, sdPath, null);
        } catch (IOException | IOSecurityException e) {
            e.printStackTrace();
            LogUtil.d(TAG, "## V3服务 初始化串口异常" + e.getMessage());
        }
    }

    /**
     * 初始化成功之后的操作
     *
     * @return
     */
    private int onInitSueess() {
        LogUtil.d(TAG, "## onStartCommand");
        try {
            LogUtil.d(TAG, "## V3服务 onStartCommand");
            openDevice();
            if (isShowToast)
                Toast.makeText(OBDV3HService.this, "开启了V3服务", Toast.LENGTH_SHORT).show();

            // Reset the options to default.
            mNeedWaitForSignal = false;
            mNeedConnect = false;
            mNeedAutoRestart = true;
            mDelay = 0L;

            if (startCommandIntent != null) {
                mNeedAutoRestart = startCommandIntent.getBooleanExtra(EXTRA_AUTO_RESTART, true);
                mNeedWaitForSignal = startCommandIntent.getBooleanExtra(EXTRA_WAIT_FOR_SIGNAL,
                        false);
                mNeedConnect = startCommandIntent.getBooleanExtra(EXTRA_NEED_CONNECT, true);
                mDelay = startCommandIntent.getLongExtra(EXTRA_DELAY, 0L);
//            if (Config.DEBUG) {
//                mDebug = intent.getBooleanExtra(EXTRA_DEBUG, false);
//            }
            }

            if (mDelay > 0L) {
                try {
                    Thread.sleep(mDelay);
                } catch (InterruptedException e) {
//                if (Config.DEBUG) {
//                    e.printStackTrace();
//                }
                }
            }

            // If the service startup and need to downloadPermision in the background
            // Attempt to connect the device automatically.
            if (!mNeedWaitForSignal) {
//            if (Config.DEBUG) {
//                if (mDebug) {
//                    Logger.d(TAG, "In *DEBUG MODE*");
//                    Logger.d(TAG, "open demo device");
//                    Manager.create().openDemoDevice();
//                } else {
//                    if (mNeedConnect) {
//                        connectDevice();
//                    }
//                }
//            } else {
                if (mNeedConnect) {
                    mObdChannel = Constants.COMAPCT_SERVICE_CHANNEL_NAME;
                    if (startCommandIntent != null) {
                        mObdChannel = startCommandIntent.getStringExtra(EXTRA_CHANNEL);
                        if (mObdChannel == null) {
                            mObdChannel = Constants.COMAPCT_SERVICE_CHANNEL_NAME;
                        }
                    }
//                connectDevice();
                }
//            }
            } else {
                // If need wait for signal to connect the device
                // check whether the status is connecting now, if true, close device
                // immediately.
                // Disconnect and hand over the device to other applications.
                if (mCurrentStatus != CONNECTION_STATE_DISCONNECTED) {
                    Manager.getInstance().closeDevice();
                }
            }

            // Need restart automatically.
            if (!mNeedAutoRestart) {
                return Service.START_NOT_STICKY;
            } else {
                return Service.START_STICKY;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            LogUtil.d(TAG, "## V3服务 error: " + ex.getMessage());
            return Service.START_NOT_STICKY;
        }
    }

    private void connectDevice() {
        // If current status is disconnected, attempt to connect the device
        // immediately.
        if (mCurrentStatus == CONNECTION_STATE_DISCONNECTED) {
//            if (!DeviceService.create().isAdapterEnabled()) {
//                DeviceService.create().enableAdapter(true);
//                mConnectHandler.sendEmptyMessageDelayed(EVENT_WAIT_FOR_ADAPTER,
//                        CONNECT_DELAY);
//            } else {
            doConnect();
//            }
        }
    }

    private void doConnect() {
        openDevice();
        /*
        mCandidateDeviceInfo = Manager.create().getCandidateDeviceInfo();
        if (mCandidateDeviceInfo != null && !TextUtils.isEmpty(mCandidateDeviceInfo.mac)) {
            // Attempt to connect the last device.
            mCurrentStatus = CONNECTION_STATE_CONNECTING;
            if (!Manager.create().openDevice(mCandidateDeviceInfo.mac)) {
                mConnectHandler.sendEmptyMessageDelayed(EVENT_DELAY_TO_CONNECT,
                        CONNECT_DELAY);
            } else {
                boolean b = Manager.create().setExtraTripInfo(new ExtraTripInfo(COLLECT_TYPE, mObdChannel));
            }
        } else {
            resetAdapterStatus();
        }
        */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "## V3服务 onDestroy");
        if (serialportFinderconn != null) {
            unbindService(serialportFinderconn);
        }
        unregisterReceiver(receiver);
        unregisterReceiver(mVoiceReceiver);

        stopLoopGetCarStatus();
        MyApplication.getInstance().exitApplication(false);

    }

    /**
     * 当需要(微信注册)弹出二维码时，启动应用
     */
    private void goApp() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent startIntent = new Intent();
                startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//必须加上
                ComponentName cName = new ComponentName("com.mapbar.android.obd.rearview", OBDV3SERVICE_CLASS_NAME);
                startIntent.setComponent(cName);
                startActivity(startIntent);
            }
        }, 2 * 60 * 1000);

    }

    /**
     * 自动登录、设备登录
     */
    private void login1() {
        android.util.Log.e("uuuuuuuu", "登录开始");

        if (UserCenter.getInstance().loginAutomatically()) {

            android.util.Log.e("uuuuuuuu", "自动登录成功");
            login2();
        } else {
            android.util.Log.e("uuuuuuuu", "自动登录失败");
            UserCenter.getInstance().DeviceLoginlogin(Utils.getImei(getApplication()));

        }
    }

    /**
     * 查询本地车辆信息
     */
    private void login2() {
        LocalUserCarResult result = Manager.getInstance().queryLocalUserCar();
        if (result != null) {
            UserCar car = result.userCars == null || result.userCars.length == 0 ? null : result.userCars[0];
            if (car != null && !TextUtils.isEmpty(car.carGenerationId)) {

                android.util.Log.e("uuuuuuuu", "查询本地车辆成功");
                //启动业务
                queryLocalCarModelInfo(car.carGenerationId);
                return;
            }
        }
        android.util.Log.e("uuuuuuuu", "查询本地车辆失败");
        //查询远程车辆信息
        Manager.getInstance().queryRemoteUserCar();

    }

    /**
     * 从本地缓存中查询单个车型参数
     *
     * @param carGenerationId 指定车型的车辆年款id.
     */
    private void queryLocalCarModelInfo(String carGenerationId) {
        localCarModelInfoResult = Manager.getInstance().queryLocalCarModelInfo(carGenerationId, 1);
        if (localCarModelInfoResult.errCode == LocalCarModelInfoResult.Error.none) {
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, " -->> 本地查询车型信息成功");
            }
            startServer();
        } else {
            //日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, " -->> 本地查询车型信息失败");
            }
            //查询车辆基本信息失败,开始远程查询车型信息
            Manager.getInstance().queryRemoteCarModelInfo(carGenerationId, 1);
        }
    }

    private void startServer() {
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, " -->> 启动业务");
        }
        Manager.getInstance().startReadThread();
        startLoopGetCarStatus();
    }

    /**
     * 设备连接
     */
    public void openDevice() {
        Manager.getInstance().openDevice(Utils.getImei(getApplication()));
    }

    /**
     * 检测token是否失效
     *
     * @param event
     * @param o
     * @return true为失效 false为没有失效
     */
    private boolean tokenInvalid(int event, Object o) {
        if (o != null && o instanceof Firmware.EventData) {
            Firmware.EventData eventData = (Firmware.EventData) o;
            if (Configs.TOKEN_INVALID == eventData.getRspCode()) {
                return true;
            }
        }
        if (o != null && o instanceof UserCenterError) {
            UserCenterError erro = (UserCenterError) o;
            if (erro.errorType == 2 && erro.errorCode == 1401) {
                return true;
            }
        }
        if (event == Manager.Event.queryCarFailed) {
            int errorCode = (int) o;
            if (errorCode == Manager.CarInfoResponseErr.unauthorized || errorCode == Manager.CarInfoResponseErr.notLogined) {
                return true;
            }
        }
        if (event == Manager.Event.commitLogFailed) {
            return true;
        }
        return false;
    }

    private OBDV3HService self() {
        return this;
    }

    private void startLoopGetCarStatus() {
        if (mHandler == null) return;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_GET_CAR_STATUS_DATA), INTERVAL_GET_CAR_STATUS);
    }

    private void stopLoopGetCarStatus() {
        if (mHandler == null) return;
        mHandler.removeMessages(MSG_GET_CAR_STATUS_DATA);
    }

    private static class MyHandler extends SafeHandler<OBDV3HService> {
        private static final String CMD_GET_STATUS_DATA = "AT@STG0001\r";
        public MyHandler(OBDV3HService object) {
            super(object);
        }

        @Override
        public void handleMessage(Message msg) {
            if (getInnerObject() == null)
                return;
            if (msg.what == MSG_GET_CAR_STATUS_DATA) {//获得车辆实施数据
                Manager.getInstance().sendCustomCommandRequest(CMD_GET_STATUS_DATA);
                removeMessages(MSG_GET_CAR_STATUS_DATA);
                sendMessageDelayed(obtainMessage(MSG_GET_CAR_STATUS_DATA), INTERVAL_GET_CAR_STATUS);
            } else if (msg.what == SerialportConstants.SERIALPORT_FIND_SUCCESS) {
                Bundle bundle = msg.getData();
                String serialport = bundle.getString("serialport");
                LogUtil.d(TAG, "接收过来的串口号:::::" + serialport);
                getInnerObject().readyStartCommond(serialport);
                getInnerObject().onInitSueess();
            } else if (msg.what == SerialportConstants.SERIALPORT_FIND_FAILURE) {
                if (isShowToast)
                    Toast.makeText(getInnerObject(), "查找串口失败", Toast.LENGTH_LONG).show();
                LogUtil.d(TAG,"查找串口失败");
            }
        }
    }

}