package com.mapbar.android.obd.rearview.modules.maintenance.model;

/**
 * 保养数据bean类
 * Created by zhangyh on 2016/9/23.
 */
public class MaintenanceData {
    public String totalMileage;
    public String lastMaintenanceMileage;
    public String purchaseDate;
    public String lastMaintenanceDate;

    public MaintenanceData(String totalMileage, String lastMaintenanceMileage, String purchaseDate, String lastMaintenanceDate) {
        this.totalMileage = totalMileage;
        this.lastMaintenanceMileage = lastMaintenanceMileage;
        this.purchaseDate = purchaseDate;
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public MaintenanceData() {
    }
}
