package com.mapbar.android.obd.rearview.framework.manager;

import android.os.Handler;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.common.LayoutUtils;
import com.mapbar.android.obd.rearview.framework.common.QRUtils;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.control.SDKListenerManager;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiPushManager;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCar;
import com.mapbar.obd.UserCenter;

/**
 * Created by tianff on 2016/6/2.
 */
public class UserCenterManager extends OBDManager {
    private Handler mHandler = new Handler();
    private boolean isPush = true;
    private boolean isOutTime = true;

    public UserCenterManager() {
        sdkListener = new SDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                onSDKEvent(event, o);
            }
        };
        SDKListenerManager.getInstance().setSdkListener(sdkListener);

        //监听推送消息
        AixintuiPushManager.getInstance().setPushCallBack(new AixintuiPushManager.PushCallBack() {
            @Override
            public void pushData(int type, int state, String userId, String token) {
                // 日志
                if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                    Log.d(LogTag.PUSH, " -->> MainPag回调");
                }
                switch (type) {
                    case 0:
                        if (state == 1)
                            showRegQr(Global.getAppContext().getResources().getString(R.string.scan_succ));
                        break;
                    case 1:
                        if (state == 1 || state == 3) { //注册成功

                            showRegQr(Global.getAppContext().getResources().getString(R.string.reg_succ));
                            isPush = false;//推送成功
                            // 更新本地用户信息
                            if (userId != null && token != null) {
                                // 日志
                                if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                                    Log.d(LogTag.PUSH, "userId -->> " + userId + " token--->" + token);
                                    Log.d(LogTag.PUSH, "当前userId -->>" + UserCenter.getInstance().getCurrentIdAndType().userId);
                                    Log.d(LogTag.PUSH, "当前token -->>" + UserCenter.getInstance().getCurrentUserToken());
                                    Log.d(LogTag.PUSH, "当前imei -->>" + Utils.getImei());
                                }
                                boolean isUpdata = UserCenter.getInstance().UpdateUserInfoByRemoteLogin(userId, null, token, "zs");
                                if (isUpdata) {
                                    //远程查询车辆信息
                                    Manager.getInstance().queryRemoteUserCar();
                                } else {
                                    //显示二维码
                                    showRegQr(Global.getAppContext().getResources().getString(R.string.reg_info));
                                    // 日志
                                    if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                                        Log.d(LogTag.PUSH, " -->>更新本地用户信息失败 ");
                                    }
                                }
                            }

                        } else if (state == 2) {
                            showRegQr(Global.getAppContext().getResources().getString(R.string.reg_info));
                        }

                }
            }
        });

    }

    /**
     * 获取UserCenterManager单例
     *
     * @return UserCenterManager单例
     */
    public static UserCenterManager getInstance() {
        return (UserCenterManager) OBDManager.getInstance(UserCenterManager.class);
    }

    /**
     * 登录流程启动
     */
    public void login() {
        //自动登录和设备登录
        login1();
    }

    @Override
    public void onSDKEvent(int event, Object o) {
        switch (event) {
            case Manager.Event.queryCarSucc:
                UserCar[] cars = (UserCar[]) o;
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> 查询远程车辆成功");
                }
                if (cars != null && cars.length > 0) {
                    if (TextUtils.isEmpty(cars[0].carGenerationId)) {
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> 未注册，数据无效");
                        }
                        showRegQr(Global.getAppContext().getResources().getString(R.string.reg_info));
                        outTime();
                    } else {
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> 已注册，数据有效");
                            Log.d(LogTag.OBD, "carGenerationId -->> " + cars[0].carGenerationId);
                        }
                        //关闭二维码
                        LayoutUtils.disQrPop();
                        startServer();
                    }
                } else {
                    // 日志
                    if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                        Log.d(LogTag.OBD, " -->> 未注册，数据无效");
                    }
                    showRegQr(Global.getAppContext().getResources().getString(R.string.reg_info));
                    outTime();
                }
                break;
            case Manager.Event.queryCarFailed:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> 查询远程车辆失败");
                }
                //显示二维码
                showRegQr(Global.getAppContext().getResources().getString(R.string.reg_info));
                outTime();
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
                //延迟，
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UserCenter.getInstance().DeviceLoginlogin(Utils.getImei());
                    }
                }, 5 * 1000);
                break;
        }
        super.onSDKEvent(event, o);
    }


    /**
     * 自动登录、设备登录
     */
    private void login1() {
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, " -->> 登录开始");
        }
//        UserCenter.getInstance().login("18610857365", "111111");
        if (UserCenter.getInstance().loginAutomatically()) {
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, " -->> 自动登录成功");
            }
            login2();
        } else {
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, " -->> 自动登录失败");
            }
            UserCenter.getInstance().DeviceLoginlogin(Utils.getImei());

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
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> 查询本地车辆成功");
                }
                //启动业务
                startServer();
                return;
            }
        }
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, " -->> 查询本地车辆失败");
        }
        //查询远程车辆信息
        Manager.getInstance().queryRemoteUserCar();
    }

    public void startServer() {
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, " -->> 启动业务");
        }
        Manager.getInstance().openDevice(Utils.getImei());
    }


    /**
     * 弹出二维码
     */
    private void showRegQr(String info) {
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, " -->>二维码应该弹出");
        }
        if (AixintuiConfigs.push_token != null) {
            QRUtils.showRegQr(info);
        } else {
            //注册爱心推
//            PushSdkApi.register(Global.getAppContext(), AixintuiConfigs.AIXINTUI_APPKEY, Utils.getChannel(Global.getAppContext()), Utils.getVersion(Global.getAppContext()) + "");

        }
    }


    public void outTime() {
        if (isOutTime) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isPush) {//推送失败
                        isOutTime = false;
                        login1();//设备登录
                    }
                }
            }, 1000 * 60);
        }

    }

}

