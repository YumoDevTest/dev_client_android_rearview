package com.mapbar.android.obd.rearview.lib.notify;

import android.content.Context;

import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.common.SimpleAlermEventDispatcher;
import com.mapbar.android.obd.rearview.modules.common.SimpleAlermMessageBuilder;
import com.mapbar.android.obd.rearview.modules.permission.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.tirepressure.TireAlermMessageBuilder;
import com.mapbar.android.obd.rearview.modules.tirepressure.TirePressureAlermEventDispatcher;
import com.mapbar.android.obd.rearview.obd.Application;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.AlarmData;
import com.mapbar.obd.TPMSAlarmData;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 通知提醒管理器
 * Created by zhangyunfei on 16/8/19.
 */
public final class NotificationManager {
    private Queue<Notification> notificationQueue;
    private Context mContext;

    private TirePressureAlermEventDispatcher tirePressureAlermEventDispatcher;
    private SimpleAlermEventDispatcher simpleAlermEventDispatcher;
    private NotificationCommingCallback commingCallback;
    private PermissionManager permissionManager;

    public NotificationManager() {
        notificationQueue = new LinkedList<>();
        mContext = Application.getInstance();
        permissionManager = LogicFactory.createPermissionManager(Application.getInstance());
    }

    /**
     * 是否还有空的
     *
     * @return
     */
    public boolean hasMore() {
        return notificationQueue.size() > 0;
    }

    public void offerSimpleAlermData(AlarmData data) {
        if (data == null)
            return;
        //检查是否有 体检模块权限，如果有，才弹出故障码提醒
        PermissionManager.PermissionResult result = permissionManager.checkPermission(PermissionKey.PERMISSION_CHECK_UP);
        if (!result.isValid)
            return;
        //有权限，则加入到提醒队列
        Notification notification = SimpleAlermMessageBuilder.from(mContext, data);
        if (notification == null) return;
        offerNotification(notification);
    }

    public void offerTirepresstureAlerm(TPMSAlarmData data) {
        if (data == null)
            return;
        //检查是否有 体检模块权限，如果有，才弹出故障码提醒
        PermissionManager.PermissionResult result = permissionManager.checkPermission(PermissionKey.PERMISSION_TIRE_PRESSURE);
        if (!result.isValid)
            return;
        //有权限，则加入到提醒队列

        Notification notification = TireAlermMessageBuilder.from(mContext, data);
        if (notification == null) return;
        offerNotification(notification);
    }


    public void offerNotification(Notification notification) {
        if (notification == null)
            return;
        notificationQueue.offer(notification);
        raiseOnNewNotificationComming(notification);
    }

    private void raiseOnNewNotificationComming(Notification notification) {
        if (commingCallback != null)
            commingCallback.onNotificationComming(notification);
    }

    public synchronized Notification poll() {
        Notification notification = notificationQueue.poll();
        return notification;
    }


    public void startListenAlerm(NotificationCommingCallback commingCallback) {
        this.commingCallback = commingCallback;
        tirePressureAlermEventDispatcher = new TirePressureAlermEventDispatcher(new TirePressureAlermEventDispatcher.TirePressureAlarmCallback() {
            @Override
            public void onAlerm(TPMSAlarmData alarmData) {
                offerTirepresstureAlerm(alarmData);
            }
        });
        tirePressureAlermEventDispatcher.start();
        simpleAlermEventDispatcher = new SimpleAlermEventDispatcher(new SimpleAlermEventDispatcher.SimpleAlarmCallback() {
            @Override
            public void onAlerm(AlarmData alarmData) {
                offerSimpleAlermData(alarmData);
            }
        });
        simpleAlermEventDispatcher.start();
    }

    public void stopListenAlerm() {
        if (simpleAlermEventDispatcher != null) {
            simpleAlermEventDispatcher.stop();
            simpleAlermEventDispatcher = null;
        }
        if (tirePressureAlermEventDispatcher != null) {
            tirePressureAlermEventDispatcher.stop();
            tirePressureAlermEventDispatcher = null;
        }
    }

    /**
     * 获得通知的数量
     *
     * @return
     */
    public int getCountOfNotification() {
        return notificationQueue.size();
    }

    public interface NotificationCommingCallback {
        /**
         * 当一个通知到达
         *
         * @param notification
         */
        public void onNotificationComming(Notification notification);
    }
}
