package com.mapbar.android.obd.rearview.lib.ota;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.bean.QRInfo;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.obd.CarDetail;
import com.mapbar.obd.Firmware;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.Manager;
import com.mapbar.obd.OtaSpecial;
import com.mapbar.obd.UserCar;

import java.io.File;

/**
 * Created by tianff on 2016/6/12.
 */
public class OTAManager extends OBDManager {

    boolean test = true;//false
    private Firmware firmware;
    private Firmware.VersionInfo versionInfo;

    public OTAManager() {
        super();
    }

    /**
     * 获取UserCenterManager单例
     *
     * @return UserCenterManager单例
     */
    public static OTAManager getInstance() {
        return (OTAManager) OBDManager.getInstance(OTAManager.class);
    }

    public void setPushData(int type, int state, String userId, String token) {
        /**
         * type  0 扫码  1 注册  2 绑定vin 3 vin扫码
         * state 1 成功  2 失败  3 已注册
         */
        // 日志
        if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
            Log.d(LogTag.PUSH, " -->> 推送userManager收到");
        }
        switch (type) {
            case 3:
                if (state == 1) {
                    showRegQr(scan_succ);
                }
                break;
            case 2:
                if (state == 1) {

                    Manager.getInstance().queryRemoteUserCar();
                    baseObdListener.onEvent(EVENT_OBD_USER_BINDVIN_SUCC, null);
                } else {
                    baseObdListener.onEvent(EVENT_OBD_USER_BINDVIN_FAILED, null);
                }
                break;

        }
    }

    private void showRegQr(final String content) {
        QRInfo qrInfo = null;
        if (AixintuiConfigs.getPushToken() != null) {
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, "-->> 弹出二维码");
            }

            String url = Configs.URL_BIND_VIN + AixintuiConfigs.getPushToken();
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, "url -->> " + url);
            }
            if (qrInfo == null) {
                qrInfo = new QRInfo();
            }
            qrInfo.setUrl(url);
            qrInfo.setContent(content);
            baseObdListener.onEvent(EVENT_OBD_OTA_SCANVIN_SUCC, qrInfo);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showRegQr(content);
                }
            }, 200);

        }
    }

    /**
     * 检测固件版本,vin不能为空
     */
    public void checkVinVersion(Context mContext) {
        OtaSpecial ota = Manager.getInstance().getOtaSpecial();
        String manualVin = Manager.getInstance().getGetObdVinManual();

        Log.e(LogTag.OBD, "whw checkVinVersion isV3SpecialOta==" + Manager.getInstance().isV3SpecialOta());

        if (ota != null) {
            Log.e(LogTag.OBD, "whw checkVinVersion ota.vin==" + ota.vin + "==manualVin==" + manualVin);
        } else {
            Log.e(LogTag.OBD, "whw checkVinVersion ota == null");
        }

        if (((ota != null && TextUtils.isEmpty(ota.vin)) && TextUtils.isEmpty(manualVin)) && !Manager.getInstance().isV3SpecialOta()) {
            QRInfo qrInfo = new QRInfo();
            qrInfo.setContent("请扫描填写车辆识别号来扩展此页的\r车辆状态和控制功能");
            qrInfo.setUrl(Configs.URL_BIND_VIN + AixintuiConfigs.getPushToken());
            baseObdListener.onEvent(EVENT_OBD_OTA_NEED_VIN, qrInfo);
        } else {
            checkVersion(mContext);
        }
    }

    /**
     * 检查固件版本,vin可为空
     *
     * @param mContext
     */
    public void checkVersion(Context mContext) {
        Log.e(LogTag.OBD, "whw checkVersion ==");
        LocalUserCarResult result = Manager.getInstance().queryLocalUserCar();
        if (result != null) {
            Log.e(LogTag.OBD, "whw checkVersion result != null ==");
            UserCar car = result.userCars == null || result.userCars.length == 0 ? null : result.userCars[0];
            CarDetail cur = Manager.getInstance().getCarDetailByCarInfo(car);
            if (cur != null) {
                Log.e(LogTag.OBD, "whw checkVersion cur != null ==");
                firmware = Firmware.getInstance(mContext);
                if (cur.firstBrand == null || cur.carModel == null || cur.generation == null) {
                    Log.e(LogTag.OBD, "OTAManager中car.firstBrand等参数为空 null ==");
                    return;
                }

                firmware.initParma(cur.firstBrand.trim(), cur.carModel.trim(), cur.generation.trim());
//                firmware.initParma("11111111", "11111111", "11111111");
                firmware.checkNewVersion(Configs.BT_TYPE);
            }
        }
    }

    @Override
    public void onSDKEvent(int event, Object o) {
//        Log.d(LogTag.OBD, "whw OTAManager.onSDKEvent event ==" + event);
        switch (event) {
            case Manager.Event.firmwareOTA:
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, "whw -->> 请求固件新版本的回调");
                }
                handleFirmware(event, o);
                break;
        }
    }

    /**
     * 处理
     *
     * @param event
     * @param data
     */
    private void handleFirmware(int event, Object data) {
        Firmware.EventData e = (Firmware.EventData) data;
        String version = firmware.getVersion();
//        String version = "test";
//            version = Manager.getInstance().getOBDVersion();
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
//            Log.d(LogTag.OBD, " -->> 请求固件新版本的回调version ==" + version.replace("\r", "") + "==end");
        }
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.e(LogTag.OBD, " -->>  e.getRspCode()-->> " + e.getRspCode());
        }
//        if(test){
        versionInfo = firmware.getVersionInfo();//TODO null
//            versionInfo.setVersion("ELM327 v1.5MAPBAR627 v1.6.1036");
//            downLoadFirmware(version);
//            downLoadFirmware("ELM327 v1.5MAPBAR627 v1.6.1036");
//            return;
//        }
        switch (e.getRspCode()) {
            case Firmware.EventData.RSPCODE_RESULT_OK://TODO 在有新版本的情况下直接去下载新版本

//                versionInfo = firmware.getVersionInfo();
//                versionInfo.setVersion("ELM327 v1.5MAPBAR627 v1.6.1036");
                downLoadFirmware(version);
//                downLoadFirmware("ELM327 v1.5MAPBAR627 v1.6.1036");//ELM327 v1.5MAPBAR627 v1.6.1036HONDA.0000.0000.0001
                break;
            default:
                break;
        }
    }

    private void downLoadFirmware(String version) {
        Firmware.UpgradeCallback fu = new Firmware.UpgradeCallback() {
            @Override
            public void onFlashResult(int statusCode, File file) {
            }

            @Override
            public void onFlashProgress(int bytesWritten, int totalSize) {
            }

            @Override
            public void onDownResult(int rspcode, final File file) {
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> 下载bin结果-->>" + rspcode);
                }
                switch (rspcode) {
                    case Firmware.UpgradeCallback.STATUSCODE_DOWN_OK:
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //TODO bin下载成功 目前两个问题
                                //1、从数据库读取不带版本号
                                //2、请求服务器结果为token失效
                                baseObdListener.onEvent(OBDManager.EVENT_OBD_OTA_HAS_NEWFIRMEWARE, file);
                            }
                        }, 1000);

                        break;
                    case Firmware.UpgradeCallback.STATUSCODE_DOWN_FAILED:
                        break;
                    case Firmware.UpgradeCallback.STATUSCODE_DOWN_NOCONNECTIVITY:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onDownProgress(int bytesWritten, int totalSize) {
                int pb = (int) (Float.intBitsToFloat(bytesWritten) / Float.intBitsToFloat(totalSize) * 100f);
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " 下载bin进度-->>" + pb);
                }
            }
        };
        if (firmware != null && version != null && versionInfo != null) {
            Log.d(LogTag.OTA, " -->> down(versionInfo, null);-->> ");
            firmware.down(versionInfo, fu);
        }
    }

    /**
     * 刷固件
     *
     * @param mContext
     * @param callback
     */
    public void upgrade(final Context mContext, final Firmware.UpgradeCallback callback) {
        Firmware.UpgradeCallback fu = new Firmware.UpgradeCallback() {
            @Override
            public void onFlashResult(final int statusCode, final File file) {
                Log.d(LogTag.OTA, " -->> 升级结果 statusCode==" + statusCode);
                switch (statusCode) {
                    case Firmware.UpgradeCallback.STATUSCODE_FLASH_OK:
                        Log.d(LogTag.OTA, " -->> 升级成功 == ");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Firmware.getInstance(mContext).delBin();
                                // FileUtils.deleteGeneralFile(getLocalHtmlUrl());
                            }
                        });
                        break;
                    case Firmware.UpgradeCallback.STATUSCODE_FLASH_FAILED:
                        break;
                    default:
                        break;
                }
                callback.onFlashResult(statusCode, file);
            }

            @Override
            public void onFlashProgress(int bytesWritten, int totalSize) {
                Log.d(LogTag.OTA, " -->> 升级进度 bytesWritten==" + bytesWritten + "== 总大小totalSize==" + totalSize);
                callback.onFlashProgress(bytesWritten, totalSize);
            }

            @Override
            public void onDownResult(int statusCode, File file) {
                Log.d(LogTag.OTA, " -->> 升级下载结果 statusCode==" + statusCode + "== 下载路径==" + file.getAbsolutePath());
                callback.onDownResult(statusCode, file);
            }

            @Override
            public void onDownProgress(int bytesDown, int totalSize) {
                Log.d(LogTag.OTA, " -->> 升级下载进度 bytesDown==" + bytesDown + "== 总大小totalSize==" + totalSize);
                callback.onDownProgress(bytesDown, totalSize);
            }
        };

        firmware.upgrade(versionInfo, fu);
    }

    /**
     * 判断固件是否需要强制升级
     *
     * @param context
     * @return true 需要，false 不需要
     */
    public boolean isForceUpdate(Context context) {
        return Firmware.getInstance(context).isForceUpdate();
    }

}