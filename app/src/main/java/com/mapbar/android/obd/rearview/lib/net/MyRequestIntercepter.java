package com.mapbar.android.obd.rearview.lib.net;

import android.text.TextUtils;

import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.obd.foundation.net.RequestIntercepter;

import java.util.HashMap;

/**
 * Created by zhangyunfei on 16/9/21.
 */
public class MyRequestIntercepter implements RequestIntercepter {

    private static final String TAG = "MyRequestIntercepter";

    @Override
    public void onHandleParameter(String url, HashMap<String, String> para, HashMap<String, String> headers) {
        String token = MyApplication.getInstance().getToken();
        if (TextUtils.isEmpty(token))
            com.mapbar.obd.foundation.log.LogUtil.e(TAG, "## 在准备http请求的header时， token为空");
        headers.put("token", token);
    }

}
