package com.mapbar.android.obd.rearview.lib.push;

import java.io.Serializable;

/**
 * 推送消息包含的 的数据状态
 * Created by zhangyunfei on 16/7/27.
 */
public class PushState implements Serializable {

    public static final int SUCCESS = 1;
    public static final int FAILURE = 2;
    public static final int REGISTERED = 3;


    private PushState() {
    }
/*
    扫码时：1成功
    注册时：1成功 2失败 3已注册
    绑定时：1成功 2失败
*/

}
