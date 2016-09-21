package com.mapbar.obd.foundation.tts;

import android.content.Context;

import com.mapbar.obd.foundation.tts.contract.ITextToSpeechPlayer;


/**
 * 文字转语音
 * Created by zhangyunfei on 16/8/19.
 */
public final class TextToSpeechManager {
    private static final String TAG = TextToSpeechManager.class.getSimpleName();
    private static ITextToSpeechPlayer textToSpeechPlayer;

    static {
        textToSpeechPlayer = createPlayer();
    }

    public static void setTextToSpeechPlayer(ITextToSpeechPlayer textToSpeechPlayer) {
        TextToSpeechManager.textToSpeechPlayer = textToSpeechPlayer;
    }

    /**
     * //在这里选择 用哪种TTS
     *
     * @return
     */
    private static ITextToSpeechPlayer createPlayer() {
//        return new ObdSdkTextToSpeechPlayer();
        //textToSpeechPlayer = new BroadcaastTextToSpeechPlayer();
        return null;
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
        if (textToSpeechPlayer == null) {
            com.mapbar.obd.foundation.log.LogUtil.e(TAG, "## 语音播放器未配置");
            return;
        }
        textToSpeechPlayer.play(context, word);
    }
}
