package com.mapbar.android.obd.rearview.obd.util;

import java.util.HashMap;

/**
 * 缓存
 * Created by zhangyunfei on 16/8/10.
 */
public class MemoryCache implements Cache {
    private static HashMap<String, Object> hashMap;

    static {
        hashMap = new HashMap<>();
    }

    public Object getCache(String key) {
        if (!exist(key))
            return null;
        return hashMap.get(key);
    }

    public boolean exist(String key) {
        return hashMap.containsKey(key);
    }

    public void cache(String key, Object object) {
        hashMap.put(key, object);
    }

    @Override
    public void clear() {
        hashMap.clear();
    }
}