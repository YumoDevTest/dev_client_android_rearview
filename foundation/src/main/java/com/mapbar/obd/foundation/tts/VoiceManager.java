package com.mapbar.obd.foundation.tts;

import android.content.Context;

/**
 * 文本转语音TTS功能
 */
public class VoiceManager {
    private static VoiceManager voiceManager;
    //    private final String ACTION_TTS = "ime.service.intent.action.TTS_SPEACK";//艾米
    private final String ACTION_TTS = "mapbar.obd.action.ACTION_TTS";//达讯

    private VoiceManager() {

    }

    /**
     * 获取单例
     *
     * @return VoiceManager单例
     */
    public static VoiceManager getInstance() {
        if (voiceManager == null) {
            voiceManager = new VoiceManager();
        }
        return voiceManager;
    }


    /**
     * @param msg 待转为语音的文本
     */

    public void sendBroadcastTTS(Context context, String msg) {
        TextToSpeechManager.speak(context, msg);
    }
}
