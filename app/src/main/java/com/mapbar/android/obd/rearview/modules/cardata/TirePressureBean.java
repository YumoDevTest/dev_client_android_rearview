package com.mapbar.android.obd.rearview.modules.cardata;

import java.io.Serializable;

/**
 * 胎压
 * Created by zhangyunfei on 16/8/3.
 */
public class TirePressureBean implements Serializable {
    public boolean isWarning;//是否警告
    public String tirePressure;//胎压
    public String itreTemprature;//胎温

    public TirePressureBean() {
    }

    public TirePressureBean(String tirePressure, String itreTemprature, boolean isWarning) {
        this.isWarning = isWarning;
        this.tirePressure = tirePressure;
        this.itreTemprature = itreTemprature;
    }
}
