package com.mapbar.android.obd.rearview.modules.tirepressure;

/**
 * Created by zhangyunfei on 16/8/17.
 */
public class TirePressureManager {
    private TirePressureDataEventDispatcher tirePressureDataEventDispatcher;

    public TirePressureManager() {
        tirePressureDataEventDispatcher = new TirePressureDataEventDispatcher();
    }

    public TirePressureDataEventDispatcher getTirePressureDataEventDispatcher() {
        return tirePressureDataEventDispatcher;
    }

    public void clear() {
        if (tirePressureDataEventDispatcher != null)
            tirePressureDataEventDispatcher.stop();
    }
}
