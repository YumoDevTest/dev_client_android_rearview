package com.mapbar.android.obd.rearview.lib.net;

import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;

import java.net.UnknownHostException;

/**
 * Created by zhangyunfei on 16/8/27.
 */
public class DefalutHttpExceptionHandler {

    public static void handleException(int httpCode, Exception ex, HttpResponse httpResponse) {
        if(ex instanceof UnknownHostException){
            EventBusManager.post(new HttpErrorEvent("网络连接失败，请检查网络是否通畅", ex));
            return;
        }
        if (httpResponse == null) {
            EventBusManager.post(new HttpErrorEvent("网络异常", ex));
            return;
        }
        //业务响应的异常 httpResponse
        if (httpResponse.code == 1401) {
            EventBusManager.post(new HttpErrorEvent("TOKEN失效", ex));
        } else if (httpResponse.code == 1402) {
            EventBusManager.post(new HttpErrorEvent("请求数据不完整", ex));
        } else if (httpResponse.code == 1403) {
            EventBusManager.post(new HttpErrorEvent("用户上传的VIN错误", ex));
        } else if (httpResponse.code == 206) {
            EventBusManager.post(new HttpErrorEvent("没有版本更新", ex));
        } else if (httpResponse.code == 504) {
            EventBusManager.post(new HttpErrorEvent("查询错误", ex));
        } else {
            EventBusManager.post(new HttpErrorEvent(httpResponse.msg, ex));
        }
    }

}

/*
*
* {\"code\":1401,\"msg\":\"token was unchecked!\"}	TOKEN失效
            {\"code\":1403,\"msg\":\"Data is not complete!\"}	请求数据不完整
                {\"code\":1403,\"msg\":\"vin is failed!\"}	用户上传的VIN错误
                    {\"code\":206,\"msg\":\"not query data!\"}	没有版本更新
                        {\"code\":504,\"msg\":\"query failed\"}	查询错误
*
* */
