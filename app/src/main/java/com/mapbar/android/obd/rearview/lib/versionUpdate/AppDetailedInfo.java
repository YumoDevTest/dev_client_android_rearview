package com.mapbar.android.obd.rearview.lib.versionUpdate;

import java.io.Serializable;

/**
 * Created by zhangyh on 2016/10/31 0031.
 */

public class AppDetailedInfo implements Serializable {
    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "AppDetailedInfo{" +
                "md5='" + md5 + '\'' +
                '}';
    }
}
