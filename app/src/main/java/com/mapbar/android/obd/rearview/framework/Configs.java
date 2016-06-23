package com.mapbar.android.obd.rearview.framework;

public class Configs {

    // OBD产品标识
    public final static String OBD_ANDROID = "obd_android";
    public final static String FILE_PATH = "/mapbar/obd";
    public final static String WX_APPID = "wxf35fb5024de20c47";

    /**
     * 是否使用内网
     */
    public final static boolean IS_USE_INTERNAT_HOST = false;


    /**
     * 微信注册
     */
    public static final String URL_REG_INFO = IS_USE_INTERNAT_HOST ? "http://weixintest.mapbar.com/obdWechat/userRegister?" : "http://weixin.mapbar.com/obd/userRegister?";

    /**
     * redirect_uri
     */
    public static final String URL_REG1 = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WX_APPID;
    public static final String URL_REG2 = "&response_type=code&scope=snsapi_userinfo&state=obdWechat#wechat_redirect";
    /**
     * 微信获取vin的url
     */
    public final static String URL_BIND_VIN = IS_USE_INTERNAT_HOST ? "http://weixintest.mapbar.com/obdWechat/vinCollector?pushToken=" : "http://weixin.mapbar.com/obd/vinCollector?pushToken=";

    public final static boolean TEST_SERIALPORT = false;//true false
    public final static String BT_TYPE = "7";

}
