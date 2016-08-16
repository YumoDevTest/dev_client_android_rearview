package com.mapbar.android.obd.rearview.modules.cardata;

import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;

/**
 * 胎压 业务类
 * Created by zhangyunfei on 16/8/3.
 */
public class TirePressureManager {

    public TirePressureManager() {

        OBDSDKListenerManager.getInstance().setSdkListener(mListenrer);
    }

    private OBDSDKListenerManager.SDKListener mListenrer = new OBDSDKListenerManager.SDKListener() {
        @Override
        public void onEvent(int event, Object o) {
            super.onEvent(event, o);
        }
    };

    /**
     * 获得4轮胎压
     *
     * @return
     */
    public TirePressureBean[] getTirePressures() {
        TirePressureBean[] tirePressureBeenArray = new TirePressureBean[4];
        tirePressureBeenArray[0] = new TirePressureBean("1.4", "30", false);
        tirePressureBeenArray[1] = new TirePressureBean("1.3", "30", false);
        tirePressureBeenArray[2] = new TirePressureBean("30.9", "30", true);
        tirePressureBeenArray[3] = new TirePressureBean("1", "3", false);
        return tirePressureBeenArray;
    }

    public boolean isTirePressuresOK() {
        return true;
    }

    public void clear() {
        OBDSDKListenerManager.getInstance().releaseSdkListener(mListenrer);
    }
}
