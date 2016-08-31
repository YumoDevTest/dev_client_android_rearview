package com.mapbar.android.obd.rearview.lib.vin;

import android.text.TextUtils;

import com.mapbar.android.log.Log;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.Manager;
import com.mapbar.obd.OtaSpecial;

/**
 * VIN  管理器
 * Created by zhangyunfei on 16/8/26.
 */
public class VinManager {

    private static final String TAG = "VinManager";

    /**
     * 从车辆串口读取VIN
     *
     * @return
     */
    public String getVinFromCar() {
        OtaSpecial ota = Manager.getInstance().getOtaSpecial();
        String vin = null;
        if (ota == null) {
            vin = null;
        } else {
            vin = ota.vin;
        }
        LogUtil.d(TAG, String.format("## 从车辆读取 VIN = %s", vin));
        return vin;
    }

    /**
     * 获得用户自己填写的VIN
     *
     * @return
     */
    public String getVinFromManual() {
        String manualVin = Manager.getInstance().getGetObdVinManual();
        LogUtil.d(TAG, String.format("## 获得用户自己填写的VIN = %s", manualVin));
        return manualVin;
    }

    /**
     * 获得vin，如果无法从车辆获取则 返回用户填写的
     *
     * @return
     */
    public String getVin() {
        String vinFromCar = getVinFromCar();
        if (!TextUtils.isEmpty(vinFromCar))
            return vinFromCar;
        return getVinFromManual();
    }

}
