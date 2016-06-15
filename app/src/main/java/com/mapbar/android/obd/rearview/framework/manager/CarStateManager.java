package com.mapbar.android.obd.rearview.framework.manager;

import android.os.Handler;

import com.mapbar.android.obd.rearview.framework.common.DecFormatUtil;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.obd.AlarmData;
import com.mapbar.obd.Manager;
import com.mapbar.obd.RealTimeData;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuyy on 2016/5/11.
 */
public class CarStateManager extends OBDManager {
    private static final String CMD_GET_STATUS_DATA = "AT@STG0001\r";
    public Timer mTimer;
    //存储预警纪录
    public ArrayList<String> alarmDatas = new ArrayList<>();
    //临时存储故障码
    private ArrayList<String> errorCode = new ArrayList<>();
    private Handler mHander = new Handler();

    protected CarStateManager() {
        super();
//        sdkListener = new SDKListenerManager.SDKListener() {
//            @Override
//            public void onEvent(int event, Object o) {
//                onSDKEvent(event, o);
//            }
//        };
//        SDKListenerManager.getInstance().setSdkListener(sdkListener);
        //初始化预警纪录数据
        alarmDatas.add("水温");
        alarmDatas.add("电压");
        alarmDatas.add("疲劳");

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
            //  Log.d(LogTag.TEMP, "tryToGetData -->> ");
        }
        Manager.getInstance().sendCustomCommandRequest(CMD_GET_STATUS_DATA);
    }

    @Override
    public void onSDKEvent(int event, Object o) {
        switch (event) {
            case Manager.Event.obdCarStatusgetSucc:
                break;
            case Manager.Event.obdCarStatusgetFailed:
                break;
            case Manager.Event.dataUpdate:
                final RealTimeData realTimeData = (RealTimeData) o;
                if (realTimeData.voltage < 10 && realTimeData.voltage > 15) {
                    String votage = DecFormatUtil.format2dot1(realTimeData.voltage);
                    alarmDatas.set(1, votage);
                }
                if (realTimeData.engineCoolantTemperature < 80) {
                    alarmDatas.set(0, "水温");
                }
                break;

            case Manager.Event.alarm:
                if (o instanceof AlarmData) {
                    AlarmData data = (AlarmData) o;
                    String content = "";
                    int type = data.getType();
                    switch (type) {
                        case Manager.AlarmType.errCode: {
                            // 故障预警
                            content = data.getString();
                            errorCode.add(content);
                            mHander.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!alarmDatas.containsAll(errorCode)) {
                                        alarmDatas.addAll(3, errorCode);
                                    }
                                    errorCode.clear();
                                }
                            }, 1000);
                        }
                        break;
                        case Manager.AlarmType.temperature: {
                            // 水温预警
                            content = String.format(Locale.getDefault(), "%d", data.getInt());
                            alarmDatas.set(0, content);
                        }
                        break;
                        case Manager.AlarmType.voltage: {
                            // 电压预警
                            content = String.format(Locale.getDefault(), "%.1f", data.getFloat());
                            alarmDatas.set(1, content);
                        }
                        break;

                        case Manager.AlarmType.tired: {
                            // 疲劳驾驶预警
                            content = String.format(Locale.getDefault(), "%.1f", data.getFloat());
                            alarmDatas.set(2, content);
                        }
                        break;
                    }
                }
                break;
        }

//        super.onSDKEvent(event, o);

    }

    /**
     * 开始刷新车辆状态;
     */

    public void startRefreshCarState() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                tryToGetData();
            }
        }, 1000, 3000);
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
