package com.mapbar.android.obd.rearview.obd;

import android.content.ComponentName;
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

import com.mapbar.android.net.HttpHandler;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.activity.BaseActivity;
import com.mapbar.android.obd.rearview.framework.bean.QRInfo;
import com.mapbar.android.obd.rearview.framework.common.LayoutUtils;
import com.mapbar.android.obd.rearview.framework.common.OBDHttpHandler;
import com.mapbar.android.obd.rearview.framework.control.OBDV3HService;
import com.mapbar.android.obd.rearview.framework.control.PageManager;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogManager;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.android.obd.rearview.framework.manager.UserCenterManager;
import com.mapbar.android.obd.rearview.obd.bean.AppInfo;
import com.mapbar.android.obd.rearview.obd.page.MainPage;
import com.mapbar.android.obd.rearview.obd.page.SplashPage;
import com.mapbar.android.obd.rearview.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.umeng.UmengConfigs;
import com.mapbar.obd.Config;
import com.mapbar.obd.SerialPortManager;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.mapbar.android.obd.rearview.framework.control.PageManager.ManagerHolder.pageManager;


public class MainActivity extends BaseActivity {
    private static MainActivity instance;
    private boolean isFinishInitView = false;
    private RelativeLayout contentView;
    private OBDSDKListenerManager.SDKListener sdkListener;
    private boolean restart;
    private boolean testAppUpdate = false;
    private PopupWindow updatePopu;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showAppUpdate((AppInfo) msg.obj);
        }
    };
    private String logFilePath = "";

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        stopBackgroundService();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentView = (RelativeLayout) View.inflate(this, R.layout.main, null);
        setContentView(contentView);
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

        SerialPortManager.getInstance().setPath(Constants.SERIALPORT_PATH);
        OBDSDKListenerManager.getInstance().init();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        final AppPage page = pageManager.createPage(SplashPage.class, null);
        transaction.replace(R.id.content_view, page);
        transaction.commit();

        onFinishedInit();

        //监听登录结果
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                switch (event) {
                    case OBDManager.EVENT_OBD_USER_LOGIN_SUCC:
                        pageManager.goPage(MainPage.class);
                        break;
                    case OBDManager.EVENT_OBD_USER_LOGIN_FAILED:
                        QRInfo qrInfo = (QRInfo) o;
                        LayoutUtils.showQrPop(qrInfo.getUrl(), qrInfo.getContent(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LayoutUtils.showPopWindow("退出", "您确定退出吗？", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                        System.exit(0);
                                    }
                                });
                            }
                        });
                        break;
                    case OBDManager.EVENT_OBD_USER_REGISTER_SUCC:
                        MobclickAgentEx.onEvent(UmengConfigs.REGISTER_SUCC);
                        LayoutUtils.disQrPop();//关闭二维码
                        break;

                }

            }
        };
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);
    }

    private void stopBackgroundService() {
        Intent intent = new Intent("com.mapbar.obd.stopservice");
        sendBroadcast(intent);
    }

    /**
     * 应用升级
     */
    private void checkAppVersion() {

        HttpHandler http = new OBDHttpHandler(MainActivity.getInstance());
        http.addParamete("package_name", getPackageName());

        String url = "http://wdservice.mapbar.com/appstorewsapi/checkexistlist/" + Build.VERSION.SDK_INT;//接口14.1

        http.setRequest(url, HttpHandler.HttpRequestType.GET);
        http.setCache(HttpHandler.CacheType.NOCACHE);
        http.setHeader("ck", "a7dc3b0377b14a6cb96ed3d18b5ed117");//TODO

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
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        LayoutUtils.disQrPop();//防止popupwindow泄露
        super.onDestroy();
        if (restart) {
            restart = false;
            restartmyapp();
        } else {
            startV3HService();
            android.os.Process.killProcess(android.os.Process.myPid());

        }
    }

    private void startV3HService() {
        //// FIXME: tianff 2016/7/25 关闭上传进程
//        stopService(new Intent(this, SyncService.class));
        stopService(new Intent(this, OBDV3HService.class));
        Intent i = new Intent(MainActivity.this, OBDV3HService.class);
        i.setAction(OBDV3HService.ACTION_COMPACT_SERVICE);
        i.putExtra(OBDV3HService.EXTRA_AUTO_RESTART, true);
        i.putExtra(OBDV3HService.EXTRA_WAIT_FOR_SIGNAL, false);
        i.putExtra(OBDV3HService.EXTRA_NEED_CONNECT, true);
        ComponentName cName = startService(i);
    }

    private void restartmyapp() {
        startService(new Intent(MainActivity.this, RestartService.class));
        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final ArrayList<AppPage> pages = PageManager.getInstance().getPages();
            boolean flag = false;
            if (pages.size() > 0) {
                flag = pages.get(pages.size() - 1).onKeyDown(keyCode, event);
            }

            if (flag) {
                return flag;
            }

            if (LayoutUtils.getPopupWindow() != null && LayoutUtils.getPopupWindow().isShowing()) {
                LayoutUtils.getPopupWindow().dismiss();
                return true;
            }

            boolean isBack = pageManager.goBack();
            if (!isBack) {
                LayoutUtils.showPopWindow("退出", "您确定退出吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
//                        System.exit(0);
                    }
                });
            }
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
                    Log.d(LogTag.OBD, "whw -->> UserCenterManager.getInstance().login() ==");
                    UserCenterManager.getInstance().login();
                }
            });

        }
    }

    public RelativeLayout getContentView() {
        return contentView;
    }

    public void restartApp() {
        this.restart = true;
        finish();
    }


}