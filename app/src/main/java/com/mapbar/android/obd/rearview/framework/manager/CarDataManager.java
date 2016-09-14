package com.mapbar.android.obd.rearview.framework.manager;

import com.mapbar.obd.Manager;
import com.mapbar.obd.RealTimeData;

/**
 * 功能：获取单次行程OBD实时数据信息
 * 抛出事件
 * event1：{@link #EVENT_OBD_CAR_DATA_UPDATA},OBD数据更新消息 ;o参数是 {@link RealTimeData}
 */
public class CarDataManager extends OBDManager {
    public static final int EVENT_OBD_CAR_DATA_UPDATA = 0xF10001;

    private RealTimeData realTimeData;

    public CarDataManager() {
        super();
//        sdkListener = new SDKListenerManager.SDKListener() {
//            @Override
//            public void onEvent(int event, Object o) {
//                onSDKEvent(event, o);
//            }
//        };
//        SDKListenerManager.create().addSdkListener(sdkListener);
    }

    /**
     * 获取CarDataManager单例
     *
     * @return CarDataManager实例
     */
    public static CarDataManager getInstance() {
        return (CarDataManager) OBDManager.getInstance(CarDataManager.class);
    }

    public void restartTrip() {
        Manager.getInstance().restoreTrip();
    }

    /**
     * 数据更新的SDK事件回调
     *
     * @param event 事件的id
     * @param o     事件携带的数据
     */
    @Override
    public void onSDKEvent(int event, Object o) {
        switch (event) {
            case Manager.Event.dataUpdate:
                realTimeData = (RealTimeData) o;
        }
//        super.onSDKEvent(event, o);
    }
}
