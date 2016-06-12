package com.mapbar.android.obd.rearview.framework.bean;

/**
 * 生成二维码信息
 */
public class QRInfo {
    /**
     * 生成二维码内容
     */
    private String url;

    /**
     * 二维码下方内容,可以不使用
     */
    private String content;

    public QRInfo() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    @Override
    public String toString() {
        return "QRInfo{" +
                "url='" + url + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
