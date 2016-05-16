package com.mapbar.android.obd.rearview.framework.manager;

import com.mapbar.android.obd.rearview.framework.control.SDKListenerManager;
import com.mapbar.obd.CarStatusData;
import com.mapbar.obd.Manager;

/**
 * Created by liuyy on 2016/5/11.
 */
public class CarStateManager extends OBDManager {
    private static final String CMD_GET_STATUS_DATA = "AT@STG0001\r";
    private CarStatusData data;

    public CarStateManager() {
        sdkListener = new SDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                onSDKEvent(event, o);
            }
        };
        SDKListenerManager.getInstance().setSdkListener(sdkListener);
    }

    public static CarStateManager getInstance() {
        return (CarStateManager) OBDManager.getInstance(CarStateManager.class);
    }

    public void tryToGetData() {
        Manager.getInstance().sendCustomCommandRequest(CMD_GET_STATUS_DATA);
    }

    public CarStatusData getCarStatusData() {
        return data;
    }

    @Override
    public void onSDKEvent(int event, Object o) {
        switch (event) {
            case Manager.Event.obdCarStatusgetSucc:
                data = (CarStatusData) o;
                break;
            case Manager.Event.obdCarStatusgetFailed:
                break;
        }
        super.onSDKEvent(event, o);
    }

    public void startRefreshCarState() {

    }
}
