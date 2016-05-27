package com.mapbar.android.obd.rearview.obd;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.ixintui.pushsdk.PushSdkApi;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.activity.BaseActivity;
import com.mapbar.android.obd.rearview.framework.common.LayoutUtils;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.control.PageManager;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiPushManager;
import com.mapbar.android.obd.rearview.framework.log.LogManager;
import com.mapbar.android.obd.rearview.obd.page.MainPage;
import com.mapbar.android.obd.rearview.obd.page.SplashPage;

import java.util.ArrayList;

import static com.mapbar.android.obd.rearview.framework.control.PageManager.ManagerHolder.pageManager;


public class MainActivity extends BaseActivity {

    private static MainActivity instance;
    private boolean isFinishInitView = false;
    private RelativeLayout contentView;

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

        OBDSDKListenerManager.getInstance().init();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        final AppPage page = pageManager.createPage(SplashPage.class, null);
        transaction.replace(R.id.content_view, page);
        transaction.commit();

        onFinishedInit();

        //注册爱心推
        PushSdkApi.register(this, AixintuiConfigs.AIXINTUI_APPKEY, Utils.getChannel(this), Utils.getVersion(this) + "");
        // 绑定爱心推token
        AixintuiPushManager.getInstance().bindPush();


//        UserCenter.getInstance().login("18600425206", "111111");
        UserCenter.getInstance().login("13673096611", "888888");
        UserCenter.getInstance().login("18610857365", "111111");

    }

    @Override
    protected void onResume() {
        super.onResume();

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
                    pageManager.goPage(MainPage.class);
                }
            }, 2000);

        }
    }


    public RelativeLayout getContentView() {
        return contentView;
    }

}