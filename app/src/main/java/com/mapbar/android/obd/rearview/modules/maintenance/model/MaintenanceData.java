package com.mapbar.android.obd.rearview.modules.maintenance.model;

/**
 * 保养数据bean类
 * Created by zhangyh on 2016/9/23.
 */
public class MaintenanceData {
    private String totalMileage;
    private String lastMaintenanceMileage;
    private String purchaseDate;
    private String lastMaintenanceDate;
    //是否有效时间
    private boolean boolPurchaseDate;
    private boolean boolLastMaintenanceDate;

    public MaintenanceData() {
    }

    public MaintenanceData(String totalMileage, String lastMaintenanceMileage, String purchaseDate, String lastMaintenanceDate) {
        this.totalMileage = totalMileage;
        this.lastMaintenanceMileage = lastMaintenanceMileage;
        this.purchaseDate = purchaseDate;
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public String getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(String totalMileage) {
        this.totalMileage = totalMileage;
    }

    public String getLastMaintenanceMileage() {
        return lastMaintenanceMileage;
    }

    public void setLastMaintenanceMileage(String lastMaintenanceMileage) {
        this.lastMaintenanceMileage = lastMaintenanceMileage;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(String lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public boolean isBoolPurchaseDate() {
        return boolPurchaseDate;
    }

    public void setBoolPurchaseDate(boolean boolPurchaseDate) {
        this.boolPurchaseDate = boolPurchaseDate;
    }

    public boolean isBoolLastMaintenanceDate() {
        return boolLastMaintenanceDate;
    }

    public void setBoolLastMaintenanceDate(boolean boolLastMaintenanceDate) {
        this.boolLastMaintenanceDate = boolLastMaintenanceDate;
    }
}
