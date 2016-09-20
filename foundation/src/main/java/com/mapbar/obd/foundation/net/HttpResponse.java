package com.mapbar.obd.foundation.net;

import java.io.Serializable;

/**
 *
 * Created by zhangyunfei on 16/8/26.
 */
public class HttpResponse implements Serializable {

    public int code;// = json.getInt("code");
    public String msg;// = json.getString("msg");
    public String data;// = json.getString("data");

    public HttpResponse() {
    }

    public HttpResponse(int code, String msg, String data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
