package com.mapbar.android.obd.rearview.modules.common;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.modules.common.contract.IAlarmDialogView;

/**
 * Created by zhangyh on 2016/12/1 0001.
 * 预警Activity
 */
public class AlarmDialogActivity extends Activity implements IAlarmDialogView {

    private android.widget.ImageView ivtdicon;
    private android.widget.TextView tvtdcontent;
    private android.widget.TextView tvcountdown;
    private String alarmContent;
    private AlarmDialogPersenter alarmDialogPersenter;
    private android.widget.FrameLayout relaclose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_timer_dialog2);
        alarmContent = getIntent().getStringExtra("alarmContent");
        alarmDialogPersenter = new AlarmDialogPersenter(this);
        initView();
        //设置预警框内容
        alarmDialogPersenter.setContent(alarmContent);
        alarmDialogPersenter.countDown();
    }

    private void initView() {
        tvcountdown = (TextView) findViewById(R.id.tv_countdown);
        tvtdcontent = (TextView) findViewById(R.id.tv_td_content);
        ivtdicon = (ImageView) findViewById(R.id.iv_td_icon);
        relaclose = (FrameLayout) findViewById(R.id.rela_close);
        relaclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishDialog();
            }
        });
    }

    @Override
    public void setContentTv(String content) {
        tvtdcontent.setText(alarmContent);
    }

    @Override
    public void setTimerTv(int timer) {
        tvcountdown.setText("(" + timer + "s)");
    }

    @Override
    public void showImage(int resourceId) {
        ivtdicon.setVisibility(View.VISIBLE);
        ivtdicon.setBackgroundResource(resourceId);
    }

    @Override
    public void hideImage() {
        ivtdicon.setVisibility(View.GONE);
    }

    @Override
    public void finishDialog() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void alert(String msg) {

    }

    @Override
    public void alert(int sourceID) {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
