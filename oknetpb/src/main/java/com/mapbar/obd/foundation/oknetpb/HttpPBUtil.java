package com.mapbar.obd.foundation.oknetpb;

import android.text.TextUtils;

import com.google.protobuf.AbstractMessageLite;
import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.foundation.net.MyHttpContext;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import security.OBD2Security;


/**
 * 发送和接收请求，pb协议
 * 对中间数据进行了加密和解密
 * Created by zhangyunfei on 16/8/9.
 */
public class HttpPBUtil {
    private static final MediaType CONTENT_TYPE = MediaType.parse("application/json");
    public static final String TAG = "HttpPBUtil";


    /**
     * 发送请求
     *
     * @param url
     * @param message
     * @param callback
     */
    public static void post(String url, AbstractMessageLite message, final HttpPBCallback callback) {
        if (TextUtils.isEmpty(url))
            throw new NullPointerException("url 不能为空");
        if (message == null)
            throw new NullPointerException("message 不能为空");

        LogUtil.d(TAG, String.format("## HTTP准备发送请求, url=%s, content=%s", url, message.toString()));
        post(url, message.toByteArray(), callback);
    }

    /**
     * 发送请求
     *
     * @param url
     * @param pbReqeustBytes
     * @param callback
     */
    public static void post(String url, byte[] pbReqeustBytes, final HttpPBCallback callback) {
        if (TextUtils.isEmpty(url))
            throw new NullPointerException("url 不能为空");
        //加密构建消息体
        byte[] btye4Request = null;
        try {
            btye4Request = OBD2Security.OBDEncode(pbReqeustBytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "## ERROR:" + e.getMessage(), e);
        }
        RequestBody requestBody = RequestBody.create(CONTENT_TYPE, btye4Request);
        //构建请求
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        //new call
        Call call = MyHttpContext.getOkHttpClient().newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                LogUtil.e(TAG, "## HTTP" + e.getMessage(), e);
                if (callback != null)
                    callback.onFailure(e);
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                LogUtil.d(TAG, "## HTTP收到响应,cod=" + response.code() + ", content length=" + response.body().contentLength());
                if (!response.isSuccessful()) {
                    if (callback != null) {
                        callback.onFailure(new Exception("HTTP异常，code=" + response.code()));
                        return;
                    }
                }
                byte[] bts = response.body().bytes();
                byte[] bytes2 = OBD2Security.OBDDecode(bts);
                if (callback != null)
                    callback.onSuccess(bytes2);

            }
        });
    }
}
