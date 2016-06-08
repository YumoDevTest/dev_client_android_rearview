package com.mapbar.android.obd.rearview.framework.control;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.mapbar.android.obd.rearview.framework.manager.UserCenterManager;

/**
 * Created by liuyy on 2016/5/26.
 */
public class OBDV3HService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //登录
        UserCenterManager.getInstance().login(UserCenterManager.getInstance().SERVIE_LOGIN);
        //设置登录监听
        UserCenterManager.getInstance().setLoginListener(new UserCenterManager.LoginListener() {
            @Override
            public void isLogin(boolean isLogin) {
                if (isLogin) {
                    //开启业务
                } else {
                    goApp();
                }
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * 当需要(微信注册)弹出二维码时，启动应用
     */
    private void goApp() {
        Intent startIntent = new Intent();
        ComponentName cName = new ComponentName("com.mapbar.android.obd.rearview", "com.mapbar.android.obd.rearview.obd.MainActivity");
        startIntent.setComponent(cName);
        startActivity(startIntent);
    }
}