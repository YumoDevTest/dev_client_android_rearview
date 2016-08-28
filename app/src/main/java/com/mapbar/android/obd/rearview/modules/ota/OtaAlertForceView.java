package com.mapbar.android.obd.rearview.modules.ota;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.mapbar.android.obd.rearview.R;

/**
 * OTA提示用户 强制更新
 * Created by zhangyunfei on 16/8/27.
 */
public class OtaAlertForceView extends FrameLayout {
    private View tv_firmware_force_update;

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

        tv_firmware_force_update = findViewById(R.id.tv_firmware_force_update);
    }

    public void setUpgradeButtonClick(View.OnClickListener onClickListener) {
        tv_firmware_force_update.setOnClickListener(onClickListener);
    }

}
