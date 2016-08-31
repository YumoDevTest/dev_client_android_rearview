package com.mapbar.android.obd.rearview.lib.ota;

import com.mapbar.obd.Firmware;
import com.mapbar.obd.Manager;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.SessionInfo;
import com.mapbar.obd.serial.ota.FirmwareUpdateCallback;
import com.mapbar.obd.serial.ota.FirmwareUpdateManager;

import java.io.File;

/**
 * Created by zhangyunfei on 16/8/27.
 */
public class FirmwareWriter {

    public void writeFirmware(File file, final FirmwareUpdateCallback firmwareUpdateCallback) {
//        if (Manager.getInstance().isBluetoothConnected()) {
        if (Manager.getInstance().isDataReaderThreadRunning()) {
            Manager.getInstance().stopReadThreadForUpgrage();
        }
        ObdContext.getInstance().getSerialPortManager().cleanup();
        beginUpgradeBox(file, firmwareUpdateCallback);
//        } else {
//            Manager.getInstance().prepareUpgradeBox();
//            final SessionInfo info = SessionInfo.getCurrent();
//            if (!Manager.getInstance().openDevice(info.obdMac)) {
//                //
//            }
//        }
    }

    private void beginUpgradeBox(final File file, final FirmwareUpdateCallback firmwareUpdateCallback) {
        FirmwareUpdateManager firmwareUpdateManager;
        firmwareUpdateManager = ObdContext.getInstance().getFirmwareUpdateManager();
        firmwareUpdateManager.updateFirware(file, firmwareUpdateCallback);
    }


//    FirmwareUpdateCallback mFirmwareUpdateCallback = new FirmwareUpdateCallback() {
//        @Override
//        public void onStart(File filePath) {
//            android.util.Log.i("TAG", "### ============ start!   " + filePath);
//            if (null != mUpgradeCallback)
//                mUpgradeCallback.onFlashProgress(1, 100);
//        }
//
//        @Override
//        public void onProgress(int progress) {
//            android.util.Log.i("TAG", "### ============ " + progress);
//            if (null != mUpgradeCallback)
//                mUpgradeCallback.onFlashProgress(progress, 100);
//        }
//
//        @Override
//        public void onError(Exception exception) {
//            android.util.Log.e("TAG", "### ============ERROR: " + exception.getMessage());
//            if (mUpgradeCallback != null)
//                mUpgradeCallback.onFlashResult(Firmware.UpgradeCallback.STATUSCODE_FLASH_FAILED, file);
//        }
//
//        @Override
//        public void onComplete() {
//            android.util.Log.i("TAG", "### ============ complete");
//            if (null != mUpgradeCallback)
//                mUpgradeCallback.onFlashResult(Firmware.UpgradeCallback.STATUSCODE_FLASH_OK, file);
//        }
//    };
}
