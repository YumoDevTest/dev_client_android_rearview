package com.mapbar.android.obd.rearview.obd.util;

/**
 * 一些URL的配置类
 * Created by zhangyunfei on 16/8/9.
 */
public class Urls {
    //权限接口对应的服务端的 URL 的基础地址
    public static final String PERMISSION_BASE_URL = "http://119.255.37.167";

    private Urls() {
    }

    /**
     * 下载本机的权限列表
     */
    public static final String PERMISSION_DOWNLOAD_PERMISSIONS = PERMISSION_BASE_URL + "/h/s/right/get";
}
