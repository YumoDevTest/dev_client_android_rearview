package com.mapbar.android.obd.rearview.lib.base;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.obd.foundation.log.LogUtil;

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
        Context context = MyApplication.getInstance();
        File dir = new File(Environment.getExternalStorageDirectory(), getPackageName(context) + "/binfile");//Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!dir.exists()) {
            boolean isOk = dir.mkdirs();
            if (!isOk)
                LogUtil.e(TAG, "## 创建bin缓存文件夹异常");
        }
        LogUtil.d(TAG, "## OTA文件夹基础路径 = " + dir.getPath());
        return dir;
    }

    private static String getPackageName(Context context) {
        if (TextUtils.isEmpty(PACKAGE_NAME)) {
            PACKAGE_NAME = context.getPackageName();
        }
        return PACKAGE_NAME;
    }

}
