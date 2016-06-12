package com.mapbar.android.obd.rearview.framework.manager;

import android.os.Handler;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.bean.QRInfo;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.obd.LocalCarModelInfoResult;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCar;
import com.mapbar.obd.UserCenter;


/**
 * Created by tianff on 2016/6/2.
 */
public class UserCenterManager extends OBDManager {

    public static final int EVENT_OBD_USER_LOGIN_SUCC = 0xF40001;//登录成功
    public static final int EVENT_OBD_USER_LOGIN_FAILED = 0xF40002;//登录失败,需要注册,弹出二维码
    public static final int EVENT_OBD_USER_REGISTER_SUCC = 0xF40003;//微信注册成功,关闭二维码
    public static final int EVENT_OBD_USER_REGISTER_FAILED = 0xF4004;

    private Handler mHandler = new Handler();
    private boolean isPush = true;
    private String reg_info = "请扫描填写信息，以获得更多汽车智能化功能\n如：远程定位、防盗提醒、远程查看车辆状态等";
    private String scan_succ = "扫描成功\\n请等待填写完成";
    private String reg_succ = "您已通过手机\\n成功完善爱车信息";
    private LocalCarModelInfoResult localCarModelInfoResult;
//    private int loginType = 0;//登录类型


    public UserCenterManager() {
        super();
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
     * 爱心推收到推送调用
     *
     * @param type
     * @param state
     * @param userId
     * @param token
     */
    public void setPushData(int type, int state, String userId, String token) {
        // 日志
        if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
            Log.d(LogTag.PUSH, " -->> 推送userManager收到");
        }
        switch (type) {
            case 0:
                if (state == 1)
                    showRegQr(scan_succ);
                break;
            case 1:
                if (state == 1 || state == 3) { //注册成功
                    showRegQr(reg_succ);
                    isPush = false;//推送成功
                    // 更新本地用户信息
                    if (userId != null && token != null) {
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, "userId -->> " + userId + " token--->" + token);
                            Log.d(LogTag.OBD, "当前userId -->>" + UserCenter.getInstance().getCurrentIdAndType().userId);
                            Log.d(LogTag.OBD, "当前token -->>" + UserCenter.getInstance().getCurrentUserToken());
                            Log.d(LogTag.OBD, "当前imei -->>" + Utils.getImei());
                        }
                        boolean isUpdata = UserCenter.getInstance().UpdateUserInfoByRemoteLogin(userId, null, token, "zs");
                        if (isUpdata) {
                            //远程查询车辆信息
                            Manager.getInstance().queryRemoteUserCar();
                        } else {
                            //显示二维码
                            showRegQr(reg_info);
                            // 日志
                            if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                                Log.d(LogTag.PUSH, " -->>更新本地用户信息失败 ");
                            }
                        }
                    }

                } else if (state == 2) {
                    showRegQr(reg_info);
                }

        }
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
        android.util.Log.e("uuuuuuuu", "App回调" + event);
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
                        showRegQr(reg_info);
                        outTime();
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

                    showRegQr(reg_info);
                    outTime();

                }
                break;
            case Manager.Event.queryCarFailed:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> 查询远程车辆失败");
                }
                //显示二维码
                showRegQr(reg_info);
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
                //延迟，设备登录
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UserCenter.getInstance().DeviceLoginlogin(Utils.getImei());
                    }
                }, 5 * 1000);
                break;
            case Manager.Event.queryRemoteCarModelInfoSucc:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> 查询远程车型信息成功");
                }
                //关闭二维码
                baseObdListener.onEvent(EVENT_OBD_USER_REGISTER_SUCC, null);
                startServer();
                break;
            case Manager.Event.queryRemoteCarModelInfoFailed:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> 查询远程车型信息失败");
                }
                break;
        }

    }


    /**
     * 自动登录、设备登录
     */
    private void login1() {
        android.util.Log.e("uuuuuuuu", "登录开始");
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
        baseObdListener.onEvent(EVENT_OBD_USER_LOGIN_SUCC, null);
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, " -->> 启动业务");
        }
        Manager.getInstance().openDevice(Utils.getImei());
    }


    /**
     * 弹出二维码,此方法可以保证push_token不为空
     */
    private void showRegQr(final String content) {
        QRInfo qrInfo = null;
        if (AixintuiConfigs.push_token != null) {
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, " -->> 弹出二维码");
            }
            StringBuilder sb = new StringBuilder();
            sb.append(Configs.URL_REG_INFO).append("imei=").append(Utils.getImei()).append("&");
            sb.append("pushToken=").append(AixintuiConfigs.push_token).append("&");
            sb.append("token=").append(UserCenter.getInstance().getCurrentUserToken());

//        String url = Configs.URL_REG1 + "&redirect_uri=" + Uri.encode(sb.toString()) + Configs.URL_REG2;
            String url = sb.toString();
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, "url -->> " + url);
            }
            if (qrInfo == null) {
                qrInfo = new QRInfo();
            }
            qrInfo.setUrl(url);
            qrInfo.setContent(content);
            baseObdListener.onEvent(EVENT_OBD_USER_LOGIN_FAILED, qrInfo);
        } else {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showRegQr(content);
                }
            }, 200);

        }
    }


    /**
     * 弹出二维码后超时，重新设备登录
     */
    private void outTime() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPush) {//推送失败
                    login1();//设备登录
                }
            }
        }, 1000 * 60);


    }


}

