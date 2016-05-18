package com.mapbar.android.obd.rearview.framework.control;

import android.content.Intent;

import com.mapbar.android.obd.rearview.obd.MainActivity;

/**
 * 文本转语音TTS功能
 */
public class VoiceManager {
    private static VoiceManager voiceManager;
    private final String ACTION_TTS = "ime.service.intent.action.TTS_SPEACK";
    private VoiceManager() {

    }

    public static VoiceManager getInstance() {
        if (voiceManager == null) {
            voiceManager = new VoiceManager();
        }
        return voiceManager;
    }


    /**
     * @param msg 待转为语音的文本
     */
    public void sendBroadcastTTS(String msg) {
        Intent mIntent = new Intent(ACTION_TTS);
        String[] result = new String[2];
        result[0] = "navigate";
        result[1] = msg;
        mIntent.putExtra("param", result);
        MainActivity.getInstance().sendBroadcast(mIntent);
    }


    /***
     * 单例持有类
     */
    public static class VoiceManagerHolder {
        public static VoiceManager voiceManager = getInstance();
    }
}
