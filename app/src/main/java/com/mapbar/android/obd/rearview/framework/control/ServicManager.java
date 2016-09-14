package com.mapbar.android.obd.rearview.framework.control;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.mapbar.android.obd.rearview.lib.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.modules.common.MyApplication;
import com.mapbar.mapdal.NativeEnv;
import com.mapbar.obd.TripSyncService;

import java.util.List;

/**
 * Created by tianff on 2016/7/25.
 */
public class ServicManager extends Service {
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
        if (!NativeEnv.isServiceRunning(TripSyncService.class.getName())) {
            Intent intent1 = new Intent(ServicManager.this, TripSyncService.class);
            stopService(intent1);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!NativeEnv.isServiceRunning(ServicManager.this, "com.mapbar.android.obd.rearview.framework.control.OBDV3HService") && !NativeEnv.isApplicationRunning(ServicManager.this, getPackageName())) {
                    Intent i = new Intent(ServicManager.this, OBDV3HService.class);
                    i.setAction(OBDV3HService.ACTION_COMPACT_SERVICE);
                    i.putExtra(OBDV3HService.EXTRA_AUTO_RESTART, true);
                    i.putExtra(OBDV3HService.EXTRA_WAIT_FOR_SIGNAL, false);
                    i.putExtra(OBDV3HService.EXTRA_NEED_CONNECT, true);
                    ComponentName cName = startService(i);
                    stopSelf();
                    MyApplication.getInstance().exitApplication();

                }
            }
        }, 45 * 1000);

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
