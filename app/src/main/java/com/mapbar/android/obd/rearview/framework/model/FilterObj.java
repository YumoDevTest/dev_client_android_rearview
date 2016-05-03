package com.mapbar.android.obd.rearview.framework.model;

import java.util.HashMap;

public class FilterObj {

    private HashMap<String, Object> dataMap;

    public FilterObj() {
        dataMap = new HashMap<>();
    }

    public void put(String key, Object value) {
        dataMap.put(key, value);
    }

    public Object getObj(String key) {
        return dataMap.get(key);
    }

    public String getString(String key) {
        return (String) dataMap.get(key);
    }

    public int getInt(String key) {
        return (int) dataMap.get(key);
    }

//    public <T> getByClass(String key,Class<T> tClass){
//        return (T)dataMap.get(key);
//    }

}
