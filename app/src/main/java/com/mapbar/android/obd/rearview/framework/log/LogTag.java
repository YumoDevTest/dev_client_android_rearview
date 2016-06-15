package com.mapbar.android.obd.rearview.framework.log;

/**
 *
 */
public enum LogTag implements LogTagInterface {

    /**
     * SD卡
     */
    SDCARD,

    /**
     * 临时
     */
    TEMP,

    /**
     * 全局
     */
    GLOBAL,


    /**
     * 系统：
     */
    SYSTEM_ANDROID,


    /**
     * 框架
     */
    FRAMEWORK,


    /**
     * 界面
     */
    UI,


    /**
     * 渲染
     */
    DRAW,


    /**
     * 定位
     */
    LOCATION,


    /**
     * 帷千
     */
    WEIQIAN,


    /**
     * 用户中心
     */
    USER_CENTER,

    /**
     * 网络
     */
    HTTP_NET,

    /**
     * 脚本
     */
    SCRIPT,


    /**
     * obd
     */
    OBD,

    /**
     * 推送
     */
    PUSH,

    /**
     * 启动
     */
    LAUNCH,

    /**
     *
     */
    CONNECT,

    /**
     *
     */
    OTA,

    @Deprecated
    ALL;

    @Override
    public String getTagName() {
        return name();
    }

}
