package com.mapbar.android.obd.rearview.views;

import android.os.Message;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.obd.util.SafeHandler;

/**
 * 到计时的textview
 * Created by zhangyunfei on 16/8/29.
 */
public class CountDownTextViewWrapper extends SafeHandler<TextView> {


    private static final int MSG_BEGIN = 1;
    private static final int MSG_COUNT_DOWN = 2;
    private static final int MSG_STOP = 3;

    private String origin_text = "";
    private int maxCount = 10;
    private int count = maxCount;

    public CountDownTextViewWrapper(TextView textView) {
        super(textView);
        origin_text = textView.getText().toString();
    }

    public CountDownTextViewWrapper(TextView textView, int maxCount) {
        super(textView);
        setMAX(maxCount);
        origin_text = textView.getText().toString();
    }

    @Override
    public void handleMessage(Message msg) {
        if (getInnerObject() == null)
            return;
        if (msg.what == MSG_BEGIN) {
            count = maxCount;
            getInnerObject().setText(String.format("%s(%s)", origin_text, count));
            sendMessageDelayed(obtainMessage(MSG_COUNT_DOWN), 1000);
        } else if (msg.what == MSG_COUNT_DOWN) {
            count--;
            if (count <= 0) {
                resetToOriginText();
                getInnerObject().performClick();
            } else {
                getInnerObject().setText(String.format("%s(%s)", origin_text, count));
                sendMessageDelayed(obtainMessage(MSG_COUNT_DOWN), 1000);
            }
        }
    }

    public void startCountDown() {
        obtainMessage(MSG_BEGIN).sendToTarget();
    }

    public void stopCountDown() {
        removeMessages(MSG_STOP);
        removeMessages(MSG_COUNT_DOWN);
        removeMessages(MSG_BEGIN);
        resetToOriginText();
    }

    public void setMAX(int maxCount) {
        this.maxCount = maxCount;
    }

    /**
     * 重置文本
     */
    private void resetToOriginText() {
        getInnerObject().setText(origin_text);
    }
}