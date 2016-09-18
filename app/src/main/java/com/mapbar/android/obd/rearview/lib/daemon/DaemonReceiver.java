package com.mapbar.android.obd.rearview.lib.daemon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 守护程序。自动启动的 receiver
 * Created by zhangyunfei on 16/9/1.
 */
public class DaemonReceiver extends BroadcastReceiver {
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "DaemonReceiver";
    private static final String ACTION_DAEMON_SERVICE = "com.mapbar.android.obd.rearview.action.DEAMON";

    @Override
    public void onReceive(Context context, Intent intent) {
        android.util.Log.d(TAG, "## DaemonReceiver收到action: " + intent.getAction());

        if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent intentService = new Intent(ACTION_DAEMON_SERVICE);
            intentService.setPackage(context.getPackageName());
            context.startService(intentService);
        }
    }
}
