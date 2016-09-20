package com.mapbar.obd.foundation.tts;

import android.content.Context;


/**
 * 文字转语音
 * Created by zhangyunfei on 16/8/19.
 */
public final class TextToSpeechManager {
    private static final String TAG = TextToSpeechManager.class.getSimpleName();
    private static ITextToSpeechPlayer textToSpeechPlayer;

    static {
        //在这里选择 用哪种TTS
        textToSpeechPlayer = new ObdSdkTextToSpeechPlayer();
        //textToSpeechPlayer = new BroadcaastTextToSpeechPlayer();
    }

    private TextToSpeechManager() {
    }

    /**
     * 播放语音。
     *
     * @param word 要播放的文字
     */
    public static void speak(Context context, String word) {
        com.mapbar.obd.foundation.log.LogUtil.i(TAG, "## 准备播放语音: " + word);
        if (textToSpeechPlayer != null)
            textToSpeechPlayer.play(context,word);
    }
}