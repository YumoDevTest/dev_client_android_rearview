package com.mapbar.android.obd.rearview.modules.common;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.android.net.HttpHandler;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.activity.BaseActivity;
import com.mapbar.android.obd.rearview.framework.bean.QRInfo;
import com.mapbar.android.obd.rearview.framework.common.LayoutUtils;
import com.mapbar.android.obd.rearview.framework.common.LayoutUtils_ui;
import com.mapbar.android.obd.rearview.framework.common.OBDHttpHandler;
import com.mapbar.android.obd.rearview.framework.common.StringUtil;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.control.ServicManager;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogManager;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.android.obd.rearview.framework.manager.UserCenterManager;
import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.lib.net.HttpErrorEvent;
import com.mapbar.android.obd.rearview.lib.net.HttpUtil;
import com.mapbar.android.obd.rearview.lib.net.PBHttpErrorEvent;
import com.mapbar.android.obd.rearview.modules.permission.PermissionBuySuccess;
import com.mapbar.android.obd.rearview.modules.permission.PermissonCheckerOnStart;
import com.mapbar.android.obd.rearview.modules.permission.model.PermissionBuyEvent;
import com.mapbar.android.obd.rearview.obd.bean.AppInfo;
import com.mapbar.android.obd.rearview.obd.page.MainPage;
import com.mapbar.android.obd.rearview.obd.page.SplashPage;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.android.obd.rearview.obd.util.SafeHandler;
import com.mapbar.android.obd.rearview.lib.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.lib.umeng.UmengConfigs;
import com.mapbar.mapdal.NativeEnv;
import com.mapbar.obd.Config;
import com.mapbar.obd.Manager;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.TripSyncService;
import com.mapbar.obd.UserCenter;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpStatus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int VIEW_CONTAINER_ID = R.id.content_view;
    private boolean isFinishInitView = false;
    private RelativeLayout contentView;
    private OBDSDKListenerManager.SDKListener sdkListener;
    private boolean restart;
    private boolean testAppUpdate = false;
    private PopupWindow updatePopu;
    private Handler handler;
    private String logFilePath = "";
    private boolean isGoDeclareActivity = false;
    private MainPage mainPage;

    static MainActivity instance;


    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        MyApplication.getInstance().setMainActivity(this);
        stopBackgroundService();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentView = (RelativeLayout) View.inflate(this, R.layout.main, null);
        setContentView(contentView);

        LayoutUtils_ui.proportional();
        LogManager.getInstance().init(MainActivity.this);
        logFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + Configs.FILE_PATH + "/client_Log1/";
        String logFilePath1 = Environment.getExternalStorageDirectory().getAbsolutePath() + Configs.FILE_PATH + "/log/";

        File file = new File(logFilePath);
        File file1 = new File(logFilePath1);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!file1.exists()) {
            file1.mkdirs();
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss");
        String fileName = "/log_" + format.format(c.getTime())
                + ".txt";
        File logFile = new File(logFilePath + fileName);
        if (Config.DEBUG) {
            com.mapbar.android.log.LogManager.getInstance().setLog(true);
            com.mapbar.android.log.LogManager.getInstance().setLogFile(true);
            if (!logFile.exists())
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            LogManager.getInstance().setLogFile(logFile);
        }

        ObdContext.setSerialPortPath(Constants.SERIALPORT_PATH);
        try {
            OBDSDKListenerManager.getInstance().init();
        } catch (IOException e) {
            e.printStackTrace();
            alert("初始化串口失败");
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //构建mainpage
        mainPage = new MainPage();
        //构建闪屏页
        final AppPage page = new SplashPage();
        transaction.replace(R.id.content_view, page);
        transaction.commit();
        onFinishedInit();
        //监听登录结果
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                switch (event) {
                    case OBDManager.EVENT_OBD_USER_LOGIN_SUCC:

                        goPage(mainPage);

                        break;
                    case OBDManager.EVENT_OBD_USER_LOGIN_FAILED:
                        QRInfo qrInfo = (QRInfo) o;
                        LogUtil.d(TAG, String.format("## 准备生成二维码,url = %s", qrInfo.getUrl()));
                        LayoutUtils.showQrPop(qrInfo.getUrl(), qrInfo.getContent(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LayoutUtils.showPopWindow("退出", "您确定退出吗？", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                        MyApplication.getInstance().exitApplication();
                                    }
                                });
                            }
                        });
                        break;
                    case OBDManager.EVENT_OBD_USER_REGISTER_SUCC:
                        MobclickAgentEx.onEvent(getActivity(), UmengConfigs.REGISTER_SUCC);
                        LayoutUtils.disQrPop();//关闭二维码
                        break;
                    case OBDManager.EVENT_OBD_TOKEN_LOSE://token失效处理走设备登陆
                        StringUtil.toastStringShort("token失效");

                        UserCenterManager.getInstance().sdkListener.setActive(true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                UserCenter.getInstance().DeviceLoginlogin(Utils.getImei(getActivity()));
                            }
                        }, 5000);


                        break;
                    case Manager.Event.dataCollectSucc:

                        break;
                }

            }
        };
        OBDSDKListenerManager.getInstance().addSdkListener(sdkListener);
        handler = new MyHandler(this);
        EventBusManager.register(this);
    }

    private void alert(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void stopBackgroundService() {
        Intent intent = new Intent("com.mapbar.obd.stopservice");
        sendBroadcast(intent);
        if (!NativeEnv.isServiceRunning(TripSyncService.class.getName())) {
            Intent intent1 = new Intent(MainActivity.this, TripSyncService.class);
            stopService(intent1);
        }
    }

    /**
     * 应用升级
     */
    private void checkAppVersion() {

        HttpHandler http = new OBDHttpHandler(getActivity());
        http.addParamete("package_name", getPackageName());

        String url = "http://wdservice.mapbar.com/appstorewsapi/checkexistlist/" + Build.VERSION.SDK_INT;//接口14.1

        http.setRequest(url, HttpHandler.HttpRequestType.GET);
        http.setCache(HttpHandler.CacheType.NOCACHE);
        http.setHeader("ck", "a7dc3b0377b14a6cb96ed3d18b5ed117");

        HttpHandler.HttpHandlerListener listener = new HttpHandler.HttpHandlerListener() {
            @Override
            public void onResponse(int httpCode, String str, byte[] responseData) {
                if (httpCode == HttpStatus.SC_OK) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(new String(responseData));
                        int code = object.getInt("status");
                        switch (code) {
                            case 200:
                                AppInfo info = parseAppInfo((JSONObject) object.getJSONArray("data").get(0));
                                if ((info != null && hasNewAppVersion(info)) || testAppUpdate) {
                                    Message msg = Message.obtain();
                                    msg.obj = info;
                                    handler.sendMessage(msg);
                                }
                                break;

                            default:
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        };
        http.setHttpHandlerListener(listener);
        http.execute();
    }

    private boolean hasNewAppVersion(AppInfo info) {
        int versionCode = -1;
        try {
            PackageManager manager = getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (Exception e) {
        }
        return (info.getVersion_no() > versionCode && versionCode != -1) ? true : false;
    }

    private AppInfo parseAppInfo(JSONObject data) {
        AppInfo info = new AppInfo();
        try {
            info.setDescription(data.getString("description"));
            info.setVersion_no(data.getInt("version_no"));
            info.setApk_path(data.getString("apk_path"));
            info.setName(data.getString("name"));
            info.setPackage_name(data.getString("package_name"));
            info.setIcon_path(data.getString("icon_path"));
            info.setApp_id(data.getString("app_id"));
            info.setVersion_id(data.getString("version_id"));
            info.setSize(data.getInt("size"));
        } catch (JSONException e) {
            info = null;
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 点击下载,则开启service,并通知栏同步下载进度
     *
     * @param info
     */
    private void showAppUpdate(final AppInfo info) {
        String versionName = "unknow";
        try {
            String pkName = getPackageName();
            versionName = getPackageManager().getPackageInfo(pkName, 0).versionName;
        } catch (Exception e) {
        }
        View popupView = View.inflate(this, R.layout.layout_app_update_pop, null);

        TextView tv_update_pop_content = (TextView) popupView.findViewById(R.id.tv_update_pop_content);
        TextView tv_update_app_info = (TextView) popupView.findViewById(R.id.tv_update_app_info);
        tv_update_pop_content.setText(info.getDescription());
        tv_update_app_info.setText("最新版本 " + versionName + "      " + "新版本大小:" + info.getSize() + "M");
        View tv_update_confirm = popupView.findViewById(R.id.tv_update_confirm);
        View tv_update_cancle = popupView.findViewById(R.id.tv_update_cancle);

        tv_update_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updatePopu != null)
                    updatePopu.dismiss();
                startAppDownload(info);
            }
        });

        tv_update_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updatePopu != null)
                    updatePopu.dismiss();
            }
        });

        updatePopu = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        updatePopu.setOutsideTouchable(false);
        updatePopu.setBackgroundDrawable(new BitmapDrawable());
        updatePopu.showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
    }

    private void startAppDownload(AppInfo info) {
        Intent updateIntent = new Intent(this, UpdateService.class);
        updateIntent.putExtra("app_info", info);
        startService(updateIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        checkAppVersion();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sdkListener.setActive(false);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        LayoutUtils.disQrPop();//防止popupwindow泄露
        HttpUtil.clear();
        EventBusManager.unregister(this);
        super.onDestroy();
        if (restart) {
            restart = false;
            MyApplication.getInstance().restartApplication();
        } else {
            startV3HService();
            Manager.getInstance().cleanup();
            ObdContext.getInstance().exit();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        MyApplication.getInstance().setMainActivity(null);
    }

    private void startV3HService() {
        //// FIXME: tianff 2016/7/25 关闭上传进程
//        stopService(new Intent(MainActivity.this, SyncService.class));
//        if (NativeEnv.isServiceRunning(MainActivity.create(), "obd.service.process")) {
//            ActivityManager activityManager = (ActivityManager) MainActivity.create().getSystemService(Context.ACTIVITY_SERVICE);
//            activityManager.killBackgroundProcesses("obd.service.process");
//        }
//        Intent i = new Intent("com.mapbar.obd.OBDV3HService");
//        sendBroadcast(i);

//        Intent i = new Intent(MainActivity.this, OBDV3HService.class);
//        i.setAction(OBDV3HService.ACTION_COMPACT_SERVICE);
//        i.putExtra(OBDV3HService.EXTRA_AUTO_RESTART, true);
//        i.putExtra(OBDV3HService.EXTRA_WAIT_FOR_SIGNAL, false);
//        i.putExtra(OBDV3HService.EXTRA_NEED_CONNECT, true);
//        ComponentName cName = startService(i);
//        new Thread(new Runnable() {
//            @Override
//            public void downloadPermision() {
//                Looper.prepare();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void downloadPermision() {
//                        Intent i = new Intent(MainActivity.this, OBDV3HService.class);
//                        i.setAction(OBDV3HService.ACTION_COMPACT_SERVICE);
//                        i.putExtra(OBDV3HService.EXTRA_AUTO_RESTART, true);
//                        i.putExtra(OBDV3HService.EXTRA_WAIT_FOR_SIGNAL, false);
//                        i.putExtra(OBDV3HService.EXTRA_NEED_CONNECT, true);
//                        ComponentName cName = startService(i);
//                        MyApplication.create().exitApplication();

////                        android.os.Process.killProcess(android.os.Process.myPid());
//                    }
//                }, 20 * 1000);
//                Looper.loop();
//            }
//
//        }).start();

        Intent intent = new Intent(this, ServicManager.class);
        startService(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                return false;
            }

            if (LayoutUtils.getPopupWindow() != null && LayoutUtils.getPopupWindow().isShowing()) {
                LayoutUtils.getPopupWindow().dismiss();
                return true;
            }

            LayoutUtils.showPopWindow("退出", "您确定退出吗？", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
//                      MyApplication.create().exitApplication();

                }
            });
            return true;
        } else {
            return false;
        }
    }


    public void onFinishedInit() {
        if (!isFinishInitView) {
            isFinishInitView = true;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    //登录
                    Log.d(LogTag.OBD, "whw -->> UserCenterManager.create().login() ==");
                    UserCenterManager.getInstance().login();
                }
            });

        }
    }

    public RelativeLayout getContentView() {
        return contentView;
    }

    public void goPage(AppPage page) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out);
        transaction.replace(VIEW_CONTAINER_ID, page);
        transaction.commit();
    }

    public void goMainPage() {
        goPage(mainPage);
    }

    private static class MyHandler extends SafeHandler<MainActivity> {

        public MyHandler(MainActivity object) {
            super(object);
        }

        @Override
        public void handleMessage(Message msg) {
            if (getInnerObject() == null || getInnerObject().isFinishing())
                return;
            getInnerObject().showAppUpdate((AppInfo) msg.obj);
        }
    }

    /**
     * 收到推送事件：购买功能成功或失败
     *
     * @param permissionBuyResult
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PermissionBuyEvent permissionBuyResult) {
        //购买成功后，需要重新下载功能权限
        if (permissionBuyResult.isBuySuccess()) {
            //弹窗购买成功
            new PermissonCheckerOnStart().downloadPermision(getActivity());
            startActivity(new Intent(getActivity(), PermissionBuySuccess.class));
        }
    }

    public Activity getActivity() {
        return this;
    }


    /**
     * 当遇到 http的错误消息时
     *
     * @param pbHttpErrorEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PBHttpErrorEvent pbHttpErrorEvent) {
        if (pbHttpErrorEvent.getException() == null)
            return;
        Toast.makeText(getActivity(), pbHttpErrorEvent.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HttpErrorEvent httpErrorEvent) {
        Toast.makeText(getActivity(), httpErrorEvent.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

}