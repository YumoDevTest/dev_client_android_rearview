package com.mapbar.android.obd.rearview.modules.setting;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.manager.UserCenterManager;
import com.mapbar.android.obd.rearview.lib.base.MyBaseActivity;
import com.mapbar.android.obd.rearview.lib.push.PushState;
import com.mapbar.android.obd.rearview.lib.push.PushType;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.Manager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 更改手机号
 * Created by zhangyunfei on 16/7/26.
 */
public class ChangePhoneActivity extends MyBaseActivity {
    private static final String TAG = ChangePhoneActivity.class.getSimpleName();
    private ChangePhoneFragment changePhoneFragment;
    private ChangePhoneWaitFragment changePhoneWaitFragment;
    private ChangePhoneFinishFragment changePhoneFinishFragment;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();

        changePhoneFragment = new ChangePhoneFragment();
        changePhoneWaitFragment = new ChangePhoneWaitFragment();
        changePhoneFinishFragment = new ChangePhoneFinishFragment();

        if (savedInstanceState == null) {
            showPage_barcode();
            //启动UserCenterManager事件接收
            UserCenterManager.getInstance().sdkListener.setActive(true);
            //停止采集线程
            Manager.getInstance().stopReadThreadForUpgrage();
            LogUtil.d(TAG,"停止采集线程");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //启动采集线程
        Manager.getInstance().startReadThread();
        LogUtil.d(TAG,"启动采集线程");
    }

    @Override
    protected void onStart() {
        getTitlebarview().setTitle(R.string.page_title_change_phone);
        getTitlebarview().setEnableBackButton(true);
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    /**
     * 当接收到 扫码成功时
     * this is a eventbus 订阅者
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangePhoneEvent_ScanOK event) {
        Log.d(TAG, "展示等待用户填写页面");
        showPage_wait();
    }

    /**
     * 当接收到 用户填写注册成功时
     * 只有当本页面存活，才可能收到这个消息时
     *
     * this is a eventbus 订阅者
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangePhoneEvent_RegisterOK event) {
        Log.d(TAG, "展示填写成功页面");
        showPage_finish();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3500);
    }


    /**
     * 显示扫码二维码页
     */
    public void showPage_barcode() {
        showFragment(changePhoneFragment, false);
    }

    /**
     * 显示 等待用户填写页
     */
    public void showPage_wait() {
        showFragment(changePhoneWaitFragment, false);
    }

    /**
     * 显示填写成功页
     */
    public void showPage_finish() {
        showFragment(changePhoneFinishFragment, false);
    }
}
