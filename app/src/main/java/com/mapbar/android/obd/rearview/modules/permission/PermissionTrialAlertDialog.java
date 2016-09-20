package com.mapbar.android.obd.rearview.modules.permission;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.mapbar.obd.foundation.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.modules.push.events.PermissionBuyEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 权限试用提醒 弹窗
 * Created by zhangyunfei on 16/8/4.
 */
public class PermissionTrialAlertDialog extends Activity {

    private PermissionAlertView permissionAlertView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(this);
    }

    private void init(Context context) {
        permissionAlertView = new PermissionAlertView(context);
        setContentView(permissionAlertView);

        if (getIntent() != null) {
            boolean expired = getIntent().getBooleanExtra("expired", false);
            int numberOfDay = getIntent().getIntExtra("numberOfDay", 0);
            permissionAlertView.setExpired(expired);//是否过期
            permissionAlertView.setNumberOfDay(numberOfDay);//剩余天数
        }
        permissionAlertView.setOnContinueTryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        EventBusManager.register(this);
    }

    @Override
    protected void onDestroy() {
        EventBusManager.unregister(this);
        super.onDestroy();
    }

    /**
     * 收到推送事件：购买功能成功或失败
     *
     * @param permissionBuyResult
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PermissionBuyEvent permissionBuyResult) {
        //购买成功则关闭自身,MainActivity也会收到推送，它会下载最新的权限
        if (permissionBuyResult.isBuySuccess()) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
