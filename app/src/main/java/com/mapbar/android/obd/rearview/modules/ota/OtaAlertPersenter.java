package com.mapbar.android.obd.rearview.modules.ota;

import android.os.Bundle;

import com.mapbar.obd.developkit.ota.FirmwareWriter;
import com.mapbar.obd.foundation.mvp.BasePresenter;
import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.android.obd.rearview.modules.ota.contract.IOtaAlertView;
import com.mapbar.obd.serial.ota.FirmwareUpdateCallback;

import java.io.File;

/**
 * Created by zhangyunfei on 16/8/27.
 */
public class OtaAlertPersenter extends BasePresenter<IOtaAlertView> {
    private String firewware_bin_file;
    private boolean is_fouce_upgreade;
    private FirmwareWriter firmwareWriter;

    public OtaAlertPersenter(IOtaAlertView view, Bundle bundle) {
        super(view, bundle);

        if (!bundle.containsKey("firewware_bin_file"))
            throw new NullPointerException();
        if (!bundle.containsKey("is_fouce_upgreade"))
            throw new NullPointerException();
        firewware_bin_file = bundle.getString("firewware_bin_file");
        is_fouce_upgreade = bundle.getBoolean("is_fouce_upgreade", false);

        firmwareWriter = new FirmwareWriter();
        //判断是否强制升级
        if (is_fouce_upgreade) {
            getView().showView_alert_ForceUpgrade();
        } else {
            getView().showView_alertUpgrade();
        }
    }

    @Override
    public void clear() {

    }

    /**
     * 开始更新固,写固件的过程
     */
    public void beginUpgrade() {
        firmwareWriter.writeFirmware(new File(firewware_bin_file), new FirmwareUpdateCallback() {

            @Override
            public void onStart(File filePath) {
                android.util.Log.i("TAG", "### ============ start!   " + filePath);
                getView().showView_alertProgress(0);
            }

            @Override
            public void onProgress(int progress) {
                android.util.Log.i("TAG", "### ============ " + progress);
                getView().showView_alertProgress(progress);
            }

            @Override
            public void onError(Exception exception) {
                android.util.Log.e("TAG", "### ============ERROR: " + exception.getMessage());
//                getView().alert(exception.getMessage());
                getView().showView_alertFailure();
            }

            @Override
            public void onComplete() {
                android.util.Log.i("TAG", "### ============ complete");
                getView().showView_alertSuccess();
            }
        });
    }

    /**
     * 当点击了 更新完成 按钮
     */
    public void onUpgradeFinish() {
        MyApplication.getInstance().restartApplication();
    }

    public void onRetryUpgrade() {
        beginUpgrade();
    }
}
