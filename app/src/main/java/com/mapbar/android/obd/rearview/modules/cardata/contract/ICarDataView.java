package com.mapbar.android.obd.rearview.modules.cardata.contract;

import com.mapbar.android.obd.rearview.lib.mvp.IMvpView;
import com.mapbar.android.obd.rearview.modules.cardata.TirePressureBean;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAble;

/**
 * 车辆数据页
 * Created by zhangyunfei on 16/8/3.
 */
public interface ICarDataView extends IMvpView, IPermissionAlertViewAble {

    /**
     * 显示单一胎压样式。正常状态
     */
    void showTirePresstureSingleNormal();

    /**
     * 显示单一胎压样式。警告状态
     */
    void showTirePresstureSingleWarning();

    /**
     * 隐藏单一胎压样式。
     */
    void hideTirePresstureSingleView();

    /**
     * 显示 四胎压样式。数组里显示4个胎压，分别对应，左上，左下，右上，右下
     *
     * @param tirePressureArray
     */
    void showTirePresstureFour(TirePressureBean[] tirePressureArray);

    /**
     * 隐藏 四胎压样式。
     */
    void hideTirePresstureFoureView();


}
