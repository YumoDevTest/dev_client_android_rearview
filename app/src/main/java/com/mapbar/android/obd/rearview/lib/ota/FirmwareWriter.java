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
//        if (Manager.create().isBluetoothConnected()) {
        if (Manager.getInstance().isDataReaderThreadRunning()) {
            Manager.getInstance().stopReadThreadForUpgrage();
        }
        ObdContext.getInstance().getSerialPortManager().cleanup();
        beginUpgradeBox(file, firmwareUpdateCallback);
//        } else {
//            Manager.create().prepareUpgradeBox();
//            final SessionInfo info = SessionInfo.getCurrent();
//            if (!Manager.create().openDevice(info.obdMac)) {
//                //
//            }
//        }
    }

    private void beginUpgradeBox(final File file, final FirmwareUpdateCallback firmwareUpdateCallback) {
        FirmwareUpdateManager firmwareUpdateManager;
        firmwareUpdateManager = ObdContext.getInstance().getFirmwareUpdateManager();
        firmwareUpdateManager.updateFirware(file, firmwareUpdateCallback);
    }


}
