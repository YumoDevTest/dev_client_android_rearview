package com.mapbar.android.obd.rearview.modules.common;

import com.mapbar.android.obd.rearview.lib.base.BaseEventDispatcher;
import com.mapbar.obd.AlarmData;
import com.mapbar.obd.Manager;

/**
 * 胎压提醒的消息的 消息分发
 * Created by zhangyunfei on 16/8/18.
 */
public class SimpleAlermEventDispatcher extends BaseEventDispatcher<SimpleAlermEventDispatcher.SimpleAlarmCallback> {
    private static final String TAG = SimpleAlermEventDispatcher.class.getSimpleName();

    public SimpleAlermEventDispatcher(SimpleAlarmCallback callback) {
        super(callback);
    }

    @Override
    protected void onSDKEvent(int event, Object o, SimpleAlarmCallback callback) {
        if (event == Manager.Event.alarm) {
            if (o instanceof AlarmData) {
                AlarmData data = (AlarmData) o;
                if (callback != null)
                    callback.onAlerm(data);
            }
        }
    }

    /**
     * 提醒
     */
    public interface SimpleAlarmCallback {

        void onAlerm(AlarmData alarmData);
    }
}
