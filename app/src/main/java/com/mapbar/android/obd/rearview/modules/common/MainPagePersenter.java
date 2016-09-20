package com.mapbar.android.obd.rearview.modules.common;

import android.os.Handler;

import com.mapbar.obd.foundation.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.lib.notify.Notification;
import com.mapbar.android.obd.rearview.lib.notify.NotificationManager;
import com.mapbar.android.obd.rearview.modules.common.contract.IMainPageView;

/**
 * MainPage persenter
 * Created by zhangyunfei on 16/8/18.
 */
public class MainPagePersenter extends BasePresenter<IMainPageView> {
    private static final String TAG = MainPagePersenter.class.getSimpleName();

    private NotificationManager notificationManager;
    private Handler mHandler = new Handler();

    public MainPagePersenter(final IMainPageView view) {
        super(view);
        notificationManager = LogicFactory.getNotifycationManager();
        notificationManager.startListenAlerm(new NotificationManager.NotificationCommingCallback() {
            @Override
            public void onNotificationComming(Notification notification) {
                startCheckNotifications();//但新通知到来时，检查通知列表
            }
        });
    }


    public void clear() {
        if (notificationManager != null)
            notificationManager.stopListenAlerm();
    }

    /**
     * 启动,检查提醒消息队列
     */
    public void startCheckNotifications() {
//        LogUtil.d(TAG, "## 检查是否有通知 - start, 数量=" + notificationManager.getCountOfNotification());
        if (!notificationManager.hasMore()) {
            return;
        }
        mHandler.removeCallbacks(runnableShowNotification);
        if (getView().isShowingNotification()) {
            mHandler.postDelayed(runnableShowNotification, 2000);
            return;
        }
        mHandler.postDelayed(runnableShowNotification, 100);
    }

    private Runnable runnableShowNotification = new Runnable() {
        @Override
        public void run() {
            synchronized (this) {
                if (!notificationManager.hasMore())
                    return;
                if (!getView().isShowingNotification()) {
                    Notification notification = notificationManager.poll();
                    if (notification != null) {
//                        LogUtil.d(TAG, "## 检查是否有通知 - 显示通知");
                        getView().showNotification(notification);
                    }
                }
                //防止中断
                mHandler.postDelayed(runnableShowNotification, 500);
            }
        }
    };
}
