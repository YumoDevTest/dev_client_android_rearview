package com.mapbar.android.obd.rearview.framework.control;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.obd.LocalCarModelInfoResult;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCar;
import com.mapbar.obd.UserCenter;

import aidl.IMyAidlInterface;


/**
 * Created by liuyy on 2016/5/26.
 */
public class OBDV3HService extends Service {
    public SDKListenerManager.SDKListener sdkListener;
    public LocalCarModelInfoResult localCarModelInfoResult;
    public Handler mHandler;

    public Binder binder = new IMyAidlInterface.Stub() {

        @Override
        public void startExam() throws RemoteException {
//            PhysicalManager.getInstance().startExam();
        }

        @Override
        public String changeData(String data) throws RemoteException {
            Intent intent = new Intent();
            intent.setAction("myReceriver");
            intent.putExtra("myReceriver", "广播");
            sendBroadcast(intent);
            return data + "------>你好";
        }


    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        SDKListenerManager.getInstance().init();
        sdkListener = new SDKListenerManager.SDKListener() {

            @Override
            public void onEvent(int event, Object o) {
                Log.e("uuuuuuuu", "有回调+" + event);
                switch (event) {
                    case Manager.Event.queryCarSucc:
                        UserCar[] cars = (UserCar[]) o;
                        // 日志
                        if (com.mapbar.android.obd.rearview.framework.log.Log.isLoggable(LogTag.OBD, com.mapbar.android.obd.rearview.framework.log.Log.DEBUG)) {
                            com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, " -->> 查询远程车辆成功");
                        }
                        if (cars != null && cars.length > 0) {
                            if (TextUtils.isEmpty(cars[0].carGenerationId)) {
                                // 日志
                                if (com.mapbar.android.obd.rearview.framework.log.Log.isLoggable(LogTag.OBD, com.mapbar.android.obd.rearview.framework.log.Log.DEBUG)) {
                                    com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, " -->> 未注册，数据无效");
                                }
                                goApp();
                            } else {
                                // 日志
                                if (com.mapbar.android.obd.rearview.framework.log.Log.isLoggable(LogTag.OBD, com.mapbar.android.obd.rearview.framework.log.Log.DEBUG)) {
                                    com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, " -->> 已注册，数据有效");
                                    com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, "carGenerationId -->> " + cars[0].carGenerationId);
                                }
                                queryLocalCarModelInfo(cars[0].carGenerationId);
                            }
                        } else {
                            // 日志
                            if (com.mapbar.android.obd.rearview.framework.log.Log.isLoggable(LogTag.OBD, com.mapbar.android.obd.rearview.framework.log.Log.DEBUG)) {
                                com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, " -->> 未注册，数据无效");
                            }

                            goApp();


                        }
                        break;
                    case Manager.Event.queryCarFailed:
                        // 日志
                        if (com.mapbar.android.obd.rearview.framework.log.Log.isLoggable(LogTag.OBD, com.mapbar.android.obd.rearview.framework.log.Log.DEBUG)) {
                            com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, " -->> 查询远程车辆失败");
                        }
                        //打开App
                        goApp();
                        break;
                    case Manager.Event.DeviceloginSucc:
                        // 日志
                        if (com.mapbar.android.obd.rearview.framework.log.Log.isLoggable(LogTag.OBD, com.mapbar.android.obd.rearview.framework.log.Log.DEBUG)) {
                            com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, " -->> 设备登录成功");
                        }
                        login2();
                        break;
                    case Manager.Event.DeviceloginFailed:
                        // 日志
                        if (com.mapbar.android.obd.rearview.framework.log.Log.isLoggable(LogTag.OBD, com.mapbar.android.obd.rearview.framework.log.Log.DEBUG)) {
                            com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, " -->> 设备登录失败");
                        }
                        //延迟，设备登录
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                UserCenter.getInstance().DeviceLoginlogin(Utils.getImei(Global.getAppContext()));
                            }
                        }, 5 * 1000);
                        break;
                    case Manager.Event.queryRemoteCarModelInfoSucc:
                        // 日志
                        if (com.mapbar.android.obd.rearview.framework.log.Log.isLoggable(LogTag.OBD, com.mapbar.android.obd.rearview.framework.log.Log.DEBUG)) {
                            com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, " -->> 查询远程车型信息成功");
                        }

                        startServer();
                        break;
                    case Manager.Event.queryRemoteCarModelInfoFailed:
                        // 日志
                        if (com.mapbar.android.obd.rearview.framework.log.Log.isLoggable(LogTag.OBD, com.mapbar.android.obd.rearview.framework.log.Log.DEBUG)) {
                            com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, " -->> 查询远程车型信息失败");
                        }
                        break;
                }
            }
        };
        SDKListenerManager.getInstance().setSdkListener(sdkListener);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //登录
        login1();
        Toast.makeText(this, "开启了V3服务", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * 当需要(微信注册)弹出二维码时，启动应用
     */
    private void goApp() {
        Log.e("uuuuuuuu", "跳转");
        Intent startIntent = new Intent();
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//必须加上
        ComponentName cName = new ComponentName("com.mapbar.android.obd.rearview", "com.mapbar.android.obd.rearview.obd.MainActivity");
        startIntent.setComponent(cName);
        startActivity(startIntent);
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
            UserCenter.getInstance().DeviceLoginlogin(Utils.getImei(Global.getAppContext()));

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
            if (com.mapbar.android.obd.rearview.framework.log.Log.isLoggable(LogTag.OBD, com.mapbar.android.obd.rearview.framework.log.Log.DEBUG)) {
                com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, " -->> 本地查询车型信息成功");
            }
            startServer();
        } else {
            //日志
            if (com.mapbar.android.obd.rearview.framework.log.Log.isLoggable(LogTag.OBD, com.mapbar.android.obd.rearview.framework.log.Log.DEBUG)) {
                com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, " -->> 本地查询车型信息失败");
            }
            //查询车辆基本信息失败,开始远程查询车型信息
            Manager.getInstance().queryRemoteCarModelInfo("52d3e9d40a36483d2ceecb10", 1);
        }
    }

    private void startServer() {
        // 日志
        if (com.mapbar.android.obd.rearview.framework.log.Log.isLoggable(LogTag.OBD, com.mapbar.android.obd.rearview.framework.log.Log.DEBUG)) {
            com.mapbar.android.obd.rearview.framework.log.Log.d(LogTag.OBD, " -->> 启动业务");
        }
        Manager.getInstance().openDevice(Utils.getImei(Global.getAppContext()));
    }


}