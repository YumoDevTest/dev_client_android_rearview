package com.mapbar.android.obd.rearview.framework.manager;

import com.mapbar.obd.CarStatusData;
import com.mapbar.obd.Manager;

/**
 * Created by liuyy on 2016/5/11.
 */
public class CarStateManager extends BaseManager {
    private static final String CMD_GET_STATUS_DATA = "AT@STG0001\r";
    private static CarStatusData data;

    public static void tryToGetData() {
        Manager.getInstance().sendCustomCommandRequest(CMD_GET_STATUS_DATA);
    }

    public static CarStatusData getData() {
        return data;
    }

    @Override
    public void onEvent(int event, Object o) {
        super.onEvent(event, o);
        switch (event) {
            case Manager.Event.obdCarStatusgetSucc:
                data = (CarStatusData) o;
                baseObdListener.onEvent(event, o);
                break;
            case Manager.Event.obdCarStatusgetFailed:
                break;
        }
    }
}
