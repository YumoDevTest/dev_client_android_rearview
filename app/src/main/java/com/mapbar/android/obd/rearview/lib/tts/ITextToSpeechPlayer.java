package com.mapbar.android.obd.rearview.lib.tts;

/**
 * 文字转语音 播放器
 * Created by zhangyunfei on 16/8/19.
 */
interface ITextToSpeechPlayer {

    /**
     * 要播放的语音的文字
     *
     * @param word
     */
    public void play(String word);

}
