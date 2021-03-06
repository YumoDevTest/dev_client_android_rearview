package com.mapbar.android.obd.rearview.lib.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.mapdal.NativeEnv;
import com.mapbar.obd.TripSyncService;
import com.mapbar.obd.foundation.log.LogUtil;

import java.util.List;

/**
 * Created by tianff on 2016/7/25.
 */
public class ServicManager extends Service {
    private static final String TAG = "ServicManager";
    private static final long INTERVAL = 45 * 1000;//延迟启动时长

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "## 启动ServicManager");
        if (!NativeEnv.isServiceRunning(TripSyncService.class.getName())) {
            Intent intent1 = new Intent(ServicManager.this, TripSyncService.class);
            stopService(intent1);
        }
        LogUtil.d(TAG, String.format("## 预计延迟 %s秒 后启动OBDV3HService", INTERVAL / 1000));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!NativeEnv.isServiceRunning(ServicManager.this, "com.mapbar.android.obd.rearview.lib.services.OBDV3HService") && !NativeEnv.isApplicationRunning(ServicManager.this, getPackageName())) {
                    LogUtil.d("DEBUG","## 准备启动 OBDV3HService");
                    Intent intent2 = new Intent(ServicManager.this, OBDV3HService.class);
                    intent2.setAction(OBDV3HService.ACTION_COMPACT_SERVICE);
                    intent2.putExtra(OBDV3HService.EXTRA_AUTO_RESTART, true);
                    intent2.putExtra(OBDV3HService.EXTRA_WAIT_FOR_SIGNAL, false);
                    intent2.putExtra(OBDV3HService.EXTRA_NEED_CONNECT, true);
                    intent2.setPackage(getPackageName());
                    LogUtil.d(TAG, "## 启动OBDV3HService");
                    startService(intent2);

                    stopSelf();
                    MyApplication.getInstance().exitApplication(false);

                }
            }
        }, INTERVAL);

        return START_NOT_STICKY;
    }

    private boolean isActivityRunning(Context context, String className) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals("com.mapbar.android.obd.rearview") && (info.topActivity.getClassName().equals(className) || info.baseActivity.getClassName().equals(className))) {
                return true;
            }
        }
        return false;
    }
}
