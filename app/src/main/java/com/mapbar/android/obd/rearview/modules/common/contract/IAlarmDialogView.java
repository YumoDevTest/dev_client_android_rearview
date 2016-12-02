package com.mapbar.android.obd.rearview.modules.common.contract;

import com.mapbar.obd.foundation.mvp.IMvpView;

/**
 * Created by Administrator on 2016/12/1 0001.
 */

public interface IAlarmDialogView extends IMvpView {
    //设置内容
    void setContentTv(String content);

    //设置倒计时
    void setTimerTv(int timer);

    //设置图片空间
    void showImage(int resourceId);

    //隐藏图片
    void hideImage();

    //关闭dialog
    void finishDialog();
}
