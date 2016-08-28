package com.mapbar.android.obd.rearview.modules.ota;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.mapbar.android.obd.rearview.R;

/**
 * OTA提示用户 升级失败
 * Created by zhangyunfei on 16/8/27.
 */
public class OtaAlertFailureView extends FrameLayout {
    private View firmware_update_fail_confirm;
    private View tv_firmware_pop_fail_cancle;

    public OtaAlertFailureView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public OtaAlertFailureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public OtaAlertFailureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.ota_alert_failure, this);

        firmware_update_fail_confirm = findViewById(R.id.firmware_update_fail_confirm);
        tv_firmware_pop_fail_cancle = findViewById(R.id.tv_firmware_pop_fail_cancle);
    }

    public void setRetryButtonClick(OnClickListener onClickListener) {
        firmware_update_fail_confirm.setOnClickListener(onClickListener);
    }

    public void setCancelButtonClick(OnClickListener onClickListener) {
        tv_firmware_pop_fail_cancle.setOnClickListener(onClickListener);
    }
}
