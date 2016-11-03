package com.mapbar.obd.foundation.net;

import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 发送和接收请求
 * 对中间数据进行了加密和解密
 * Created by zhangyunfei on 16/8/9.
 */
public final class HttpUtil {
    public static final String TAG = "HTTP";
    private static final MediaType CONTENT_TYPE = MediaType.parse("application/json");
    private static final Object DEFAULT_TAG = "DEFAULT_TAG";

    /**
     * 清理所有的请求
     */
    public static void clear() {
        MyHttpContext.getOkHttpClient().cancel(DEFAULT_TAG);
        MyHttpContext.getOkHttpClient().cancel(null);
        Log.d(TAG, "## [HTTP] 清理全部HTTP请求");
    }

    /**
     * 发送请求
     *
     * @param url
     * @param para
     * @param callback
     */
    public static void post(String url, HashMap<String, String> para, final HttpCallback callback) {
        post(url, null, para, callback, null);
    }


    /**
     * 发送请求
     *
     * @param url
     * @param para
     * @param callback
     */
    public static void post(String url, HashMap<String, String> para, final HttpCallback callback, final ProgressIndicator progressIndicator) {
        post(url, null, para, callback, progressIndicator);
    }

    /**
     * 发送请求
     *
     * @param url
     * @param para
     * @param callback
     */
    public static void post(String url, HashMap<String, String> headers, HashMap<String, String> para, final HttpCallback callback, final ProgressIndicator progressIndicator) {
        if (TextUtils.isEmpty(url))
            throw new NullPointerException("url 不能为空");
        try {
            if (progressIndicator != null)
                progressIndicator.onStart();
            RequestIntercepter requestParaIntercepter = MyHttpContext.getRequestIntercepter();

            //加密构建消息体
            HashMap<String, String> headsGloble = new HashMap<>();
            if (requestParaIntercepter != null) {
                requestParaIntercepter.onHandleParameter(url, para, headsGloble);
            }
            Headers.Builder headersBuilder = new Headers.Builder();
            for (Map.Entry<String, String> item : headsGloble.entrySet()) {
                if (TextUtils.isEmpty(item.getKey())) continue;
                headersBuilder.add(item.getKey(), item.getValue() == null ? "" : item.getValue());
            }

            if (headers != null) {
                for (Map.Entry<String, String> item : headers.entrySet()) {
                    headersBuilder.add(item.getKey(), item.getValue() == null ? "" : item.getValue());
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("## [HTTP] 准备发送,url=").append(url).append(", 参数: ");
            FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
            if (para != null) {
                for (Map.Entry<String, String> item : para.entrySet()) {
                    if (item.getKey() == null)
                        continue;
                    formEncodingBuilder.add(item.getKey(), item.getValue() == null ? "" : item.getValue());
                    sb.append(item.getKey()).append(" = ").append(item.getValue()).append(", ");
                }
            }
            //构建请求
            Request request = null;
            if (para == null) {
                request = new Request.Builder()
                        .headers(headersBuilder.build())
                        .url(url)
                        .tag(DEFAULT_TAG)
                        .build();
            } else {
                request = new Request.Builder()
                        .headers(headersBuilder.build())
                        .url(url)
                        .post(formEncodingBuilder.build())
                        .tag(DEFAULT_TAG)
                        .build();
            }
            //new call
            Call call = MyHttpContext.getOkHttpClient().newCall(request);
            //请求加入调度
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG, String.format("## [HTTP]onFailure exception type = %s, msg = %s", e.getClass(), e.getMessage()), e);
                    if (callback != null)
                        callback.onFailure(0, e, null);
                    if (progressIndicator != null)
                        progressIndicator.onFinish();
                }

                @Override
                public void onResponse(final Response response) throws IOException {
                    final long bodyLength = response.body().contentLength();
                    final String bodyString = response.body().string();
                    final int httpCode = response.code();
                    Log.d(TAG, "## [HTTP] 收到响应,HTTP Code=" + httpCode + ", content length=" + bodyLength + ", content=" + bodyString);
                    if (!response.isSuccessful()) {
                        if (callback != null) {
                            String errStr = "HTTP异常，http code=" + response.code();
                            callback.onFailure(httpCode, new Exception(errStr), null);
                        }
                        if (progressIndicator != null)
                            progressIndicator.onFinish();
                        return;
                    }
                    JSONObject json = null;
                    HttpResponse httpResponse1 = null;
                    try {
                        json = new JSONObject(bodyString);
                        int code = json.getInt("status");
                        String msg = json.getString("msg");
                        String data = json.isNull("data") ? null : json.getString("data");
                        httpResponse1 = new HttpResponse(code, msg, data);
                        if (callback != null)
                            callback.onSuccess(httpCode, data, httpResponse1);
                        if (progressIndicator != null)
                            progressIndicator.onFinish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "## [HTTP]解析ERROR:" + e.getMessage(), e);
                        if (callback != null)
                            callback.onFailure(httpCode, e, httpResponse1);
                        if (progressIndicator != null)
                            progressIndicator.onFinish();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null)
                callback.onFailure(0, e, null);
            if (progressIndicator != null)
                progressIndicator.onFinish();
        }
    }
}
