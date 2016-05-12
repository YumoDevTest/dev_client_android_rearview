package com.mapbar.android.obd.rearview.framework.ixintui;

import android.content.Context;
import android.text.TextUtils;

import com.mapbar.android.net.HttpHandler;
import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.common.OBDHttpHandler;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.preferences.PreferencesConfig;
import com.mapbar.android.obd.rearview.obd.util.URLconfigs;
import com.mapbar.obd.SessionInfo;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

interface AixintuiCallBack {
    /**
     * 透传消息 MESSAGE_ACTION
     */
    public void onAppearMsg(Context context, String msg, String extra);

    /**
     * 爱心推异步结果返回 （无论爱心推底层做任何逻辑处理，只要是异步的任务，最后都会回调这个方法。包括初始化的register方法）
     * RESULT_ACTION
     */
    public void onAsyResult(Context context, String extra);

    /**
     * 当通知栏消息被点击之后
     */
    public void onNofificationClick(Context context, String msg);

}

/**
 * Created by THINKPAD on 2016/3/8.
 */
public class AixintuiPushManager implements AixintuiCallBack {

    public String aixintui_token = ""; // 爱心推token
    String url;
    String title;
    private Context mContext;
    private boolean isClickFromNotification = false;// 是否通过通知栏点击进入


    /**
     * 禁止构造
     */
    private AixintuiPushManager() {
    }

    /**
     * 获得单例
     */
    public static AixintuiPushManager getInstance() {
        return InstanceHolder.INSTANCE;
    }


    public void setAixintui_token(String aixintui_token) {
        this.aixintui_token = aixintui_token;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    /***
     * 透传信息处理
     */
    @Override
    public void onAppearMsg(Context context, String msg, String extra) {

    }

    @Override
    public void onAsyResult(Context context, String extra) {

    }

    @Override
    public void onNofificationClick(Context context, String msg) {
        if (!TextUtils.isEmpty(msg)) {
            try {
                JSONObject jObj1 = new JSONObject(msg);
                JSONObject jObj2 = new JSONObject(new String(jObj1.getString("extra")));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    public void bindPush() {
        String token = PreferencesConfig.IXINTUI_TOKEN.get();
        if (TextUtils.isEmpty(token) || !token.equals(aixintui_token)) {

            PreferencesConfig.IXINTUI_TOKEN.set(aixintui_token);
            String userId = SessionInfo.getCurrent().userId;
            String token2 = SessionInfo.getCurrent().token;
            HttpHandler http = new OBDHttpHandler(Global.getAppContext());
            http.setRequest(URLconfigs.BIND_PUSH, HttpHandler.HttpRequestType.POST);
            http.setCache(HttpHandler.CacheType.NOCACHE);
            http.addPostParamete("aitoken", TextUtils.isEmpty(token) ? aixintui_token : token);
            http.addPostParamete("userId", userId);
            http.addPostParamete("product", Configs.OBD_ANDROID);
            http.setHeader("token", "neC51yc8f1omKvDrZTGdUZEvIq4Tu8ZzprhKOXyw+hwwilqi7SptvJ1jMyD8QbDr");
            // 日志
            if (Log.isLoggable(LogTag.TEMP, Log.DEBUG)) {
                Log.e(LogTag.TEMP, " 调用绑定接口-->> " + "aixintui_token-->" + aixintui_token + "token---" + token + "--userId--" + userId + "--token--" + token2);
            }
            //内网测试token  neC51yc8f1omKvDrZTGdUZEvIq4Tu8ZzprhKOXyw+hwwilqi7SptvJ1jMyD8QbDr
            http.setHttpHandlerListener(new HttpHandler.HttpHandlerListener() {

                @Override
                public void onResponse(int httpCode, String str, byte[] responseData) {
                    // 日志
                    if (Log.isLoggable(LogTag.TEMP, Log.DEBUG)) {
                        Log.d(LogTag.TEMP, " 推送绑定httpCode-->> " + httpCode + "-->responseData-->" + responseData.toString() + "--str-->" + str);
                    }
                    if (httpCode == HttpStatus.SC_OK)
                        ;
                    {
                        JSONObject object = null;
                        try {
                            object = new JSONObject(new String(responseData));
                            // 日志
                            if (Log.isLoggable(LogTag.TEMP, Log.DEBUG)) {
                                Log.d(LogTag.TEMP, " JSONObject-->> " + object);
                            }
                            int code = object.getInt("code");
                            switch (code) {
                                case 200:

                                    break;

                                default:
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
            http.execute();

        } else {
            return;
        }

    }
    /**
     * 单例持有器
     */
    private static final class InstanceHolder {
        private static final AixintuiPushManager INSTANCE = new AixintuiPushManager();
    }
}