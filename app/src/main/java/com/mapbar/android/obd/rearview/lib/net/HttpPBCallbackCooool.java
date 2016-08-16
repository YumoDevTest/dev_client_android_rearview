package com.mapbar.android.obd.rearview.lib.net;

import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;

/**
 * 比较 cool 的 HttpPBCallback
 * 具有错误提示能力 ，当遇到 failure 时，通知 eventbus遇到错误，MainActivity会toast这个错误信息
 * Created by zhangyunfei on 16/8/16.
 */
public abstract class HttpPBCallbackCooool implements HttpPBCallback {
    @Override
    public void onFailure(Exception e) {
        LogUtil.e(HttpPBUtil.TAG, "分发PB HTTP Exception: " + e.getMessage());
        EventBusManager.post(new PBHttpErrorEvent(e));
    }

}
