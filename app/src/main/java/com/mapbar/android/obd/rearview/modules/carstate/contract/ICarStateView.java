package com.mapbar.android.obd.rearview.modules.carstate.contract;

import com.mapbar.android.obd.rearview.lib.mvp.IMvpView;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;

/**
 * Created by zhangyunfei on 16/8/4.
 */
public interface ICarStateView extends IMvpView, IPermissionAlertViewAble {

    /**
     * 是否显示 车辆状态错误码 的提示语
     * @param isVisiable
     */
    void setCarStateRecordVisiable(boolean isVisiable);

}
