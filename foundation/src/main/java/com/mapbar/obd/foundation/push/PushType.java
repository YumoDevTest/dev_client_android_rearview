package com.mapbar.obd.foundation.push;

/**
 * 推送消息包含的 type类型
 * Created by zhangyunfei on 16/7/27.
 */
public class PushType {

    public static final int SCAN_OK = 0;
    public static final int SCAN_REGISTER = 1;
    public static final int SCAN_BIND_VIN = 2;
    public static final int SCAN_BIND_VIN_CODE = 3;
    public static final int BUY = 4;


    private PushType() {
    }


/*
* 0 扫码
* 1 注册
* 2 绑定vin
* 3 绑定vin扫码
* 4 购买
* */
}
