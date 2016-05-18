package com.mapbar.android.obd.rearview.framework.manager;

import com.mapbar.android.obd.rearview.framework.control.SDKListenerManager;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.obd.CarStatusData;
import com.mapbar.obd.Manager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 类获取车辆状态（车灯、车窗、车锁、车门、后备箱、天窗）
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


    /**
     * 请求获取最新车辆信息；调用次此方法后，再调用getCarStatusData()方法可以获取最新的车辆状态信息
     */
    public void tryToGetData() {
        // 日志
        if (Log.isLoggable(LogTag.TEMP, Log.DEBUG)) {
            Log.d(LogTag.TEMP, "tryToGetData -->> ");
        }
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
     * 开始刷新车辆状态;
     *
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
