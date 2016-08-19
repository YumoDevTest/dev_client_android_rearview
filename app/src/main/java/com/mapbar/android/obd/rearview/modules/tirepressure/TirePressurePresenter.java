package com.mapbar.android.obd.rearview.modules.tirepressure;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.lib.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.permission.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.model.PermissionChangedEvent;
import com.mapbar.android.obd.rearview.modules.tirepressure.contract.ITirePressureView;
import com.mapbar.android.obd.rearview.modules.tirepressure.model.TirePressure4ViewModel;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.RealTimeDataTPMS;
import com.mapbar.obd.RealTimeDataTPMSAll;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 胎压数据，呈现器
 * Created by zhangyunfei on 16/8/3.
 */
public class TirePressurePresenter extends BasePresenter<ITirePressureView> implements TirePressureDataEventDispatcher.TirePressureDataEventHandler {
    private PermissionManager permissionManager;
    private TirePressureDataEventDispatcher tirePressureDataEventDispatcher;
    private TirePressure4ViewModel[] tirePressureViewModels;
    private boolean isHaveTireperssturePermission = false;

    public TirePressurePresenter(ITirePressureView view) {
        super(view);
        permissionManager = LogicFactory.createPermissionManager(getView().getContext());

        //先隐藏所有的胎压视图
        getView().hideTirePresstureFoureView();
        getView().hideTirePresstureSingleView();

        EventBusManager.register(this);

        tirePressureDataEventDispatcher = new TirePressureDataEventDispatcher(this);
        tirePressureDataEventDispatcher.start();

    }

    public void clear() {
        if (tirePressureDataEventDispatcher != null) {
            tirePressureDataEventDispatcher.stop();
            tirePressureDataEventDispatcher = null;
        }
        permissionManager = null;
        EventBusManager.unregister(this);
    }

    /**
     * 检查胎压权限
     */
    public void checkPermission() {
        //检查是否有胎压权限，如果有，则显示胎压
        PermissionManager.PermissionResult result1 = permissionManager.checkPermission(PermissionKey.PERMISSION_TIRE_PRESSURE);
        if (result1.isValid) {
            isHaveTireperssturePermission = true;
        } else {
            isHaveTireperssturePermission = false;
            //隐藏所有的胎压视图
            getView().hideTirePresstureFoureView();
            getView().hideTirePresstureSingleView();
        }
    }

    /**
     * 当本地权限发生变化时，这是一个Eventbus订阅者
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PermissionChangedEvent event) {
        checkPermission();
    }


    /**
     * 重用model
     *
     * @return
     */
    @NonNull
    private TirePressure4ViewModel[] makeTirePressureViewModels() {
        if (tirePressureViewModels == null) {
            tirePressureViewModels = new TirePressure4ViewModel[4];
            for (int i = 0; i < tirePressureViewModels.length; i++) {
                tirePressureViewModels[i] = new TirePressure4ViewModel();
            }
        }
        return tirePressureViewModels;
    }

    /**
     * 当收到 精确胎压数据
     *
     * @param realTimeDataTPMSAll
     */
    @Override
    public void onReceiveTirePressureFromImmediate(RealTimeDataTPMSAll realTimeDataTPMSAll) {
        if (!isHaveTireperssturePermission)
            return;
        if (realTimeDataTPMSAll == null || realTimeDataTPMSAll.m_tpmsData.length != 4)
            return;
        TirePressure4ViewModel[] tirePressureViewModels = makeTirePressureViewModels();
        for (int i = 0; i < realTimeDataTPMSAll.m_tpmsData.length; i++) {
            RealTimeDataTPMS item = realTimeDataTPMSAll.m_tpmsData[i];
            TirePressure4ViewModel t = tirePressureViewModels[i];
            t.tirePressure = item.attPa;
            t.itreTemprature = item.attDegree;
            t.isWarning = item.attStatus0 != 0 ||
                    item.attStatus1 != 0 ||
                    item.attStatus2 != 0 ||
                    item.attStatus3 != 0 ||
                    item.attStatus4 != 0 ||
                    item.attStatus5 != 0 ||
                    item.attStatus6 != 0 ||
                    item.attStatus7 != 0;

//            LogUtil.i("TIRE", "## 显示胎压： " + item.toString());
        }
        //0左前轮、1右前轮、2左后轮、3右后轮
        getView().showTirePresstureFour(tirePressureViewModels);
        getView().hideTirePresstureSingleView();
    }


    /**
     * 当收到，算法胎压数据
     *
     * @param realTimeDataTPMSAll
     */
    @Override
    public void onReceiveTirePressureFromIndirect(RealTimeDataTPMSAll realTimeDataTPMSAll) {
        if (!isHaveTireperssturePermission)
            return;
        if (realTimeDataTPMSAll == null || realTimeDataTPMSAll.m_tpmsData.length != 4)
            return;
        TirePressure4ViewModel[] tirePressureViewModels = makeTirePressureViewModels();
        for (int i = 0; i < realTimeDataTPMSAll.m_tpmsData.length; i++) {
            RealTimeDataTPMS item = realTimeDataTPMSAll.m_tpmsData[i];
            TirePressure4ViewModel t = tirePressureViewModels[i];
            t.isWarning = item.attStatus5 != 0;
//            LogUtil.i("TIRE", "## 显示胎压： " + item.toString());
        }
        //0左前轮、1右前轮、2左后轮、3右后轮
        getView().showTirePresstureSingle(tirePressureViewModels);
        getView().hideTirePresstureFoureView();
    }
}
