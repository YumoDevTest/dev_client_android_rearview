package com.mapbar.android.obd.rearview.framework.ixintui;

import android.content.Context;
import android.text.TextUtils;

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


    /**
     * 单例持有器
     */
    private static final class InstanceHolder {
        private static final AixintuiPushManager INSTANCE = new AixintuiPushManager();
    }
}