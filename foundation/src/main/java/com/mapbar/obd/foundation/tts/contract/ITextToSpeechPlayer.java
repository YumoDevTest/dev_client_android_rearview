package com.mapbar.obd.foundation.tts.contract;

import android.content.Context;

/**
 * 文字转语音 播放器
 * Created by zhangyunfei on 16/8/19.
 */
public interface ITextToSpeechPlayer {

    /**
     * 要播放的语音的文字
     *
     * @param word
     */
    public void play(Context context, String word);

}
