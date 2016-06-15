package com.mapbar.android.obd.rearview.framework;

public class Configs {

    // OBD产品标识
    public final static String OBD_ANDROID = "obd_android";
    public final static String FILE_PATH = "/mapbar/obd";
    public final static String WX_APPID = "wxf35fb5024de20c47";

    /**
     * imei
     * pushToken
     * token
     */
    //URL
//    public static final String URL_REG_INFO = "http://weixintest.mapbar.com/obdWechat/userRegister/index?";
    //内网
//    public static final String URL_REG_INFO = "http://weixintest.mapbar.com/obdWechat/userRegister?";
    //外网微信
    public static final String URL_REG_INFO = "http://weixin.mapbar.com/obd/userRegister?";
//    public static final String URL_REG_INFO = "http://weixintest.mapbar.com/obdWechat/userRegister?";
    /**
     * redirect_uri
     */
    public static final String URL_REG1 = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WX_APPID;
    public static final String URL_REG2 = "&response_type=code&scope=snsapi_userinfo&state=obdWechat#wechat_redirect";
    /**
     * 微信获取vin的url
     */
//    public final static String URL_BIND_VIN = "http://weixintest.mapbar.com/obdWechat/vinCollector?pushToken=8125513276403507479";
    public final static String URL_BIND_VIN = "http://weixin.mapbar.com/obd/vinCollector?pushToken=8125513276403507479";
    //有 weixintest.mapbar.com/obdWechat/ 改成 weixin.mapbar.com/obd/

    public final static boolean TEST_SERIALPORT = true;//true false

}
