package com.mapbar.android.obd.rearview.modules.permission.contract;

/**
 * 具有能力 弹出试用提醒
 * Created by zhangyunfei on 16/8/4.
 */
public interface IPermissionAlertViewAble {
    /**
     * 展示 隐藏 试用到期提醒
     * 根节点是个framentView,可以放入 授权提醒的视图作为浮层
     *
     * @param numberOfDay
     */
    void showPermissionAlertView_FreeTrial(boolean isExpired, int numberOfDay);

    /**
     * 隐藏 试用到期提醒
     */
    void hidePermissionAlertView_FreeTrial();
}
