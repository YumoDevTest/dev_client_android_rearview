package com.mapbar.obd.foundation.tts.impl;

import android.content.Context;
import android.text.TextUtils;

import com.mapbar.obd.foundation.tts.contract.ITextToSpeechPlayer;


/**
 * 通过广播 提供的播放语音的方法。
 * Created by zhangyunfei on 16/8/19.
 */
public class BroadcaastTextToSpeechPlayer implements ITextToSpeechPlayer {
    @Override
    public void play(Context context, String word) {
        if (TextUtils.isEmpty(word))
            return;
        //        Intent mIntent = new Intent(ACTION_TTS);
//        String[] result = new String[2];
//        result[0] = "navigate";
//        result[1] = msg;
//        mIntent.putExtra("param", result);
//        MainActivity.getInstance().sendBroadcast(mIntent);
//        Manager.getInstance().speak(msg);
    }
}
