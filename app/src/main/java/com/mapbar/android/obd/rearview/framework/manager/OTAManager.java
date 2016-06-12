package com.mapbar.android.obd.rearview.framework.manager;

/**
 * Created by tianff on 2016/6/12.
 */
public class OTAManager {
//    public void setPushData(int type, int state, String userId, String token) {
//        // 日志
//        if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
//            Log.d(LogTag.PUSH, " -->> 推送userManager收到");
//        }
//        switch (type) {
//            case 0:
//                if (state == 1)
//                    showRegQr(scan_succ);
//                break;
//            case 1:
//                if (state == 1 || state == 3) { //注册成功
//                    showRegQr(reg_succ);
//                    isPush = false;//推送成功
//                    // 更新本地用户信息
//                    if (userId != null && token != null) {
//                        // 日志
//                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
//                            Log.d(LogTag.OBD, "userId -->> " + userId + " token--->" + token);
//                            Log.d(LogTag.OBD, "当前userId -->>" + UserCenter.getInstance().getCurrentIdAndType().userId);
//                            Log.d(LogTag.OBD, "当前token -->>" + UserCenter.getInstance().getCurrentUserToken());
//                            Log.d(LogTag.OBD, "当前imei -->>" + Utils.getImei());
//                        }
//                        boolean isUpdata = UserCenter.getInstance().UpdateUserInfoByRemoteLogin(userId, null, token, "zs");
//                        if (isUpdata) {
//                            //远程查询车辆信息
//                            Manager.getInstance().queryRemoteUserCar();
//                        } else {
//                            //显示二维码
//                            showRegQr(reg_info);
//                            // 日志
//                            if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
//                                Log.d(LogTag.PUSH, " -->>更新本地用户信息失败 ");
//                            }
//                        }
//                    }
//
//                } else if (state == 2) {
//                    showRegQr(reg_info);
//                }
//
//        }
}
