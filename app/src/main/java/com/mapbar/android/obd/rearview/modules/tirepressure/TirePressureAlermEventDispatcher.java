package com.mapbar.android.obd.rearview.modules.tirepressure;

import com.mapbar.android.obd.rearview.lib.base.BaseEventDispatcher2;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.Manager;
import com.mapbar.obd.TPMSAlarmData;

/**
 * 胎压提醒的消息的 消息分发
 * Created by zhangyunfei on 16/8/18.
 */
public class TirePressureAlermEventDispatcher extends BaseEventDispatcher2<TirePressureAlermEventDispatcher.TirePressureAlarmCallback> {
    private static final String TAG = TirePressureAlermEventDispatcher.class.getSimpleName();

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
