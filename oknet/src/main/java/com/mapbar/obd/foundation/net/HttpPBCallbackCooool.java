package com.mapbar.obd.foundation.net;//package com.mapbar.obd.foundation.net;
//
//import android.text.TextUtils;
//
//import com.mapbar.obd.foundation.eventbus.EventBusManager;
//import com.mapbar.obd.foundation.log.LogUtil;
//
//
///**
// * 比较 cool 的 HttpPBCallback
// * 具有错误提示能力 ，当遇到 failure 时，通知 eventbus遇到错误，MainActivity会toast这个错误信息
// * Created by zhangyunfei on 16/8/16.
// */
//public abstract class HttpPBCallbackCooool implements HttpPBCallback {
//    @Override
//    public void onFailure(Exception e) {
//        LogUtil.e(HttpPBUtil.TAG, "分发PB HTTP Exception: " + e.getMessage());
//        EventBusManager.post(new PBHttpErrorEvent(TextUtils.isEmpty(e.getMessage()) ? "网络异常" : e.getMessage(), e));
//    }
//
//}
