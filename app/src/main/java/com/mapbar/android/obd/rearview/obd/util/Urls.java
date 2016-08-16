package com.mapbar.android.obd.rearview.obd.util;

import com.mapbar.android.obd.rearview.BuildConfig;

/**
 * 一些URL的配置类
 * Created by zhangyunfei on 16/8/9.
 */
public class Urls {
    //权限接口对应的服务端的 URL 的基础地址
    public static final String PERMISSION_BASE_URL = BuildConfig.BASE_PERMISSION_URL;

    private Urls() {
    }

    /**
     * 下载本机的权限列表
     */
    public static final String PERMISSION_DOWNLOAD = PERMISSION_BASE_URL + "/h/s/right/get";

    /**
     * 购买链接的url
     */
    public static final String PERMISSION_BUY = PERMISSION_BASE_URL + "/h/p/buy";
}
