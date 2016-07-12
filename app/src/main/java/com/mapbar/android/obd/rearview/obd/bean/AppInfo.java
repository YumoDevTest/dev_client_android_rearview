package com.mapbar.android.obd.rearview.obd.bean;

import java.io.Serializable;

/**
 * Created by wanghw on 2016/7/8.
 */
public class AppInfo implements Serializable {

    private String description;
    private int version_no;
    private String apk_path;
    private String name;
    private String package_name;
    private String icon_path;
    private String app_id;
    private String version_id;
    private int size;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersion_no() {
        return version_no;
    }

    public void setVersion_no(int version_no) {
        this.version_no = version_no;
    }

    public String getApk_path() {
        return apk_path;
    }

    public void setApk_path(String apk_path) {
        this.apk_path = apk_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getIcon_path() {
        return icon_path;
    }

    public void setIcon_path(String icon_path) {
        this.icon_path = icon_path;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getVersion_id() {
        return version_id;
    }

    public void setVersion_id(String version_id) {
        this.version_id = version_id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
