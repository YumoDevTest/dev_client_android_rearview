package com.mapbar.android.obd.rearview.umeng;

import android.content.Context;

import com.mapbar.android.obd.rearview.modules.common.MainActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by THINKPAD on 2016/3/22.
 */
public class MobclickAgentEx {
    public static void onEvent(String eventId) {
        MobclickAgent.onEvent(MainActivity.getInstance(), eventId);
    }


    public static void reportError(Context ctx, String arg1) {
        MobclickAgent.reportError(ctx, arg1);
    }

    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    public static void onPageStart(String arg0) {
        MobclickAgent.onPageStart(arg0);
    }

    public static void onPageEnd(String arg0) {
        MobclickAgent.onPageEnd(arg0);
    }

    public static void onKillProcess(Context context) {
        MobclickAgent.onKillProcess(context);
    }

    /**
     * 当用户两次使用之间间隔超过30秒时，将被认为是两个的独立的session(启动)，
     * 例如用户回到home，或进入其他程序，经过一段时间后再返回之前的应用。
     * 可通过接口：MobclickAgent.setSessionContinueMillis(long interval) 来设置这个间隔(参数单位为毫秒)。
     *
     * @param interval
     */
    public static void setSessionContinueMillis(long interval) {
        MobclickAgent.setSessionContinueMillis(interval);
    }

    /**
     * 在程序入口处，调用 MobclickAgent.openActivityDurationTrack(false) 禁止默认的页面统计方式，
     * 这样将不会再自动统计Activity。
     *
     * @param arg0
     */
    public static void openActivityDurationTrack(boolean arg0) {
        MobclickAgent.openActivityDurationTrack(arg0);
    }

}
