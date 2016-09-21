package com.mapbar.obd.foundation.net;

import java.util.HashMap;

/**
 * 消息拦截器
 * Created by zhangyunfei on 16/9/21.
 */
public interface RequestIntercepter {

    /**
     * 当 配置参数时
     * @param url
     * @param para
     * @param headerPara
     */
    void onHandleParameter(String url, HashMap<String, String> para, HashMap<String, String> headerPara);
}
