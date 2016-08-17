package com.mapbar.android.obd.rearview.modules.tirepressure.contract;

import com.mapbar.android.obd.rearview.lib.mvp.IMvpView;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;
import com.mapbar.android.obd.rearview.modules.tirepressure.model.TirePressure4ViewModel;

/**
 * 车辆数据页
 * Created by zhangyunfei on 16/8/3.
 */
public interface ITirePressureView extends IMvpView, IPermissionAlertViewAble {

    /**
     * 显示单一胎压样式。
     */
    void showTirePresstureSingle(TirePressure4ViewModel[] tirePressureArray);

    /**
     * 隐藏单一胎压样式。
     */
    void hideTirePresstureSingleView();

    /**
     * 显示 四胎压样式。数组里显示4个胎压，分别对应，左上，右上，左下，右下
     *
     * @param tirePressureArray
     */
    void showTirePresstureFour(TirePressure4ViewModel[] tirePressureArray);

    /**
     * 隐藏 四胎压样式。
     */
    void hideTirePresstureFoureView();


}
