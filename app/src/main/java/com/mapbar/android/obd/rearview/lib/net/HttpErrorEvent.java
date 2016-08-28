package com.mapbar.android.obd.rearview.lib.net;

import com.mapbar.android.obd.rearview.lib.eventbus.EventBusEvent;

/**
 * Event bus事件，http发生异常
 * Created by zhangyunfei on 16/8/27.
 */
public class HttpErrorEvent extends EventBusEvent {
    private Exception exception;
    private String errorMessage;

    public HttpErrorEvent(Exception exception) {
        this.exception = exception;
    }

    public HttpErrorEvent(String msg, Exception ex) {
        this(ex);
        this.errorMessage = msg;
    }

    public Exception getException() {
        return exception;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
