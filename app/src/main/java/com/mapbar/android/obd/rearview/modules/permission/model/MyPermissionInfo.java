package com.mapbar.android.obd.rearview.modules.permission.model;


/**
 * 权限信息，对应数据库表
 * Created by zhangyunfei on 16/8/9.
 */
//@DatabaseTable(tableName = "MyPermissionInfo")
public class MyPermissionInfo {
//    @DatabaseField(columnName="idpk",generatedId = true)
    private int idpk;

//    @DatabaseField(columnName="productId")
    private String productId;//产品编号

//    @DatabaseField(columnName="producteStatus")
    private int producteStatus;//产品状态

//    @DatabaseField(columnName="deadline")
    private String deadline;//产品过期时间

    public MyPermissionInfo() {
    }

    public MyPermissionInfo(String productId, int producteStatus, String deadline) {
        this.productId = productId;
        this.producteStatus = producteStatus;
        this.deadline = deadline;
    }

    public int getIdpk() {
        return idpk;
    }

    public void setIdpk(int idpk) {
        this.idpk = idpk;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getProducteStatus() {
        return producteStatus;
    }

    public void setProducteStatus(int producteStatus) {
        this.producteStatus = producteStatus;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
