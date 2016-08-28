package com.mapbar.android.obd.rearview.modules.ota;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.mapbar.android.obd.rearview.R;

/**
 * OTA提示用户 进度条
 * Created by zhangyunfei on 16/8/27.
 */
public class OtaAlertProgressView extends FrameLayout {
    private ProgressBar progressbar;

    public OtaAlertProgressView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public OtaAlertProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public OtaAlertProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.ota_alert_progress, this);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);
    }

    public void setProgress(int progress) {
        progressbar.setProgress(progress);
    }

}
