package com.mapbar.android.obd.rearview.obd;

import com.ixintui.pushsdk.PushSdkApi;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.modules.common.Session;
import com.mapbar.android.obd.rearview.umeng.MobclickAgentEx;
import com.mapbar.obd.CrashHandler;
import com.mapbar.obd.Manager;

import java.util.HashMap;


/**
 * 应用
 * Created by yun on 16/1/7.
 */
public class Application extends android.app.Application {
    private static Application instance;
    //构建一个session，会话概念，该 session仅仅在app启动后有效，在app停止后销毁。
    // 用于临时在内存防止一些变量.仅建议存放 数据载体模型(model,entity,基础数据类型）
    // 不建议存放 业务操作类，比如manager等，以防止内存泄漏
    // 自己放的，自己用，用完清理
    private Session mSession;

    public Application() {
        instance = this;
    }

    public static Application getInstance() {
        return Application.instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化一个会话对象,一个会话对象可以在内存中存储一些变量，仅在app启动时有效
        mSession =new Session();

        Global.setAppContext(this);
        Manager.onApplicationonCreate(this);
        //捕捉异常注册
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this, 3);
        //注册爱心推
        PushSdkApi.register(this, AixintuiConfigs.AIXINTUI_APPKEY, Utils.getChannel(this), Utils.getVersion(this) + "");
        //禁用默认页面统计
        MobclickAgentEx.openActivityDurationTrack(false);
    }

    @Override
    public void onTerminate() {
        // infos 当前应用如果被系统强杀则方法不会被调用
        Manager.onApplicationTerminate();
        super.onTerminate();
    }


    /**
     * 获得会话对象
     * session是一个会话对象,一个会话对象可以在内存中存储一些变量，仅在app启动时有效
     * @return
     */
    public Session getSession(){
        return mSession;
    }
}
