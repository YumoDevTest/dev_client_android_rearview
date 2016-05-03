package com.mapbar.android.obd.rearview.framework.model;

import java.util.Hashtable;

public class PageRestoreData {
    private int mFlag;
    private int mPosition;
    private int mDataType = -1;

    private Hashtable<Integer, Object> mDatas = new Hashtable<Integer, Object>();

    public PageRestoreData(int flag, int position) {
        this.mFlag = flag;
        this.mPosition = position;
    }

    public int getFlag() {
        return this.mFlag;
    }

    public void setFlag(int flag) {
        this.mFlag = flag;
    }

    public int getPosition() {
        return this.mPosition;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public int getDataType() {
        return this.mDataType;
    }

    public void setDataType(int type) {
        this.mDataType = type;
    }

    public Hashtable<Integer, Object> getDatas() {
        return this.mDatas;
    }

    public void setDatas(Hashtable<Integer, Object> datas) {
        this.mDatas = datas;
    }

    public void addData(int key, Object obj) {
        this.mDatas.put(key, obj);
    }

    public Object getData(int key) {
        if (this.mDatas.containsKey(key))
            return this.mDatas.get(key);
        return null;
    }
}
