package com.mapbar.android.obd.rearview.lib.autostart;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.mapbar.android.obd.rearview.lib.autostart.contract.DelayAutoStartServiceFactory;
import com.mapbar.android.obd.rearview.lib.autostart.contract.DelayAutoStartService;

/**
 * 守护 服务
 * Created by zhangyunfei on 16/9/1.
 */
public class DaemonService extends Service {
    private DelayAutoStartService delayAutoStartService;

    private static final String TAG = "DaemonService";

    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.d(TAG, "## onCreate");
        android.util.Log.d(TAG, "## 守护服务启动");

        //启动监听： 启动延迟
        if (delayAutoStartService == null) {
            delayAutoStartService = DelayAutoStartServiceFactory.getAutostartHandler();
            if (delayAutoStartService != null)//这里根据变异环境，迪瑞特的会有值，而其他无需延迟自启动的为null
                delayAutoStartService.start(getContext());
        }
    }

    @Override
    public void onDestroy() {
        android.util.Log.d(TAG, "## onDestroy");
        if (delayAutoStartService != null) {
            delayAutoStartService.clear();
            delayAutoStartService = null;
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        android.util.Log.d(TAG, " ## onStartCommand, action = " + intent.getAction());

        //发送延迟启动消息
        Intent intentDelayStart = new Intent(DelayAutoStartService.ACTION_DELAY_START_APP);
        sendBroadcast(intentDelayStart);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Context getContext() {
        return this;
    }
}
