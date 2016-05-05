package com.mapbar.android.obd.rearview.framework.control;

import android.content.Intent;

import com.mapbar.android.obd.rearview.MainActivity;

/**
 * Created by tianff on 2016/5/5.
 */
public class VoiceManager {
    private static VoiceManager voiceManager;

    private VoiceManager() {

    }

    public static VoiceManager getInstance() {
        if (voiceManager == null) {
            voiceManager = new VoiceManager();
        }
        return voiceManager;
    }

    public void sendBroadcastTTS(String msg) {
        Intent mIntent = new Intent("ime.service.intent.action.TTS_SPEACK");
        String[] result = new String[2];
        result[0] = "navigate";
        result[1] = msg;
        mIntent.putExtra("param", result);
        MainActivity.getInstance().sendBroadcast(mIntent);
    }


    public static class VoiceManagerHolder {
        public static VoiceManager voiceManager = getInstance();
    }
}
