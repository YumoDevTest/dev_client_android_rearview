package com.mapbar.obd.foundation.net.breakpoint;

import java.io.Serializable;

/**
 * Created by zhangyh on 2016/10/28 0028.
 * 下载任务实体类
 */

public class DownloadTask implements Serializable {
    private long breakpoint;
    private String url;
    private String filePath;
    private String MD5;
    private boolean isCompelete;

    public long getBreakpoint() {
        return breakpoint;
    }

    public void setBreakpoint(long breakpoint) {
        this.breakpoint = breakpoint;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public boolean isCompelete() {
        return isCompelete;
    }

    public void setCompelete(boolean compelete) {
        isCompelete = compelete;
    }

    @Override
    public String toString() {
        return "DownloadTask{" +
                "breakpoint=" + breakpoint +
                ", url='" + url + '\'' +
                ", filePath='" + filePath + '\'' +
                ", MD5='" + MD5 + '\'' +
                ", isCompelete=" + isCompelete +
                '}';
    }
}
