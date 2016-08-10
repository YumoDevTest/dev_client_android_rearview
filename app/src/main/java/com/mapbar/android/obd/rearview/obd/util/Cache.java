package com.mapbar.android.obd.rearview.obd.util;

/**
 * 缓存
 * Created by zhangyunfei on 16/8/10.
 */
public interface Cache {

    public Object getCache(String key);

    public boolean exist(String key);

    public void cache(String key, Object object);

    void clear();
}