package com.mapbar.android.obd.rearview.views;

import android.os.Message;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.modules.ota.OtaAlertUpgradeView;
import com.mapbar.android.obd.rearview.obd.util.SafeHandler;

/**
 * Created by zhangyunfei on 16/8/29.
 */
public class MyHandler extends SafeHandler<TextView> {

    private static final int MAX = 10;
    private static final int BEGIN = 1;
    private static final int COUNT_DOWN = 2;
    private static String origin_text = "";
    private int count = MAX;

    public MyHandler(TextView object) {
        super(object);
    }

    @Override
    public void handleMessage(Message msg) {
        if (getInnerObject() == null)
            return;
        if (msg.what == BEGIN) {
            count = MAX;
            getInnerObject().setText(String.format("%s(%s)", origin_text, count));
            sendMessageDelayed(obtainMessage(COUNT_DOWN), 1000);
        } else if (msg.what == COUNT_DOWN) {
            count--;
            if (count <= 0) {
                getInnerObject().setText(origin_text);
                getInnerObject().performClick();
            } else {
                getInnerObject().setText(String.format("%s(%s)", origin_text, count));
                sendMessageDelayed(obtainMessage(COUNT_DOWN), 1000);
            }
        }
    }
}