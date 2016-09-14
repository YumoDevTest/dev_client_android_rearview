package com.mapbar.android.obd.rearview.framework.control;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.lib.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.modules.external.ExternalManager;
import com.mapbar.android.obd.rearview.obd.Constants;
import com.mapbar.android.obd.rearview.obd.util.SafeHandler;
import com.mapbar.obd.CarStatusData;
import com.mapbar.obd.Config;
import com.mapbar.obd.Firmware;
import com.mapbar.obd.LocalCarModelInfoResult;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.Manager;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.RealTimeData;
import com.mapbar.obd.UserCar;
import com.mapbar.obd.UserCenter;
import com.mapbar.obd.UserCenterError;

import java.io.IOException;


/**
 * 与app同一时间只能有一个在操作obd盒子,并保证一直存在后台
 * 策略：app开启
 * Created by liuyy on 2016/5/26.
 */
public class OBDV3HService extends Service {
    private static final int MSG_GET_CAR_STATUS_DATA = 1;
    private static final long INTERVAL_GET_CAR_STATUS = 3000;

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
    private static final int CONNECTION_STATE_DISCONNECTED = 0;
    private static final int CONNECTION_STATE_CONNECTING = 1;
    private static final int CONNECTION_STATE_CONNECTED = 2;
    private static String mObdChannel = Constants.COMAPCT_SERVICE_CHANNEL_NAME;
    private static boolean mNeedWaitForSignal = false;
    private static volatile int mCurrentStatus = CONNECTION_STATE_DISCONNECTED;
    private static boolean mNeedAutoRestart = true;
    private static boolean mNeedConnect = true;
    private static long mDelay = 0L;
    public Manager.Listener sdkListener;
    public LocalCarModelInfoResult localCarModelInfoResult;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(OBDV3HService.this, "关闭V3服务", Toast.LENGTH_SHORT).show();
            Log.e(LogTag.OBD, "whw OBDV3HService receiver stopservice");
            android.util.Log.d("OBDV3HService", "## 关闭V3服务,onReceive action " + intent.getAction());
            stopSelf();
            Manager.getInstance().stopTrip(true);
            Manager.getInstance().cleanup();
            //umeng统计，在杀死进程是需要调用用来保存统计数据。
            MobclickAgentEx.onKillProcess(OBDV3HService.this);
            System.exit(0);
        }
    };
    private Manager manager;
    private Handler mHandler;
    private int times;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        manager = Manager.getInstance();
        //捕捉异常注册
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplication(), 2);
//        Log.e(LogTag.OBD, object.toString());

        mHandler = new MyHandler(this);
        ObdContext.setSerialPortPath(Constants.SERIALPORT_PATH);
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
                        if (Config.DEBUG)
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
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Configs.FILE_PATH;
        try {
            manager.init(this, sdkListener, sdPath, null);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("### 初始化串口异常");
        }
        //
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.mapbar.obd.stopservice");
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        android.util.Log.d("OBDV3HService", "## 开启了V3服务 onStartCommand");
        openDevice();
        Toast.makeText(OBDV3HService.this, "开启了V3服务", Toast.LENGTH_SHORT).show();

        // Reset the options to default.
        mNeedWaitForSignal = false;
        mNeedConnect = false;
        mNeedAutoRestart = true;
        mDelay = 0L;

        if (intent != null) {
            mNeedAutoRestart = intent.getBooleanExtra(EXTRA_AUTO_RESTART, true);
            mNeedWaitForSignal = intent.getBooleanExtra(EXTRA_WAIT_FOR_SIGNAL,
                    false);
            mNeedConnect = intent.getBooleanExtra(EXTRA_NEED_CONNECT, true);
            mDelay = intent.getLongExtra(EXTRA_DELAY, 0L);
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
//                    Manager.getInstance().openDemoDevice();
//                } else {
//                    if (mNeedConnect) {
//                        connectDevice();
//                    }
//                }
//            } else {
            if (mNeedConnect) {
                mObdChannel = Constants.COMAPCT_SERVICE_CHANNEL_NAME;
                if (intent != null) {
                    mObdChannel = intent.getStringExtra(EXTRA_CHANNEL);
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

    }

    private void connectDevice() {
        // If current status is disconnected, attempt to connect the device
        // immediately.
        if (mCurrentStatus == CONNECTION_STATE_DISCONNECTED) {
//            if (!DeviceService.getInstance().isAdapterEnabled()) {
//                DeviceService.getInstance().enableAdapter(true);
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
        mCandidateDeviceInfo = Manager.getInstance().getCandidateDeviceInfo();
        if (mCandidateDeviceInfo != null && !TextUtils.isEmpty(mCandidateDeviceInfo.mac)) {
            // Attempt to connect the last device.
            mCurrentStatus = CONNECTION_STATE_CONNECTING;
            if (!Manager.getInstance().openDevice(mCandidateDeviceInfo.mac)) {
                mConnectHandler.sendEmptyMessageDelayed(EVENT_DELAY_TO_CONNECT,
                        CONNECT_DELAY);
            } else {
                boolean b = Manager.getInstance().setExtraTripInfo(new ExtraTripInfo(COLLECT_TYPE, mObdChannel));
            }
        } else {
            resetAdapterStatus();
        }
        */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLoopGetCarStatus();
        Manager.getInstance().cleanup();

        //umeng统计，在杀死进程是需要调用用来保存统计数据。
        MobclickAgentEx.onKillProcess(this);
        System.exit(0);
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
                ComponentName cName = new ComponentName("com.mapbar.android.obd.rearview", "com.mapbar.android.obd.rearview.obd.DeclareActivity");
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
            }
        }
    }
}