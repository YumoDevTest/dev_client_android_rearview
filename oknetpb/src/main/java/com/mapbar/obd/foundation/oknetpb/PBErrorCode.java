package com.mapbar.obd.foundation.oknetpb;

/**
 * 网络返回的错误码
 * 从 pb 协议反馈的
 * Created by zhangyunfei on 16/8/9.
 */

public class PBErrorCode {
    private PBErrorCode() {
    }

    public static final int OK = 200;//	1．成功	调用成功
    public static final int DECODE_FAILURE = 601;//	1．解密错误！服务端异常或输入流异常！2.请求参数为空	输入不合法,请求参数不对(数据的合法性验证不通过)
    public static final int NOT_FOUND = 207;//	1．无该产品	查询成功但无数据
    public static final int FAILURE = 506;//	1．调用失败	服务异常
}