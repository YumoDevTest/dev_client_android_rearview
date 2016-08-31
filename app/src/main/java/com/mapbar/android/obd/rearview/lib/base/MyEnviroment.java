package com.mapbar.android.obd.rearview.lib.base;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.mapbar.android.obd.rearview.obd.util.LogUtil;

import java.io.File;

/**
 * Created by zhangyunfei on 16/8/31.
 */
public class MyEnviroment {

    private static final String TAG = "MyEnviroment";

    /**
     * 获得 OTA 升级时，bin文件的下载，解压路径
     *
     * @return
     */
    @NonNull
    public static File getOTAFilesDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), "mapbar/binfile");//Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!dir.exists()) {
            boolean isOk = dir.mkdirs();
            if (!isOk)
                LogUtil.e(TAG, "## 创建bin缓存文件夹异常");
        }
        LogUtil.e(TAG, "## OTA文件夹基础路径 = " + dir.getPath());
        return dir;
    }

}
