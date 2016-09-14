package com.mapbar.android.obd.rearview.modules.common;

import android.content.Intent;

import com.ixintui.pushsdk.PushSdkApi;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.control.PageManager;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.lib.base.CustomMadeType;
import com.mapbar.android.obd.rearview.lib.net.HttpUtil;
import com.mapbar.android.obd.rearview.lib.umeng.MobclickAgentEx;
import com.mapbar.obd.Manager;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.UserCenter;


/**
 * 应用
 * Created by yun on 16/1/7.
 */
public class MyApplication extends android.app.Application {
    private static final String TAG = "MyApplication";
    private static MyApplication instance;
    //构建一个session，会话概念，该 session仅仅在app启动后有效，在app停止后销毁。
    // 用于临时在内存防止一些变量.仅建议存放 数据载体模型(model,entity,基础数据类型）
    // 不建议存放 业务操作类，比如manager等，以防止内存泄漏
    // 自己放的，自己用，用完清理
    private Session mSession;
    private boolean imei;
    private MainActivity mainActivity;//主页面

    public MyApplication() {
        instance = this;
    }

    public static MyApplication getInstance() {
        return MyApplication.instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化一个会话对象,一个会话对象可以在内存中存储一些变量，仅在app启动时有效
        mSession = new Session();
        android.util.Log.d(TAG, "## [application] 启动");
        CustomMadeType.printLog();

        Global.setAppContext(this);
        ObdContext.setSerialPortPath(Constants.SERIALPORT_PATH);
        Manager.onApplicationonCreate(this);
        //配置umeng统计分析
        MobclickAgentEx.onStart();
//        捕捉异常注册
//        Thread.setDefaultUncaughtExceptionHandler(new MyCrashLoger());
        //注册爱心推
        PushSdkApi.register(this, AixintuiConfigs.AIXINTUI_APPKEY, Utils.getChannel(this), Utils.getVersion(this) + "");

    }

    @Override
    public void onTerminate() {
        //配置umeng统计分析
        MobclickAgentEx.onTerminal();
        HttpUtil.clear();
        android.util.Log.d(TAG, "## [application] 停止");
        // infos 当前应用如果被系统强杀则方法不会被调用
        ObdContext.getInstance().exit();
        Manager.onApplicationTerminate();
        super.onTerminate();
    }


    /**
     * 获得会话对象
     * session是一个会话对象,一个会话对象可以在内存中存储一些变量，仅在app启动时有效
     *
     * @return
     */
    public Session getSession() {
        return mSession;
    }

    /**
     * 获得推送的token
     *
     * @return
     */
    public String getPushToken() {
        return AixintuiConfigs.getPushToken();
    }

    /**
     * 获得用户登录后的token
     *
     * @return
     */
    public String getToken() {
        return UserCenter.getInstance().getCurrentUserToken();
    }

    public String getImei() {
        return Utils.getImei(getMainActivity());
    }

    /**
     * 获得 userID
     *
     * @return
     */
    public String getUserID() {
        if (UserCenter.getInstance().getCurrentIdAndType() == null)
            return null;
        return UserCenter.getInstance().getCurrentIdAndType().userId;
    }

    /**
     * 退出当前app
     */
    public void exitApplication() {
        if (getMainActivity() != null && !getMainActivity().isFinishing()) {
            getMainActivity().finish();
        }
        Manager.getInstance().stopTrip(true);
        Manager.getInstance().cleanup();
        //umeng统计，在杀死进程是需要调用用来保存统计数据。
        MobclickAgentEx.onKillProcess(this);
        System.exit(0);
    }

    /**
     * 重启这个app
     */
    public void restartApplication() {
        startService(new Intent(getMainActivity(), RestartService.class));
        MyApplication.getInstance().exitApplication();
    }


    /**
     * 获得 首页 Activity
     *
     * @return
     */
    public MainActivity getMainActivity() {
        return mainActivity;
    }

    /**
     * 设置 主页 Activity
     *
     * @param mainActivity 主页
     */
    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


}
