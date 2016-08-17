package com.mapbar.android.obd.rearview.modules.tirepressure.model;

import java.io.Serializable;

/**
 * 胎压
 * Created by zhangyunfei on 16/8/3.
 */
public class TirePressure4ViewModel implements Serializable {
    public boolean isWarning;//是否警告
    public float tirePressure;//胎压
    public float itreTemprature;//胎温

    public TirePressure4ViewModel() {
    }

    public TirePressure4ViewModel(float tirePressure, float itreTemprature, boolean isWarning) {
        this.isWarning = isWarning;
        this.tirePressure = tirePressure;
        this.itreTemprature = itreTemprature;
    }
}
