package com.mapbar.android.obd.rearview.framework.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by tianff on 2016/5/7.
 */
public class VoiceReceiver extends BroadcastReceiver {
    public static final String VOICE_ACTION = "mapbar.obd.intent.action.VOICE_CONTROL";
    private final int DEFAULT_COMMAND = 000000;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (VOICE_ACTION.equals(action)) {
            int command = intent.getIntExtra("command", DEFAULT_COMMAND);
            Toast.makeText(context, "接收器" + command, Toast.LENGTH_SHORT).show();
            if (command != DEFAULT_COMMAND) {
                CommandControl.getInstance().executeCommand(command);
            }

        }
        //// FIXME: tianff 2016/6/8 VoiceReceiver onReceive 临时测试，测完删除
        if ("mapbar.obd.intent.action.login".equals(action)) {
            Toast.makeText(context, "启动v3h服务", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(context, OBDV3HService.class);
            context.startService(intent2);
        }
    }
}
