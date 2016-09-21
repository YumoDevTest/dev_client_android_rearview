package com.mapbar.android.obd.rearview.modules.ota;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.base.SimpleActivity;
import com.mapbar.android.obd.rearview.modules.ota.contract.IOtaAlertView;

/**
 * 刷新固件 的视图
 * Created by zhangyunfei on 16/8/27.
 */
public class OtaAlertActivity extends SimpleActivity implements IOtaAlertView {
    private ViewGroup container1;

    private OtaAlertUpgradeView otaAlertUpgradeView;
    private OtaAlertForceView otaAlertForceView;
    private OtaAlertSuccessView otaAlertSuccessView;
    private OtaAlertFailureView otaAlertFailureView;
    private OtaAlertProgressView otaAlertProgressView;
    private OtaAlertPersenter otaAlertPersenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ota_alert);

        if (getIntent() == null)
            throw new NullPointerException();
        Bundle bundle = getIntent().getExtras();

        container1 = (ViewGroup) findViewById(R.id.container1);

        //提示 是否更新
        otaAlertUpgradeView = new OtaAlertUpgradeView(getActivity());
        otaAlertUpgradeView.setUpgradeButtonClick(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //更新按钮 点击
                otaAlertPersenter.beginUpgrade();
            }
        });
        otaAlertUpgradeView.setCancelButtonClick(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //取消按钮 点击
                finish();
            }
        });
        otaAlertUpgradeView.setVisibility(View.GONE);
        addChildView(otaAlertUpgradeView);
        //强制更新
        otaAlertForceView = new OtaAlertForceView(getActivity());
        otaAlertForceView.setUpgradeButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //更新按钮 点击
                otaAlertPersenter.beginUpgrade();
            }
        });
        otaAlertForceView.setVisibility(View.GONE);
        addChildView(otaAlertForceView);
        //刷新进度view
        otaAlertProgressView = new OtaAlertProgressView(getActivity());
        otaAlertProgressView.setVisibility(View.GONE);
        addChildView(otaAlertProgressView);
        //刷固件成功
        otaAlertSuccessView = new OtaAlertSuccessView(getActivity());
        otaAlertSuccessView.setOKButtonClick(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //确定按钮 点击
                otaAlertPersenter.onUpgradeFinish();
            }
        });
        otaAlertSuccessView.setVisibility(View.GONE);
        addChildView(otaAlertSuccessView);
        //刷新失败按钮
        otaAlertFailureView = new OtaAlertFailureView(getActivity());
        otaAlertFailureView.setRetryButtonClick(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //重试按钮 点击
                otaAlertPersenter.onRetryUpgrade();
            }
        });
        otaAlertFailureView.setCancelButtonClick(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //确定按钮 点击
            }
        });
        otaAlertFailureView.setVisibility(View.GONE);
        addChildView(otaAlertFailureView);

        otaAlertPersenter = new OtaAlertPersenter(this, bundle);


    }

    private void addChildView(View view) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        container1.addView(view, layoutParams);
    }

    @Override
    public void showView_alert_ForceUpgrade() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                otaAlertUpgradeView.setVisibility(View.GONE);
                otaAlertForceView.setVisibility(View.VISIBLE);
                otaAlertForceView.show();
                otaAlertSuccessView.setVisibility(View.GONE);
                otaAlertFailureView.setVisibility(View.GONE);
                otaAlertProgressView.setVisibility(View.GONE);
            }
        });
    }

    public void showView_alertUpgrade() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                otaAlertUpgradeView.show();
                otaAlertForceView.setVisibility(View.GONE);
                otaAlertSuccessView.setVisibility(View.GONE);
                otaAlertFailureView.setVisibility(View.GONE);
                otaAlertProgressView.setVisibility(View.GONE);
            }
        });
    }

    public void showView_alertSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                otaAlertUpgradeView.setVisibility(View.GONE);
                otaAlertForceView.setVisibility(View.GONE);
                otaAlertSuccessView.setVisibility(View.VISIBLE);
                otaAlertFailureView.setVisibility(View.GONE);
                otaAlertProgressView.setVisibility(View.GONE);
            }
        });
    }

    public void showView_alertFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                otaAlertUpgradeView.setVisibility(View.GONE);
                otaAlertForceView.setVisibility(View.GONE);
                otaAlertSuccessView.setVisibility(View.GONE);
                otaAlertFailureView.setVisibility(View.VISIBLE);
                otaAlertProgressView.setVisibility(View.GONE);
            }
        });
    }

    public void showView_alertProgress(final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                otaAlertUpgradeView.setVisibility(View.GONE);
                otaAlertForceView.setVisibility(View.GONE);
                otaAlertSuccessView.setVisibility(View.GONE);
                otaAlertFailureView.setVisibility(View.GONE);
                otaAlertProgressView.setVisibility(View.VISIBLE);
                otaAlertProgressView.setProgress(progress);
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
