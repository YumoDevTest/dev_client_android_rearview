package com.mapbar.android.obd.rearview.framework.ixintui;

import com.mapbar.android.obd.rearview.modules.common.MyApplication;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.Config;

/**
 * 存放 PushToken
 * 2016-08-16 张云飞，从单纯的静态变量存储 改为 存放在 application的Session里存储
 * Created by THINKPAD on 2016/3/8.
 */
public class AixintuiConfigs {

    /**
     * 爱心推的appkey
     * <p/>2062762211
     * 测试 :
     * 正式 :1822600920
     */
    public static final int AIXINTUI_APPKEY = Config.DEBUG ? 1822600920 : 1822600920;

    private static final String KEY_AIXINTUI_PUSH_TOKEN = "KEY_AIXINTUI_PUSH_TOKEN";
    private static final String TAG = "PUSh";

    public static String getPushToken() {
        return MyApplication.getInstance().getSession().getString(KEY_AIXINTUI_PUSH_TOKEN, null);
    }

    public static void setPushToken(String push_token) {
        MyApplication.getInstance().getSession().put(KEY_AIXINTUI_PUSH_TOKEN, push_token);
        LogUtil.d(TAG, "## 记录 push token=" + push_token);
    }
}
