package com.mapbar.android.obd.rearview.modules.ota;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.views.CountDownTextViewWrapper;

/**
 * OTA提示用户 强制更新
 * Created by zhangyunfei on 16/8/27.
 */
public class OtaAlertForceView extends FrameLayout {
    private TextView tv_firmware_force_update;
    private CountDownTextViewWrapper countDownTextViewWrapper;
    private View.OnClickListener onUpgradeClickListener1;

    public OtaAlertForceView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public OtaAlertForceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public OtaAlertForceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.ota_alert_force, this);

        tv_firmware_force_update = (TextView) findViewById(R.id.tv_firmware_force_update);
        tv_firmware_force_update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTextViewWrapper.stopCountDown();
                if (onUpgradeClickListener1 != null)
                    onUpgradeClickListener1.onClick(view);
            }
        });
        countDownTextViewWrapper = new CountDownTextViewWrapper(tv_firmware_force_update, 5);
    }

    public void setUpgradeButtonClick(View.OnClickListener onClickListener) {
        this.onUpgradeClickListener1 = onClickListener;
    }

    public void show() {
        countDownTextViewWrapper.startCountDown();
    }

}
