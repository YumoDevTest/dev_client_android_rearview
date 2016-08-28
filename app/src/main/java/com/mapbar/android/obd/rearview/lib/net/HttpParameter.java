package com.mapbar.android.obd.rearview.lib.net;

import java.util.HashMap;

/**
 * HTTP 发送的参数
 * Created by zhangyunfei on 16/8/27.
 */
public class HttpParameter extends HashMap<String, String> {
    public static HttpParameter create() {
        return new HttpParameter();
    }

    public HttpParameter add(String key, String value) {
        put(key, value);
        return this;
    }

}
