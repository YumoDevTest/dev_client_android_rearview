package com.mapbar.android.obd.rearview.obd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.activity.BaseActivity;
import com.mapbar.android.obd.rearview.framework.bean.QRInfo;
import com.mapbar.android.obd.rearview.framework.common.LayoutUtils;
import com.mapbar.android.obd.rearview.framework.control.PageManager;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogManager;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.android.obd.rearview.framework.manager.UserCenterManager;
import com.mapbar.android.obd.rearview.obd.page.MainPage;
import com.mapbar.android.obd.rearview.obd.page.SplashPage;
import com.mapbar.obd.SerialPortManager;

import java.util.ArrayList;

import static com.mapbar.android.obd.rearview.framework.control.PageManager.ManagerHolder.pageManager;


public class MainActivity extends BaseActivity {

    private static MainActivity instance;
    private boolean isFinishInitView = false;
    private RelativeLayout contentView;
    private OBDSDKListenerManager.SDKListener sdkListener;
    private boolean restart;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentView = (RelativeLayout) View.inflate(this, R.layout.main, null);
        setContentView(contentView);
        LogManager.getInstance().init(MainActivity.this);
        SerialPortManager.getInstance().setPath("/dev/ttyMT2");
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
                        LayoutUtils.disQrPop();//关闭二维码
                        break;
                }

            }
        };
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        LayoutUtils.disQrPop();//防止popupwindow泄露
        if (restart) {
            restart = false;
            restartmyapp();
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        super.onDestroy();
    }

    private void restartmyapp() {
        PageManager.getInstance().finishAll();
        Intent i = this.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(this.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
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
                        System.exit(0);
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //登录
                    Log.d(LogTag.OBD, "whw -->> UserCenterManager.getInstance().login() ==");
                    UserCenterManager.getInstance().login();
                }
            }, 2000);

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