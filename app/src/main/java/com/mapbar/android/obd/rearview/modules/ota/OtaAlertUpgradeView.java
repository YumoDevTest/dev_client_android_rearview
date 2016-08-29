package com.mapbar.android.obd.rearview.modules.ota;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.mapbar.android.obd.rearview.R;

/**
 * 提示用户升级，确定或者取消
 * Created by zhangyunfei on 16/8/27.
 */
public class OtaAlertUpgradeView extends FrameLayout {
    private View tv_firmware_pop_update;
    private View tv_firmware_pop_cancle;

    public OtaAlertUpgradeView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public OtaAlertUpgradeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public OtaAlertUpgradeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.ota_alert_upgrade, this);

        tv_firmware_pop_update = findViewById(R.id.tv_firmware_pop_update);
        tv_firmware_pop_cancle = findViewById(R.id.tv_firmware_pop_cancle);
    }

    public void setUpgradeButtonClick(View.OnClickListener onClickListener) {
        tv_firmware_pop_update.setOnClickListener(onClickListener);
    }

    public void setCancelButtonClick(View.OnClickListener onClickListener) {
        tv_firmware_pop_cancle.setOnClickListener(onClickListener);
    }

    public void show() {
        this.setVisibility(View.VISIBLE);
        startTimer();
    }
}
