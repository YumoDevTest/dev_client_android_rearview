package com.mapbar.android.obd.rearview.lib.tts;

import android.content.Context;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.framework.control.VoiceManager;
import com.mapbar.obd.Manager;

/**
 * 通过广播 提供的播放语音的方法。
 * Created by zhangyunfei on 16/8/19.
 */
class BroadcaastTextToSpeechPlayer implements ITextToSpeechPlayer {
    @Override
    public void play(Context context, String word) {
        if (TextUtils.isEmpty(word))
            return;
        VoiceManager.getInstance().sendBroadcastTTS(context,word);
    }
}
