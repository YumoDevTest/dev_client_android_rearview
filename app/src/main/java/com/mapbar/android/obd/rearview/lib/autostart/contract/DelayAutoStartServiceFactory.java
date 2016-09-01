package com.mapbar.android.obd.rearview.lib.autostart.contract;

import com.mapbar.android.obd.rearview.lib.autostart.contract.DelayAutoStartService;
import com.mapbar.android.obd.rearview.lib.autostart.impl.DiruiteDelayAutoStartService;

/**
 * 根据当前编译环境构建不同对应的 自启动处理器
 * Created by zhangyunfei on 16/9/1.
 */
public class DelayAutoStartServiceFactory {
    private static DelayAutoStartService autostartHandler;

    private DelayAutoStartServiceFactory() {
    }

    public static DelayAutoStartService getAutostartHandler() {
        if (autostartHandler == null) {
            autostartHandler = new DiruiteDelayAutoStartService();
        }
        return autostartHandler;
    }
}
