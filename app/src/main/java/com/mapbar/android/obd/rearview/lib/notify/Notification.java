package com.mapbar.android.obd.rearview.lib.notify;

/**
 * 通知，提醒 消息的描述
 * Created by zhangyunfei on 16/8/19.
 */
public class Notification {

    private String text;
    private String word;

    public Notification() {
    }

    public Notification(String text, String word) {
        this.text = text;
        this.word = word;
    }

    public String getText() {
        return text;
    }

    public String getWord() {
        return word;
    }


    @Override
    public String toString() {
        return text + ", " + word;
    }
}
