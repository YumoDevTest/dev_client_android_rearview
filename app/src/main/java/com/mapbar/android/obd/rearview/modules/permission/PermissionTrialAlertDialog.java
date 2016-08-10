package com.mapbar.android.obd.rearview.modules.permission;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mapbar.android.obd.rearview.R;

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
    }

//    /**
//     * 重试按钮点击事件
//     *
//     * @param onRetryClickListener
//     */
//    public void setOnRetryClickListener(View.OnClickListener onRetryClickListener) {
//        this.onRetryClickListener = onRetryClickListener;
//        if (btn_retry != null)
//            btn_retry.setOnClickListener(this.onRetryClickListener);
//
//    }


}
