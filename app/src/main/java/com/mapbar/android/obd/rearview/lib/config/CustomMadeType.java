package com.mapbar.android.obd.rearview.lib.config;

/**
 * 定制模式
 * Created by zhangyunfei on 16/9/1.
 */
public class CustomMadeType {
    private static final String TAG = "CustomMadeType";

    private CustomMadeType() {
    }

    public static int CUSTOM_MADE_TYPE_COMMON = 0;
    public static int CUSTOM_MADE_TYPE_DAXUN = 1;
    public static int CUSTOM_MADE_TYPE_WEISHITE = 2;
    public static int CUSTOM_MADE_TYPE_DIRUITE = 3;

    public static void printLog() {
        print(Constants.CUSTOM_MADE_TYPE);
    }

    private static void print(int customMadeType) {
        String made = "";//定制模式
        if (customMadeType == CUSTOM_MADE_TYPE_COMMON) {
            made = "通用版";
        } else if (customMadeType == CUSTOM_MADE_TYPE_DAXUN) {
            made = "达讯定制版";
        } else if (customMadeType == CUSTOM_MADE_TYPE_WEISHITE) {
            made = "威仕特定制版";
        } else if (customMadeType == CUSTOM_MADE_TYPE_DIRUITE) {
            made = "迪瑞特定制版";
        }
        android.util.Log.d(TAG, String.format("## 当前版本定制化模式：%s", made));
    }
}
