package com.mapbar.android.obd.rearview.lib.net;

import android.text.TextUtils;

import com.google.protobuf.AbstractMessageLite;
import com.mapbar.android.obd.rearview.obd.Application;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.serial.utils.OutputStringUtil;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import security.OBD2Security;

/**
 * 发送和接收请求
 * 对中间数据进行了加密和解密
 * Created by zhangyunfei on 16/8/9.
 */
public final class HttpUtil {
    private static final MediaType CONTENT_TYPE = MediaType.parse("application/json");
    public static final String TAG = "HTTP";


    /**
     * 发送请求
     *
     * @param url
     * @param para
     * @param callback
     */
    public static void post(String url, HashMap<String, String> para, final HttpCallback callback, final ProgressIndicator progressIndicator) {
        if (TextUtils.isEmpty(url))
            throw new NullPointerException("url 不能为空");
        try {
            if (progressIndicator != null)
                progressIndicator.onStart();
            //加密构建消息体
            Headers headers = new Headers.Builder().add("token", Application.getInstance().getToken()).build();

            StringBuilder sb = new StringBuilder();
            sb.append("## [HTTP请求]准备发送,url=").append(url).append(", 参数: ");
            FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
            if (para != null) {
                for (Map.Entry<String, String> item : para.entrySet()) {
                    if (item.getKey() == null)
                        continue;
                    formEncodingBuilder.add(item.getKey(), item.getValue() == null ? "" : item.getValue());
                    sb.append(item.getKey()).append(" = ").append(item.getValue()).append(", ");
                }
            }
            LogUtil.d(TAG, OutputStringUtil.transferForPrint(sb.toString()));
            //构建请求
            final Request request = new Request.Builder()
                    .headers(headers)
                    .url(url)
                    .post(formEncodingBuilder.build())
                    .build();
            //new call
            Call call = MyHttpContext.getOkHttpClient().newCall(request);
            //请求加入调度
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    LogUtil.e(TAG, "## [HTTP请求]ERROR:" + e.getMessage(), e);
                    if (callback != null)
                        callback.onFailure(e, null);
                    if (progressIndicator != null)
                        progressIndicator.onFinish();
                }

                @Override
                public void onResponse(final Response response) throws IOException {
                    long bodyLength = response.body().contentLength();
                    String bodyString = response.body().string();
                    LogUtil.d(TAG, "##  [HTTP请求]收到响应,code=" + response.code() + ", content length=" + bodyLength + ", content=" + bodyString);
                    if (!response.isSuccessful()) {
                        if (callback != null) {
                            String errStr = "HTTP异常，http code=" + response.code();
                            callback.onFailure(new Exception(errStr), null);
                        }
                        if (progressIndicator != null)
                            progressIndicator.onFinish();
                        return;
                    }
                    JSONObject json = null;
                    HttpResponse httpResponse1 = null;
                    try {
                        json = new JSONObject(bodyString);
                        int code = json.getInt("code");
                        String msg = json.getString("msg");
                        String data = json.getString("data");
                        httpResponse1 = new HttpResponse(code, msg, data);
                        if (callback != null)
                            callback.onSuccess(data, httpResponse1);
                        if (progressIndicator != null)
                            progressIndicator.onFinish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (callback != null)
                            callback.onFailure(e, httpResponse1);
                        if (progressIndicator != null)
                            progressIndicator.onFinish();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (progressIndicator != null)
                progressIndicator.onFinish();
        }
    }
}
