package com.mapbar.android.obd.rearview.modules.common.contract;

import com.mapbar.android.obd.rearview.lib.mvp.IMvpView;
import com.mapbar.android.obd.rearview.lib.notify.Notification;
import com.mapbar.map.MapView;
import com.mapbar.obd.TPMSAlarmData;

/**
 * Created by zhangyunfei on 16/8/18.
 */
public interface IMainPageView extends IMvpView {

    /**
     * 展示 通知。弹窗显示
     *
     * @param notification
     */
    void showNotification(Notification notification);

    /**
     * 完成通知显示。比如弹窗被关闭，或者弹窗里的倒计时到期
     */
    void onNotificationFinished();

    boolean isShowingNotification();
}
