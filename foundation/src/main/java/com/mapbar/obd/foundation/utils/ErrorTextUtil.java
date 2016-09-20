package com.mapbar.obd.foundation.utils;

/**
 * 错误文本转换
 * Created by zhangyunfei on 16/9/20.
 */
public class ErrorTextUtil {

    public static class DataCollectFailure {
        public static final int NEED_DECRYPTION = 7;
        public static final int NONE = 0;
        public static final int NO_SUPPORTED_PROTOCOL = 2;
        public static final int OTHER = 3;
        public static final int RPM_INVALID = 1;


        /**
         * 解析 数据准备失败异常码 到错误描述文字
         *
         * @param errorCode
         * @return
         */
        public static String parseToText(Integer errorCode) {
            String msg = "未知错误";
            if (errorCode == null || errorCode == DataCollectFailure.NONE) {
                msg = "未知错误";
            } else if (errorCode == DataCollectFailure.NEED_DECRYPTION) {
                msg = "need decryption";
            } else if (errorCode == DataCollectFailure.NO_SUPPORTED_PROTOCOL) {
                msg = "不支持的协议";
            } else if (errorCode == DataCollectFailure.RPM_INVALID) {
                msg = "转速不能为空";
            } else if (errorCode == DataCollectFailure.OTHER) {
                msg = "其他错误";
            }
            return msg;
        }
    }


}
