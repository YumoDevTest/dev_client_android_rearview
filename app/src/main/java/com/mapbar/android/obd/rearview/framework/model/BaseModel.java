package com.mapbar.android.obd.rearview.framework.model;

import com.mapbar.android.obd.rearview.framework.control.SDKManager;

/**
 * Created by liuyy on 2016/5/10.
 */
public abstract class BaseModel {

    protected static BaseModel model;
    protected OBDListener obdListener;
    protected SDKManager.SDKListener sdkListener;

    protected BaseModel() {
        init();
    }

    public static BaseModel getModel(Class<? extends BaseModel> clazz) {
        try {
            if (model == null) {

                model = clazz.newInstance();

            } else if (!model.getClass().equals(clazz)) {
                model = clazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    public void init() {
        sdkListener = new SDKManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                super.onEvent(event, o);
                onEvent(event, o);
            }
        };
        SDKManager.getInstance().setSdkListener(sdkListener);
    }

    /**
     * SDK事件回调
     *
     * @param event 事件的id
     * @param o     事件携带的数据
     */
    public abstract void onEvent(int event, Object o);

    public void setObdListener(OBDListener obdListener) {
        this.obdListener = obdListener;
    }

    /**
     * FIXME 回调给用户的事件
     */
    public interface OBDListener {
        void onEvent(int event, Object o);
    }
}
