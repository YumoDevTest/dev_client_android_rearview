package com.mapbar.android.obd.rearview.modules.setting;

import android.os.Bundle;
import android.os.Handler;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.manager.UserCenterManager;
import com.mapbar.android.obd.rearview.lib.base.MyBaseFragmentActivity;
import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.modules.setting.events.ChangePhoneEvent_RegisterFailure;
import com.mapbar.android.obd.rearview.modules.setting.events.ChangePhoneEvent_RegisterOK;
import com.mapbar.android.obd.rearview.modules.setting.events.ChangePhoneEvent_ScanOK;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 更改手机号
 * Created by zhangyunfei on 16/7/26.
 */
public class ChangePhoneActivity extends MyBaseFragmentActivity {
    private static final String TAG = ChangePhoneActivity.class.getSimpleName();
    private static final int TIMEOUT = 300000;
    private ChangePhoneFragment changePhoneFragment;
    private ChangePhoneWaitFragment changePhoneWaitFragment;
    private ChangePhoneFinishFragment changePhoneFinishFragment;
    private Handler handler;
    private Timer timer;

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
            LogUtil.d(TAG, "##停止采集线程");

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //设置超时标志
                    UserCenterManager.getInstance().setOutTime(true);
                    onWaitUserOperationTimeout();
                }
            }, TIMEOUT, TIMEOUT);
        }
    }

    /**
     * 当用户更改手机号超时
     */
    private void onWaitUserOperationTimeout() {
        LogUtil.d(TAG, "##等待用户操作Timerout");
        UserCenterManager.getInstance().sdkListener.setActive(true);
        UserCenter.getInstance().DeviceLoginlogin(Utils.getImei(getActivity()));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
        //启动采集线程
        Manager.getInstance().startReadThread();
        LogUtil.d(TAG, "##启动采集线程");
    }

    @Override
    protected void onStart() {
        getTitlebarview().setTitle(R.string.page_title_change_phone);
        getTitlebarview().setEnableBackButton(true);
        EventBusManager.register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBusManager.unregister(this);
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
        LogUtil.d(TAG, "##展示等待用户填写页面");
        showPage_wait();
    }

    /**
     * 当接收到 用户填写注册成功时
     * 只有当本页面存活，才可能收到这个消息时
     * <p/>
     * this is a eventbus 订阅者
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangePhoneEvent_RegisterOK event) {
        LogUtil.d(TAG, "##展示填写成功页面");
        showPage_finish();

        String userId = event.userId;
        String token = event.token;
        //从新走设备登录
        UserCenterManager.getInstance().sdkListener.setActive(true);
        UserCenter.getInstance().DeviceLoginlogin(Utils.getImei(ChangePhoneActivity.this));

        if (timer != null)
            timer.cancel();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3500);
    }


    /**
     * 当接收到 注册失败时
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangePhoneEvent_RegisterFailure event) {
        //触发超时
        onWaitUserOperationTimeout();
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
