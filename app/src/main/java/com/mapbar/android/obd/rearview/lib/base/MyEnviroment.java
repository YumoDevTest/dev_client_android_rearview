package com.mapbar.android.obd.rearview.lib.base;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.modules.common.MyApplication;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;

import java.io.File;

/**
 * Created by zhangyunfei on 16/8/31.
 */
public class MyEnviroment {

    private static final String TAG = "MyEnviroment";
    private static String PACKAGE_NAME;

    /**
     * 获得 OTA 升级时，bin文件的下载，解压路径
     *
     * @return
     */
    @NonNull
    public static File getOTAFilesDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), getPackageName() + "/binfile");//Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!dir.exists()) {
            boolean isOk = dir.mkdirs();
            if (!isOk)
                LogUtil.e(TAG, "## 创建bin缓存文件夹异常");
        }
        LogUtil.d(TAG, "## OTA文件夹基础路径 = " + dir.getPath());
        return dir;
    }

    public static String getPackageName() {
        if (TextUtils.isEmpty(PACKAGE_NAME)) {
            PACKAGE_NAME = MyApplication.getInstance().getPackageName();
        }
        return PACKAGE_NAME;
    }

}
