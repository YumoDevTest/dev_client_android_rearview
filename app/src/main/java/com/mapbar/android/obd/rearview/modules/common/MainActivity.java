package com.mapbar.android.obd.rearview.modules.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.log.LogManager;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.android.obd.rearview.framework.manager.UserCenterManager;
import com.mapbar.android.obd.rearview.lib.base.AppPage2;
import com.mapbar.android.obd.rearview.lib.base.TitlebarActivity;
import com.mapbar.android.obd.rearview.lib.config.Configs;
import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.android.obd.rearview.lib.net.HttpErrorEvent;
import com.mapbar.android.obd.rearview.lib.serialportsearch.SerialportConstants;
import com.mapbar.android.obd.rearview.lib.serialportsearch.SerialportSPUtils;
import com.mapbar.android.obd.rearview.lib.services.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.lib.umeng.UmengConfigs;
import com.mapbar.android.obd.rearview.lib.versionUpdate.AppInfo;
import com.mapbar.android.obd.rearview.lib.versionUpdate.FileBreakpointLoadManager;
import com.mapbar.android.obd.rearview.model.QRInfo;
import com.mapbar.android.obd.rearview.modules.permission.PermissionBuySuccess;
import com.mapbar.android.obd.rearview.modules.permission.PermissonCheckerOnStart;
import com.mapbar.android.obd.rearview.modules.push.events.PermissionBuyEvent;
import com.mapbar.android.obd.rearview.util.LayoutUtils;
import com.mapbar.android.obd.rearview.util.StringUtil;
import com.mapbar.android.obd.rearview.util.Utils;
import com.mapbar.mapdal.NativeEnv;
import com.mapbar.obd.Config;
import com.mapbar.obd.Manager;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.TripSyncService;
import com.mapbar.obd.UserCenter;
import com.mapbar.obd.foundation.eventbus.EventBusManager;
import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.foundation.oknetpb.PBHttpErrorEvent;
import com.mapbar.obd.foundation.umeng.MobclickAgentEx;
import com.mapbar.obd.foundation.utils.SafeHandler;
import com.mapbar.obd.serial.comond.IOSecurityException;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 首页
 */
public class MainActivity extends TitlebarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int VIEW_CONTAINER_ID = R.id.content_view;
    private RelativeLayout contentView;
    private OBDSDKListenerManager.SDKListener sdkListener;
    private boolean testAppUpdate = false;
    private PopupWindow updatePopu;
    private Handler handler;
    private MainPage mainPage;
    private PopupWindow qrPopupWindow;
    private PopupWindow exitAlertDialog;
    private AppPage2 currentPage;
    private FileBreakpointLoadManager fileBreakpointLoadManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = (RelativeLayout) View.inflate(this, R.layout.main, null);
        setContentView(contentView);
        handler = new MyHandler(this);
        //构建闪屏页
        goPage(new SplashPage(), false);

        MyApplication.getInstance().setMainActivity(this);
        //停止后台服务
//        stopBackgroundService();

        LogManager.getInstance().init(MainActivity.this);
        initLogFile();

        try {
            if (TextUtils.isEmpty(ObdContext.getSerialPortPath())) {
                String serialport = SerialportSPUtils.getSerialport(this);
                ObdContext.configSerialport(serialport, SerialportConstants.BAUDRATE_DEFAULT, SerialportConstants.TIMEOUT_DEFAULT, SerialportConstants.IS_DEBUG_SERIALPORT);
            }
            OBDSDKListenerManager.getInstance().init();
        } catch (IOException | IOSecurityException e) {
            e.printStackTrace();
            alert("初始化串口失败," + e.getMessage());
            return;
        }

        //构建mainpage
        mainPage = new MainPage();
        //监听登录结果
        registerSDKListener();
        EventBusManager.register(this);
        //执行登录
        login();

    }


    private void initLogFile() {
        if (Config.DEBUG) {
            String logFilePath = "";
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
    }

    private void registerSDKListener() {
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
//                LogUtil.d(TAG, "## MainActivity OBDSDKListenerManager event=" + event);
                switch (event) {
                    case OBDManager.EVENT_OBD_USER_LOGIN_SUCC:
                        LogUtil.d(TAG, "## 登录成功");

                        goMainPage();
                        String currentUserToken = UserCenter.getInstance().getCurrentUserToken();
                        LogUtil.d(TAG, "##  当前token::::" + currentUserToken);
                        fileBreakpointLoadManager = new FileBreakpointLoadManager(getContext());
                        //检测是否有新版本,有新版本则通过handler发送消息,弹出对话框
                        fileBreakpointLoadManager.checkAppVersion(new FileBreakpointLoadManager.OnCheckAppVersionLinstener() {
                            @Override
                            public void prepareUpdate(AppInfo info) {
                                Message msg = Message.obtain();
                                msg.obj = info;
                                handler.sendMessage(msg);
                            }
                        });
                        break;
                    case OBDManager.EVENT_OBD_USER_LOGIN_FAILED:
                        LogUtil.d(TAG, "## 登录失败");
                        QRInfo qrInfo = (QRInfo) o;
                        showQrDialog(qrInfo);
                        break;
                    case OBDManager.EVENT_OBD_USER_REGISTER_SUCC:
                        MobclickAgentEx.onEvent(getActivity(), UmengConfigs.REGISTER_SUCC);
                        dismissQrPopwindow();//关闭二维码
                        break;
                    case OBDManager.EVENT_OBD_TOKEN_LOSE://token失效处理走设备登陆
                        LogUtil.d(TAG, "## token失效");
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
                        LogUtil.d(TAG, "## 数据准备成功");
                        break;
                }

            }
        };
        OBDSDKListenerManager.getInstance().addSdkListener(sdkListener);
        LogUtil.d(TAG, "## MainActivity addSdkListener");
    }

    private void unregisterSDKListener() {
        OBDSDKListenerManager.getInstance().removeSdkListener(sdkListener);
    }

    private void showQrDialog(QRInfo qrInfo) {
        LogUtil.d(TAG, "## MainActivity showQrDialog");
        LogUtil.d(TAG, String.format("## 准备生成二维码,url = %s", qrInfo.getUrl()));
        dismissQrPopwindow();
        qrPopupWindow = LayoutUtils.showQrPop(self(), qrInfo.getUrl(), qrInfo.getContent(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitAlertDialog();
            }
        });
    }

    private void showExitAlertDialog() {
        LogUtil.d(TAG, "## MainActivity showExitAlertDialog");
        dismissExitAlertDialog();
        exitAlertDialog = LayoutUtils.showPopWindow(self(), "退出", "您确定退出吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exitAlertDialog != null)
                    exitAlertDialog.dismiss();
                finish();
                Manager.getInstance().stopTrip(false);
                MyApplication.getInstance().exitApplication(true);
            }
        });
    }

    private void dismissExitAlertDialog() {
        if (exitAlertDialog != null && exitAlertDialog.isShowing()) {
            exitAlertDialog.dismiss();
            exitAlertDialog = null;
        }
    }

    private void dismissQrPopwindow() {
        if (qrPopupWindow != null && qrPopupWindow.isShowing()) {
            qrPopupWindow.dismiss();
            qrPopupWindow = null;
        }
    }

    private MainActivity self() {
        return this;
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
     * 点击下载,则开启service,并通知栏同步下载进度
     * @param info
     */
    private void showAppUpdate(final AppInfo info) {
        View popupView = View.inflate(this, R.layout.layout_app_update_pop, null);
        TextView tv_update_app_info = (TextView) popupView.findViewById(R.id.tv_update_app_info);
        tv_update_app_info.setText(info.getDescription() + "      " + "新版本大小:" + info.getSize() + "M");
        View tv_update_confirm = popupView.findViewById(R.id.tv_update_confirm);
        View tv_update_cancle = popupView.findViewById(R.id.tv_update_cancle);

        tv_update_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updatePopu != null)
                    updatePopu.dismiss();
                //如果需要更新的话,通过升级管理类,
                fileBreakpointLoadManager.startUpdate(info);
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



    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sdkListener != null)
            sdkListener.setActive(false);
        LogUtil.d(TAG, "## MainActivity sdkListener setActive(false)");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        currentPage = null;
        unregisterSDKListener();
        if (fileBreakpointLoadManager != null)
            fileBreakpointLoadManager.unregisterReceiver();
        dismissQrPopwindow();
        dismissExitAlertDialog();
        //防止popupwindow泄露
        EventBusManager.unregister(this);
        super.onDestroy();
        MyApplication.getInstance().setMainActivity(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                return false;
            }
            if (exitAlertDialog != null && exitAlertDialog.isShowing()) {
                dismissExitAlertDialog();
                return true;
            }
            if (qrPopupWindow != null && qrPopupWindow.isShowing()) {
                dismissQrPopwindow();
                return true;
            }
            showExitAlertDialog();
            return true;
        } else {
            return false;
        }
    }


    public void login() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //登录
                LogUtil.d(TAG, "## 登录,UserCenterManager.getInstance().login()");
                UserCenterManager.getInstance().login();
            }
        });
    }

    public RelativeLayout getContentView() {
        return contentView;
    }

    public void goPage(AppPage2 page) {
        goPage(page, true);
    }

    public void goPage(AppPage2 page, boolean useAnimation) {
        if (currentPage == page) return;
        currentPage = page;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (useAnimation)
            transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out);
        transaction.replace(VIEW_CONTAINER_ID, page);
        transaction.commit();
    }

    public void goMainPage() {
        goPage(mainPage);
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
}