package com.mapbar.android.obd.rearview.modules.tirepressure;

import com.mapbar.android.obd.rearview.lib.base.BaseEventDispatcher;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.Manager;
import com.mapbar.obd.TPMSAlarmData;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 胎压提醒的消息的 消息分发
 * Created by zhangyunfei on 16/8/18.
 */
public class TirePressureAlermEventDispatcher extends BaseEventDispatcher<TirePressureAlermEventDispatcher.TirePressureAlarmCallback> {
    private static final String TAG = TirePressureAlermEventDispatcher.class.getSimpleName();
    private Timer timer;

    public TirePressureAlermEventDispatcher(TirePressureAlarmCallback tirePressureAlarmCallback) {
        super(tirePressureAlarmCallback);
//        makeDemoData();
    }

    private void makeDemoData() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogUtil.i(TAG, "## 模拟胎压预警");
                TPMSAlarmData demoData = TireAlermMessageBuilder.createDemoData();
                raiseOnSDKEvent(Manager.Event.alarmTPMS, demoData);
            }
        }, 23000, 3000);
    }

    @Override
    protected void onSDKEvent(int event, Object o, TirePressureAlarmCallback tirePressureAlarmCallback) {
        if (event == Manager.Event.alarmTPMS) {
            if (o instanceof TPMSAlarmData) {
                LogUtil.i(TAG, "## 获得胎压预警");
                TPMSAlarmData tpmsAlarmData = (TPMSAlarmData) o;
                tirePressureAlarmCallback.onAlerm(tpmsAlarmData);
            }
        }
    }

    /**
     * 提醒
     */
    public interface TirePressureAlarmCallback {

        void onAlerm(TPMSAlarmData alarmData);
    }
}
