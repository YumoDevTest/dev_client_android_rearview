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
    public static final String URL_REG_INFO = "http://weixintest.mapbar.com/obdWechat/userRegister?";
    /**
     * redirect_uri
     */
    public static final String URL_REG1 = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WX_APPID;
    public static final String URL_REG2 = "&response_type=code&scope=snsapi_userinfo&state=obdWechat#wechat_redirect";
}
