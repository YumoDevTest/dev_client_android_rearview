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

    private static RealTimeData realTimeData;


    /**
     * 数据更新的SDK事件回调
     *
     * @param event 事件的id
     * @param o     事件携带的数据
     */
    @Override
    public void onEvent(int event, Object o) {
        switch (event) {
            case Manager.Event.dataUpdate:
                realTimeData = (RealTimeData) o;
                baseObdListener.onEvent(EVENT_OBD_CAR_DATA_UPDATA, o);
        }

    }

    /**
     * 获取单次行程OBD实时数据信息
     * return 单次行程OBD实时数据信息
     */
    public RealTimeData getRealTimeData() {
        realTimeData = Manager.getInstance().getRealTimeData();
        return realTimeData;
    }


}
