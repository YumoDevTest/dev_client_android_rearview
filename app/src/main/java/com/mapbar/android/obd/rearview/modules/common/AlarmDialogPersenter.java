package com.mapbar.android.obd.rearview.modules.common;

import android.os.Message;

import com.mapbar.android.obd.rearview.modules.common.contract.IAlarmDialogView;
import com.mapbar.obd.foundation.mvp.BasePresenter;
import com.mapbar.obd.foundation.utils.SafeHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangyh on 2016/12/1 0001.
 * 预警persenter类
 */

public class AlarmDialogPersenter extends BasePresenter<IAlarmDialogView> {
    private final MyHandler myHandler;
    private IAlarmDialogView alarmDialogView;
    private int mCountdown = 5;
    private Timer mTimer = null;

    public AlarmDialogPersenter(IAlarmDialogView alarmDialogView) {
        super(alarmDialogView);
        this.alarmDialogView = alarmDialogView;
        myHandler = new MyHandler(this);
    }

    @Override
    public void clear() {

    }

    //设置预警内容
    public void setContent(String alarmContent) {
        getView().setContentTv(alarmContent);
    }

    //设置倒计时
    public void countDown() {
        getView().setTimerTv(mCountdown);
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    mCountdown--;
                    myHandler.sendEmptyMessage(0);
                }
            }, 1000, 1000);
        }
    }

    private class MyHandler extends SafeHandler<AlarmDialogPersenter> {

        public MyHandler(AlarmDialogPersenter object) {
            super(object);
        }

        //收到消息设置倒计时控件
        @Override
        public void handleMessage(Message msg) {
            getInnerObject().getView().setTimerTv(mCountdown);
            if (mCountdown <= 0) {
                mCountdown = 0;
                if (MainPagePersenter.isShowAlarm)
                    getInnerObject().getView().finishDialog();
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
                return;
            }
            super.handleMessage(msg);
        }

    }
}
