package com.mapbar.android.obd.rearview.obd.util;

import com.mapbar.android.obd.rearview.BuildConfig;

/**
 * 一些URL的配置类
 * Created by zhangyunfei on 16/8/9.
 */
public class Urls {
    public static final boolean IS_USE_TEST_HOST = false;

    //权限接口对应的服务端的 URL 的基础地址
    public static final String PERMISSION_BASE_URL = BuildConfig.BASE_PERMISSION_URL;

    //微信服务的基础URL
    public static final String WEIXIN_BASE_URL = IS_USE_TEST_HOST ? "http://weixintest.mapbar.com/obdWechat" : "http://weixin.mapbar.com/obd";

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


    /**
     * 微信 绑定vin
     */
    public final static String URL_BIND_VIN = WEIXIN_BASE_URL + "/vinCollector";

}
