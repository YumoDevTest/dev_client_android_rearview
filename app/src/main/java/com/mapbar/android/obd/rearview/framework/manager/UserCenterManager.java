package com.mapbar.android.obd.rearview.framework.manager;

import android.os.Handler;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.bean.QRInfo;
import com.mapbar.android.obd.rearview.framework.common.LayoutUtils;
import com.mapbar.android.obd.rearview.framework.common.StringUtil;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.umeng.UmengConfigs;
import com.mapbar.obd.Config;
import com.mapbar.obd.LocalCarModelInfoResult;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCar;
import com.mapbar.obd.UserCenter;
import com.mapbar.obd.UserCenterError;


/**
 * 用户登录中心管理者
 */
public class UserCenterManager extends OBDManager {
    /**
     * 默认密码
     */
    private final String DEFAULT_PASSWORD = Config.USE_INTERNAL_HOST ? "654321" : "111qqq,,,";
    private Handler mHandler = new Handler();
    private boolean isPush = true;
    private LocalCarModelInfoResult localCarModelInfoResult;
    private QRInfo qrInfo = null;
    /**
     * 用户名
     */
    private String account;
    private UserCar userCar;
    /**
     * 最大远程查询车辆信息失败次数
     */
    private int times;
    /**
     * 设备是否连接成功
     */
    private boolean isDeviceConnect = false;
    /**
     * 数据是否准备成功
     */
    private boolean isDataPrepare = false;


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
        if (!isPush) {
            return;
        }
        // 日志
        if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
            Log.d(LogTag.PUSH, " -->> 推送userManager收到");
        }
        switch (type) {
            case 0:
                if (state == 1) {
                    //TODO 登录
                    showRegQr(scan_succ);
                }
                break;
            case 1:
                if (state == 1 || state == 3) { //注册成功
                    showRegQr(reg_succ);
                    // 更新本地用户信息
                    if (userId != null && token != null) {
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, "userId -->> " + userId + " token--->" + token);
                            Log.d(LogTag.OBD, "当前userId -->>" + UserCenter.getInstance().getCurrentIdAndType().userId);
                            Log.d(LogTag.OBD, "当前token -->>" + UserCenter.getInstance().getCurrentUserToken());
                            Log.d(LogTag.OBD, "当前imei -->>" + Utils.getImei(MainActivity.getInstance()));
                        }

                        updateUserInfoByRemoteLogin(userId, null, token, "zs");
                    }

                } else if (state == 2) {
                    //友盟注册失败统计
                    MobclickAgentEx.onEvent(UmengConfigs.REGISTR_FAILED);
                    //微信注册失败时清除本地token，从新走设备注册
                    UserCenter.getInstance().clearCurrentUserToken();
                    login();
                }
                break;
        }
    }

    /**
     * 登录流程启动
     */
    public void login() {
        sdkListener.setActive(true);
        //连接设备
        openDevice();

    }

    /**
     * 更新本地用户信息
     *
     * @param userId
     * @param p1
     * @param token
     * @param p2
     * @return
     */
    public void updateUserInfoByRemoteLogin(String userId, String p1, String token, String p2) {
        boolean isUpdata = UserCenter.getInstance().UpdateUserInfoByRemoteLogin(userId, null, token, "zs");
        if (isUpdata) {
            //远程查询车辆信息
            Manager.getInstance().queryRemoteUserCar();
        } else {
            UserCenter.getInstance().clearCurrentUserToken();
            login();
            showRegQr(reg_info);
            // 日志
            if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                Log.d(LogTag.PUSH, " -->>更新本地用户信息失败 ");
            }

        }
    }

    @Override
    public void onSDKEvent(int event, Object o) {
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, "event---->" + event + "");
        }
        switch (event) {
            case Manager.Event.queryCarSucc:
                UserCar[] cars = (UserCar[]) o;
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> -->> 查询远程车辆成功");
                }
                if (cars != null && cars.length > 0) {
                    times = 0;
                    userCar = cars[0];
                    if (TextUtils.isEmpty(cars[0].carGenerationId)) {
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> -->> 未注册，数据无效");
                        }
                        android.util.Log.i("uuuuuuuu", "未注册，数据无效1");
                        if (!Config.MAPBAR_AIMI) {//非艾米版本
                            //显示二维码
                            showRegQr(reg_info);
                            outTime();
                        } else {//艾米版本，通知填写用户信息
                            baseObdListener.onEvent(EVENT_OBD_AIMI_SET_USER_DATA, null);
                        }
                    } else {
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> -->> 已注册，数据有效");
                            Log.d(LogTag.OBD, "carGenerationId -->> " + cars[0].carGenerationId);
                        }
                        queryLocalCarModelInfo(cars[0].carGenerationId);

                    }
                } else {

                    // 日志
                    if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                        Log.d(LogTag.OBD, " -->> -->> 未注册，数据无效");
                    }

                    if (!Config.MAPBAR_AIMI) {//非艾米版本
                        //显示二维码
                        showRegQr(reg_info);
                        outTime();
                    } else {//艾米版本，通知填写用户信息
                        baseObdListener.onEvent(EVENT_OBD_AIMI_SET_USER_DATA, null);
                    }

                }
                break;
            case Manager.Event.queryCarFailed:
                Integer errorCode = (Integer) o;
                if (times < 4) {
                    times++;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Manager.getInstance().queryRemoteUserCar();
                        }
                    }, 200);
                } else {
                    if (!Config.MAPBAR_AIMI) {//非艾米版本
                        //显示二维码
                        showRegQr(reg_info);
                        outTime();
                    } else {
                        baseObdListener.onEvent(23, errorCode);
                    }
                }

                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, "whw -->> -->> 查询远程车辆失败");
                }
                break;
            case Manager.Event.DeviceloginSucc:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> 设备登录成功");
                }
                LayoutUtils.disHud();
                login2();
                break;
            case Manager.Event.DeviceloginFailed:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> 设备登录失败");
                }
                LayoutUtils.disHud();
                LayoutUtils.showHud(MainActivity.getInstance(), "网络问题，无法连接");
                //延迟，设备登录
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UserCenter.getInstance().DeviceLoginlogin(Utils.getImei(MainActivity.getInstance()));
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
                String s = (String) o;
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> 查询远程车型信息失败" + s);
                }
                break;
            case Manager.Event.registerSucc:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> aim注册成功 ");
                }
                aimiLogin(account);
                break;
            case Manager.Event.registerFailed:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> aimi注册失败");
                }
                UserCenterError userCenterError = (UserCenterError) o;
                android.util.Log.i("uuuuuuuu", "注册失败 " + userCenterError.toString());
                if (userCenterError.errorCode == 1409) {//用户已注册，走aimi登录
                    aimiLogin(account);
                }

                break;
            case Manager.Event.loginSucc:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> aimi登录成功");
                }
                aimiSetUserCar(userCar);
                break;
            case Manager.Event.loginFailed:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> aimi登录失败");
                }
                UserCenterError userCenterErro = (UserCenterError) o;
                android.util.Log.e("uuuuuuuu", "登录失败 " + userCenterErro.toString());
                break;
            case Manager.Event.carInfoUploadSucc:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> aimi上传车辆信息成功");
                }
                //远程查询车辆信息
                Manager.getInstance().queryRemoteUserCar();
                break;
            case Manager.Event.carInfoUploadFailed:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> aimi上传车辆信息失败");
                }
                int errorCode1 = (int) o;
                android.util.Log.i("uuuuuuuu", "aimi上传车辆信息失败--->" + errorCode1);
                break;
            case Manager.Event.carInfoWriteDatabaseSucc:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> aimi写数据库车辆信息成功");
                }
                break;
            case Manager.Event.carInfoWriteDatabaseFailed:
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> aimi写数据库车辆信息失败");
                }
                break;
            case Manager.Event.obdConnectSucc:
                StringUtil.toastStringLong("连接成功");
                isDeviceConnect = true;
                break;
            case Manager.Event.obdConnectFailed:
                isDeviceConnect = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openDevice();
                        StringUtil.toastStringShort("设备连接失败");
                    }
                }, 500);
                break;
            case Manager.Event.dataCollectSucc:
                StringUtil.toastStringLong("数据准备成功");
                isDataPrepare = true;
                //走自动注册
                login1();
                break;
            case Manager.Event.dataCollectFailed:
                isDataPrepare = false;

//                Manager.ObdInitError obdInitError = (Manager.ObdInitError) o;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openDevice();
                        StringUtil.toastStringShort("数据准备失败");
                    }
                }, 500);

                break;
        }

    }

    /**
     * 自动登录、设备登录
     */
    private void login1() {
        android.util.Log.i("uuuuuuuu", "whw -->>登录开始");
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, "whw -->> -->> 登录开始");
        }
        if (UserCenter.getInstance().loginAutomatically()) {
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, "whw -->> -->> 自动登录成功");
            }
            login2();
        } else {
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, "whw -->> -->> 自动登录失败");

            }

            UserCenter.getInstance().DeviceLoginlogin(Utils.getImei(MainActivity.getInstance()));

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
                    Log.d(LogTag.OBD, "whw -->> -->> 查询本地车辆成功");
                }
                queryLocalCarModelInfo(car.carGenerationId);
                return;
            }
        }
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, "whw -->> -->> 查询本地车辆失败");
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
                Log.d(LogTag.OBD, " -->> 本地查询车型信息成功" + carGenerationId);
                Log.d(LogTag.OBD, " -->> token-->" + UserCenter.getInstance().getCurrentUserToken());
            }
            startServer();
        } else {
            //日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, " -->> 本地查询车型信息失败");
            }
            Manager.getInstance().queryRemoteCarModelInfo(carGenerationId, 1);
        }
    }

    /**
     * 启动业务
     */
    private void startServer() {
        isPush = false;
        // tianff 2016/7/7 UserCenterManager startServer 停止接收系统事件
        sdkListener.setActive(false);
        baseObdListener.onEvent(EVENT_OBD_USER_LOGIN_SUCC, null);
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, " -->> 启动业务");
        }
        Manager.getInstance().startReadThread();
    }

    /**
     * 弹出二维码,此方法可以保证push_token不为空
     */
    private void showRegQr(final String content) {
        if (AixintuiConfigs.push_token != null) {
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, " -->> 弹出二维码");
            }
            StringBuilder sb = new StringBuilder();
            sb.append(Configs.URL_REG_INFO).append("imei=").append(Utils.getImei(MainActivity.getInstance())).append("&");
            sb.append("pushToken=").append(AixintuiConfigs.push_token).append("&");
            sb.append("token=").append(UserCenter.getInstance().getCurrentUserToken());

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
     * 弹出二维码后超时,设备登录
     */
    private void outTime() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, "推送超时 -->> ");
                }
                UserCenter.getInstance().DeviceLoginlogin(Utils.getImei(MainActivity.getInstance()));
            }
        }, 1000 * 60 * 5);


    }

    /**
     * 艾米定制注册
     */
    private void aimiRegister(String name) {
        UserCenter.getInstance().register(name, DEFAULT_PASSWORD);
    }

    /**
     * 艾米定制登录
     */
    private void aimiLogin(String name) {
        UserCenter.getInstance().login(name, DEFAULT_PASSWORD);
    }

    /**
     * aimi设置用户车辆
     *
     * @param userCar
     */
    private void aimiSetUserCar(UserCar userCar) {
        if (userCar != null) {
            Manager.getInstance().setUserCar(userCar);
        }
    }


    /**
     * 艾米定制设置用户信息,参数不可为空。
     *
     * @param account         用户名（手机号）
     * @param carGenerationId 车型id(根据提供的接口获取)
     * @param vin             车辆vin
     */
    public void aimiSetUserInfo(String account, String carGenerationId, String vin) {
        if (account == null || carGenerationId == null || vin == null) {
            throw new RuntimeException("account或carGenerationId或vin参数为空");
        }
        LocalUserCarResult userCars = Manager.getInstance().queryLocalUserCar();
        if (userCars.userCars != null && userCars.userCars.length != 0) {
            userCar = userCars.userCars[0];
        }
        userCar.carGenerationId = carGenerationId;
        userCar.vinManually = vin;
        this.account = account;
        //aimi注册
        aimiRegister(this.account);
    }


    /**
     * 设备连接
     */
    public void openDevice() {
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, "连接设备openDevice -->> ");
        }
        StringUtil.toastStringLong("数据准备中,请稍后");
        Manager.getInstance().openDevice(Utils.getImei(MainActivity.getInstance()));
    }

    /**
     * 获取设备连接连接状态
     *
     * @return
     */
    public boolean getIsDeviceConnect() {
        return isDeviceConnect;
    }

    /**
     * 获取数据准备状态
     *
     * @return
     */
    public boolean getIsDataPrepare() {
        return isDataPrepare;
    }

    /**
     * 手动填写手机号，或者carGenerationId和vin信息；
     *
     * @param account
     * @param carGenerationId
     * @param vin
     */
    public void updataUserData(String account, String carGenerationId, String vin) {
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, "更新用户信息updataUserData -->> ");
            Log.d(LogTag.OBD, "停止采集线程 ");
        }
        //停止采集线程
        Manager.getInstance().stopReadThreadForUpgrage();
        sdkListener.setActive(true);
        LocalUserCarResult userCars = Manager.getInstance().queryLocalUserCar();
        if (userCars.userCars != null && userCars.userCars.length != 0) {
            userCar = userCars.userCars[0];
        }

        if (carGenerationId != null) {
            userCar.carGenerationId = carGenerationId;
        }
        if (vin != null) {
            userCar.vinManually = vin;
        }
        if (account != null) {
            this.account = account;
            //aimi注册
            aimiRegister(this.account);
        } else {
            //不修改手机号,只修改车辆信息
            aimiSetUserCar(userCar);
        }

    }
}

