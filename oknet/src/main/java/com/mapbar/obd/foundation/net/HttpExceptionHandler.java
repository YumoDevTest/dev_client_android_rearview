package com.mapbar.obd.foundation.net;

/**
 * 异常处理类
 * Created by zhangyunfei on 16/9/20.
 */
public interface HttpExceptionHandler {

    public void handleException(int httpCode, Exception ex, HttpResponse httpResponse);
}
