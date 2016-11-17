package com.mapbar.obd.developkit.ota;

import com.mapbar.obd.Manager;
import com.mapbar.obd.ObdContext;
import com.mapbar.obd.serial.ota.FirmwareUpdateCallback;
import com.mapbar.obd.serial.ota.FirmwareUpdateManager;

import java.io.File;

/**
 * Created by zhangyunfei on 16/8/27.
 */
public class FirmwareWriter {

    public void writeFirmware(File file, final FirmwareUpdateCallback firmwareUpdateCallback) {
        if (Manager.getInstance().isDataReaderThreadRunning()) {
            Manager.getInstance().stopReadThreadForUpgrage();
        }
        try {
            //防止 stopReadThreadForUpgrage 未执行完毕
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ObdContext.getInstance().getSerialPortManager().cleanup();

        FirmwareUpdateManager firmwareUpdateManager;
        firmwareUpdateManager = ObdContext.getInstance().getFirmwareUpdateManager();
        firmwareUpdateManager.updateFirware(file, firmwareUpdateCallback);
    }


}
