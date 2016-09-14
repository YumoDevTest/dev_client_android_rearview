package com.mapbar.android.obd.rearview.lib.net;

import java.io.Serializable;

/**
 * 当访问 pb的 http消息后遇到错误时
 * 这是一个 eventbus 消息
 * Created by zhangyunfei on 16/8/16.
 */
public class PBHttpErrorEvent implements Serializable {
    private Exception exception;
    private String errorMessage;

    public PBHttpErrorEvent(String errorMessage, Exception exception) {
        this.errorMessage = errorMessage;
        this.exception = exception;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Exception getException() {
        return exception;
    }
}
