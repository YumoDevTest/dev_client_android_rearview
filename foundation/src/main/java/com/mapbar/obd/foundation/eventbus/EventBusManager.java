package com.mapbar.obd.foundation.eventbus;

import org.greenrobot.eventbus.EventBus;

/**
 * Event Bus 操作类
 * Created by zhangyunfei on 16/8/15.
 */
public class EventBusManager {

    /**
     * 注册
     * @param subscriber
     */
    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    /**
     * 反注册
     * @param subscriber
     */
    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    /**
     * 触发事件
     * @param event
     */
    public static void post(Object event) {
        EventBus.getDefault().post(event);
    }
}
