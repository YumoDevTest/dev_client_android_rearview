package com.mapbar.android.obd.rearview.lib.tts;

import android.content.Context;
import android.text.TextUtils;

import com.mapbar.obd.Manager;
import com.mapbar.obd.foundation.tts.contract.ITextToSpeechPlayer;


/**
 * OBD SDK 提供的播放语音的方法。 通过jni实现.
 * Created by zhangyunfei on 16/8/19.
 */
public class ObdSdkTextToSpeechPlayer implements ITextToSpeechPlayer {
    @Override
    public void play(Context context, String word) {
        if (TextUtils.isEmpty(word))
            return;
        Manager.getInstance().speak(word);
    }



}
