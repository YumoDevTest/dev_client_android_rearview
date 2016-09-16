//package com.mapbar.android.obd.rearview.lib.daemon.delaystart;
//
//import com.mapbar.android.obd.rearview.lib.base.CustomMadeType;
//import com.mapbar.android.obd.rearview.lib.daemon.delaystart.contract.DelayAutoStartService;
//import com.mapbar.android.obd.rearview.modules.common.Constants;
//
///**
// * 根据当前编译环境构建不同对应的 自启动处理器
// * Created by zhangyunfei on 16/9/1.
// */
//public class DelayAutoStartServiceFactory {
//    private static DelayAutoStartService autostartHandler;
//
//    private DelayAutoStartServiceFactory() {
//    }
//
//    public static DelayAutoStartService getAutostartHandler() {
//        CustomMadeType.printLog();
//        if (autostartHandler == null) {
//            autostartHandler = createDelayAutoStartService();
//        }
//        return autostartHandler;
//    }
//
//    private static DelayAutoStartService createDelayAutoStartService() {
//        if (Constants.CUSTOM_MADE_TYPE == CustomMadeType.CUSTOM_MADE_TYPE_DIRUITE) {
////            autostartHandler = new DiruiteDelayAutoStartService();
//        }
//        return autostartHandler;
//    }
//}
