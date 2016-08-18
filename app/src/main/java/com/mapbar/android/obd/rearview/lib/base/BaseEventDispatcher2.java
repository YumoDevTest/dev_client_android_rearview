package com.mapbar.android.obd.rearview.lib.base;

/**
 * 消息分发器.支持 泛型的callback
 * Created by zhangyunfei on 16/8/18.
 */
public abstract class BaseEventDispatcher2<CALLBACK extends Object> extends BaseEventDispatcher {
    private CALLBACK callback;

    @Override
    protected void onSDKEvent(int event, Object o) {
        onSDKEvent(event, o, callback);
    }

    protected abstract void onSDKEvent(int event, Object o, CALLBACK callback);

    public void start(CALLBACK callback) {
        if (callback == null)
            throw new NullPointerException();
        this.callback = callback;
        super.start();
    }
}
