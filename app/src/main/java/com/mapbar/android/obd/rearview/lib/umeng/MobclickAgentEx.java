package com.mapbar.android.obd.rearview.lib.umeng;

import android.content.Context;

import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by THINKPAD on 2016/3/22.
 */
public class MobclickAgentEx {
    private static final String TAG = "MobclickAgentEx";

    public static void onEvent(String eventId) {
        MobclickAgent.onEvent(MainActivity.getInstance(), eventId);
    }


    public static void reportError(Context ctx, String arg1) {
        MobclickAgent.reportError(ctx, arg1);
    }

    public static void reportError(Context ctx, Throwable arg1) {
        MobclickAgent.reportError(ctx, arg1);
    }

    public static void onActivityPause(Context context) {
        MobclickAgent.onPause(context);
    }

    public static void onActivityResume(Context context) {
        MobclickAgent.onResume(context);
    }

    public static void onPageStart(String arg0) {
        MobclickAgent.onPageStart(arg0);
    }

    public static void onPageEnd(String arg0) {
        MobclickAgent.onPageEnd(arg0);
    }

    public static void onKillProcess(Context context) {
        LogUtil.d(TAG,"## 出发umeng onKillProcess");
        MobclickAgent.onKillProcess(context);
    }

    /**
     * 当用户两次使用之间间隔超过30秒时，将被认为是两个的独立的session(启动)，
     * 例如用户回到home，或进入其他程序，经过一段时间后再返回之前的应用。
     * 可通过接口：MobclickAgent.setSessionContinueMillis(long interval) 来设置这个间隔(参数单位为毫秒)。
     *
     * @param interval
     */
    public static void setSessionKillProcessonContinueMillis(long interval) {
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

    public static void onStart() {
        //打开uemng的 debug
        MobclickAgent.setDebugMode(true);
        //禁用默认页面统计
        MobclickAgentEx.openActivityDurationTrack(false);
        //日志加密
        MobclickAgent.enableEncrypt(false);//6.0.0版本及以后
        //如不需要错误统计功能，可通过此方法关闭
//        MobclickAgent.setCatchUncaughtExceptions(false);
    }


    public static void onTerminal() {

    }
}
