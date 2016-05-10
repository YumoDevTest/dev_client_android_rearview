package com.mapbar.android.obd.rearview.framework.model;

import com.mapbar.obd.Manager;

/**
 * 功能：xxx
 * 抛出事件
 * event1：obdPhysicalCheckEnd,体检完成的回调
 * event2：
 */
public class PhysicalModel extends BaseModel {
    public static final int EVENT_XXX = 0xF00001;

    /**
     * 开始一键体检
     *
     * @return false:不满足体检条件，请保持车在怠速状态;true:体检开始进行
     */
    public boolean startExam() {
        int state = Manager.getInstance().getState();
        if (state != Manager.State.tripStoppedManually) {
            return false;
        }
        //startPhysical()
        return true;
    }

    /**
     * 一键体检的SDK事件回调
     *
     * @param event 事件的id
     * @param o     事件携带的数据
     */
    @Override
    public void onEvent(int event, Object o) {
        switch (event) {
            case Manager.Event.obdPhysicalCheckEnd:
                if (obdListener != null) {
                    obdListener.onEvent(event, o);
                }
                break;
        }
    }


}
