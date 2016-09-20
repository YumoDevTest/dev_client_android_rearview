package com.mapbar.android.obd.rearview.modules.ota;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.views.CountDownTextViewWrapper;

/**
 * 提示用户升级，确定或者取消
 * Created by zhangyunfei on 16/8/27.
 */
public class OtaAlertUpgradeView extends FrameLayout {
    public static final String CANCEL_TEXT = "取消";
    private View tv_firmware_pop_update;
    private TextView tv_firmware_pop_cancle;
    private CountDownTextViewWrapper countDownTextViewWrapper;
    private View.OnClickListener onUpgradeClickListener1;
    private View.OnClickListener onCancelClickListener1;

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
        tv_firmware_pop_update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTextViewWrapper.stopCountDown();
                if (onUpgradeClickListener1 != null)
                    onUpgradeClickListener1.onClick(view);
            }
        });
        tv_firmware_pop_update.setClickable(true);
        tv_firmware_pop_cancle = (TextView) findViewById(R.id.tv_firmware_pop_cancle);
        tv_firmware_pop_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTextViewWrapper.stopCountDown();
                if (onCancelClickListener1 != null)
                    onCancelClickListener1.onClick(view);
            }
        });
        tv_firmware_pop_cancle.setClickable(true);

        countDownTextViewWrapper = new CountDownTextViewWrapper(tv_firmware_pop_cancle, 10);
    }

    public void setUpgradeButtonClick(View.OnClickListener onClickListener) {
        this.onUpgradeClickListener1 = onClickListener;
    }


    public void setCancelButtonClick(View.OnClickListener onClickListener) {
        this.onCancelClickListener1 = onClickListener;
    }

    public void show() {
        this.setVisibility(View.VISIBLE);
        countDownTextViewWrapper.startCountDown();
    }


}