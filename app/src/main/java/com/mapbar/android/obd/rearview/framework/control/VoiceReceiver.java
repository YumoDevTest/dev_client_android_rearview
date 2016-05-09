package com.mapbar.android.obd.rearview.framework.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by tianff on 2016/5/7.
 */
public class VoiceReceiver extends BroadcastReceiver {
    private final String VOICE_ACTION = "mapbar.obd.intent.action.VOICE_CONTROL";
    private final int DEFAULT_COMMAND = 000000;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (VOICE_ACTION.equals(action)) {
            int command = intent.getIntExtra("command", DEFAULT_COMMAND);
            if (command != DEFAULT_COMMAND) {
                CommandControl.getInstance().executeCommand(command);
            }

        }
    }
}
