package com.mapbar.android.obd.rearview.lib.versionUpdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.mapbar.obd.foundation.net.GsonHttpCallback;
import com.mapbar.obd.foundation.net.HttpResponse;
import com.mapbar.obd.foundation.net.HttpUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhangyh on 2016/10/28 0028.
 * 断点续传管理类
 */

public class FileBreakpointLoadManager {
    private static final String TAG = "BreakpointLoadManager";
    private Context context;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private AppInfo info;
    private boolean isUnregisterReceiver;
    /**
     * 创建广播对象
     */
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "网络状态已经改变");
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                String name = networkInfo.getTypeName();
                Log.d(TAG, "当前网络名称：" + name);
                downloadAPP(info);
            } else {
                Log.d(TAG, "没有可用网络");
            }
        }
    };
    /**
     * 创建一个关闭网络监听的广播,如果下载成功则反注册对网络的监听,
     * 如果不进行反注册,每次网络异常都会弹出安装界面
     */
    private BroadcastReceiver closeNetLinstenerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "接收到广播停止监听网络");
            context.unregisterReceiver(myReceiver);
        }
    };

    public FileBreakpointLoadManager(Context context) {
        this.context = context;
    }

    /**
     * 网络是否连接
     *
     * @return
     */
    public boolean isNetworkConnction() {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    /**
     * 开始升级
     *
     * @param info
     */
    public void startUpdate(AppInfo info) {
        this.info = info;
        //注册网络监听广播,如果有网进行下载,没网停止
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(myReceiver, filter);
        isUnregisterReceiver = true;
        //如果下载完成就会关闭网络监听
        IntentFilter closeNetLinstenerFilter = new IntentFilter("mapbar.obd.intent.action.CLOSE_NETLINSTENER");
        context.registerReceiver(closeNetLinstenerReceiver, closeNetLinstenerFilter);
    }

    public void unregisterReceiver() {
        if (isUnregisterReceiver)
            context.unregisterReceiver(closeNetLinstenerReceiver);
    }

    /**
     * 跳转到BreakpointService中进行后台下载
     *
     * @param appInfo
     */
    private void downloadAPP(AppInfo appInfo) {
        Intent updateIntent = new Intent(context, BreakpointLoadService.class);
        updateIntent.putExtra("app_info", appInfo);
        context.startService(updateIntent);
    }

    /**
     * 检测APP版本,需要一个监听,如果检测到需要版本升级,则触发监听
     *
     * @param onCheckAppVersionLinstener
     */
    public void checkAppVersion(final OnCheckAppVersionLinstener onCheckAppVersionLinstener) {
//        Toast.makeText(context, "开始查询最新信息", Toast.LENGTH_SHORT).show();
        Type type = new TypeToken<List<AppInfo>>() {
        }.getType();
        HashMap<String, String> headers = new HashMap<>();
        headers.put("ck", "a7dc3b0377b14a6cb96ed3d18b5ed117");
        HashMap<String, String> paras = new HashMap<>();
        paras.put("package_name", "com.mapbar.android.obd.rearview");
        HttpUtil.post(UpdateAPPConstants.APPCHECK_URL, headers, paras, new GsonHttpCallback<List<AppInfo>>(type) {
            @Override
            protected void onSuccess(List<AppInfo> appInfos, HttpResponse httpResponse) {
                AppInfo appInfo = appInfos.get(0);
                Log.d(TAG, appInfo.toString());
                if ((appInfo != null && hasNewAppVersion(appInfo, context))) {
                    onCheckAppVersionLinstener.prepareUpdate(appInfo);
                }
            }

            @Override
            public void onFailure(int httpCode, Exception e, HttpResponse httpResponse) {
                super.onFailure(httpCode, e, httpResponse);
            }
        }, null);
    }

    /**
     * 检测是否是新版本
     *
     * @param info
     * @param context
     * @return
     */
    private boolean hasNewAppVersion(AppInfo info, Context context) {
        int versionCode = -1;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (Exception e) {
        }
        return (info.getVersion_no() > versionCode && versionCode != -1) ? true : false;
    }

    public interface OnCheckAppVersionLinstener {
        void prepareUpdate(AppInfo info);
    }
}
