package com.mapbar.android.obd.rearview.modules.common;

import java.io.InvalidObjectException;
import java.util.HashMap;

/**
 * 会话对象。存储了一些对象，类似HashMap
 * Created by zhangyunfei on 16/8/7.
 */
public class Session {
    HashMap<String, Object> hashMap;

    public Session() {
        hashMap = new HashMap<>();
    }

    public void put(String key, Object value) {
        hashMap.put(key, value);
    }

    public Object getObject(String key) {
        return hashMap.get(key);
    }

    public String getString(String key, String defalutValue) throws InvalidObjectException {
        if (!hashMap.containsKey(key)) {
            return defalutValue;
        }
        Object tmp = hashMap.get(key);
        if (tmp instanceof String) {
            return (String) tmp;
        } else {
            return defalutValue;
//            throw new InvalidObjectException(String.format("key is not a string,it is a ", key, tmp.getClass().getSimpleName()));
        }
    }

    public int getInt(String key, int defalutValue) {
        if (!hashMap.containsKey(key)) {
            return defalutValue;
        }
        Object tmp = hashMap.get(key);
        if (tmp instanceof Integer)
            return (Integer) tmp;
        else
            return defalutValue;
    }

    public Boolean getBoolean(String key, boolean defalutValue) {
        if (!hashMap.containsKey(key)) {
            return defalutValue;
        }
        Object tmp = hashMap.get(key);
        if (tmp instanceof Boolean)
            return (Boolean) tmp;
        else
            return defalutValue;
    }
}
