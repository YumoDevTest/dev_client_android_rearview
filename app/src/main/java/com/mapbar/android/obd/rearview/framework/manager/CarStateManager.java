package com.mapbar.android.obd.rearview.framework.manager;

import com.mapbar.android.obd.rearview.framework.control.SDKListenerManager;
import com.mapbar.obd.CarStatusData;
import com.mapbar.obd.Manager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuyy on 2016/5/11.
 */
public class CarStateManager extends OBDManager {
    private static final String CMD_GET_STATUS_DATA = "AT@STG0001\r";
    public Timer mTimer;
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

    /**
     * 获取CarStateManager单例
     *
     * @return CarStateManager实例
     */
    public static CarStateManager getInstance() {
        return (CarStateManager) OBDManager.getInstance(CarStateManager.class);
    }



    public void tryToGetData() {
        Manager.getInstance().sendCustomCommandRequest(CMD_GET_STATUS_DATA);
    }

    /**
     * 获取车辆状态信息
     * @return {@link CarStatusData}车辆状态信息
     */
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

    /**
     * 开始刷新车辆状态
     */
    public void startRefreshCarState() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                tryToGetData();
            }
        }, 1000, 1000);
    }

    /**
     * 停止刷新车辆状态
     */
    public void stopRefreshCarState() {
        if (mTimer != null) {
            mTimer.cancel();
        }

    }
}
