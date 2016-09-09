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
 * 提示用户升级成功
 * Created by zhangyunfei on 16/8/27.
 */
public class OtaAlertSuccessView extends FrameLayout {
    private TextView firmware_update_succ_confirm;
    private CountDownTextViewWrapper countDownTextViewWrapper;
    private OnClickListener onOKClickListener;

    public OtaAlertSuccessView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public OtaAlertSuccessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public OtaAlertSuccessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.ota_alert_success, this);

        firmware_update_succ_confirm = (TextView) findViewById(R.id.firmware_update_succ_confirm);
        firmware_update_succ_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onOKClickListener != null)
                    onOKClickListener.onClick(view);
            }
        });
        countDownTextViewWrapper = new CountDownTextViewWrapper(firmware_update_succ_confirm, 5);
    }

    public void setOKButtonClick(OnClickListener onClickListener) {
        this.onOKClickListener = onClickListener;
    }

}
