package com.mapbar.android.obd.rearview.lib.net;

import java.io.Serializable;

/**
 *
 * 当访问 pb的 http消息后遇到错误时
 * 这是一个 eventbus 消息
 * Created by zhangyunfei on 16/8/16.
 */
public class PBHttpErrorEvent implements Serializable {
    private Exception exception;

    public PBHttpErrorEvent(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
