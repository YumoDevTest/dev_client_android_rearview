package com.mapbar.android.obd.rearview.framework.manager;

import com.mapbar.android.obd.rearview.framework.control.SDKListenerManager;
import com.mapbar.obd.Manager;
import com.mapbar.obd.Physical;
import com.mapbar.obd.PhysicalData;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：一键体检
 * 抛出事件
 * event1：EVENT_OBD_PHYSICAL_CONDITION_FAILED,体检不满足条件 ;o=不满足类型  int型,0是速度  1是转速
 * event2：EVENT_OBD_PHYSICAL_CHECK_START； o=NULL
 * event3：EVENT_OBD_PHYSICAL_CHECK_RESULT,体检结果事件；此时 o 参数是Physical.SystemInfo，存储着体检系统ID和检测结果的信息
 * event4：EVENT_OBD_PHYSICAL_CHECK_END,体检完成的回调
 * event5：EVENT_OBD_PHYSICAL_CHECK_PROGRESS,体检完成的进度 ;o 是体检进度 int型
 */
public class PhysicalManager extends OBDManager {
    public static final int EVENT_OBD_PHYSICAL_CONDITION_FAILED = 0xF00001;
    public static final int EVENT_OBD_PHYSICAL_CHECK_START = 0xF00002;
    public static final int EVENT_OBD_PHYSICAL_CHECK_RESULT = 0xF00003;
    public static final int EVENT_OBD_PHYSICAL_CHECK_END = 0xF00004;
    public static final int EVENT_OBD_PHYSICAL_CHECK_PROGRESS = 0xF00004;

    private List<PhysicalData> physicalList = new ArrayList<>();
    private int progress = 0;

    public PhysicalManager() {
        sdkListener = new SDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                onSDKEvent(event, o);
            }
        };
        SDKListenerManager.getInstance().setSdkListener(sdkListener);
    }

    public static PhysicalManager getInstance() {
        return (PhysicalManager) OBDManager.getInstance(PhysicalManager.class);
    }

    /**
     * 开始一键体检
     *
     * @return false:不满足体检条件，请保持OBD设备在非行程被手动终止状态;true:体检开始进行
     */
    public boolean startExam() {
        int state = Manager.getInstance().getState();
        if (state != Manager.State.tripStoppedManually) {
            return false;
        }
        Physical physical = Physical.getInstance();
        physical.startPhysical();
        return true;
    }

    /**
     * 结束体检,页面上点返回，或者系统back键返回
     */
    public void stopPhysical() {
        Physical physical = Physical.getInstance();
        physical.stopPhysical();
    }

    /**
     * 一键体检的SDK事件回调
     *
     * @param event 事件的id
     * @param o     事件携带的数据
     */
    @Override
    public void onSDKEvent(int event, Object o) {
        if (baseObdListener != null) {
            switch (event) {
                case Manager.Event.obdPhysicalConditionFailed:
                    break;
                case Manager.Event.obdPhysicalCheckStart:
                    break;
                case Manager.Event.obdPhysicalCheckResult:
                    for (PhysicalData itemData : physicalList) {
                        if (itemData.getId() == ((PhysicalData) o).getId()) {
                            progress = getProgress(itemData.getId());
                            baseObdListener.onEvent(EVENT_OBD_PHYSICAL_CHECK_PROGRESS, progress);
                        }
                    }
                    break;
                case Manager.Event.obdPhysicalCheckEnd:
                    progress = 100;
                    baseObdListener.onEvent(EVENT_OBD_PHYSICAL_CHECK_PROGRESS, progress);
                    break;
            }
        }
        super.onSDKEvent(event, o);
    }

    /**
     * 体检页，返回所有分类列表
     *
     * @return 分类列表
     */
    public List<PhysicalData> getPhysicalSystem() {
        return Physical.getInstance().getPhysicalSystem();
    }

    /**
     * 根据体检项id计算体检进度
     *
     * @param id PhysicalData的id
     * @return 体检进度
     */
    private int getProgress(int id) {
        physicalList = Physical.getInstance().getPhysicalSystem();
        int progress = 0;
        switch (id) {
            case 1:
                progress = 0;
                break;
            case 2:
                progress = 25;
                break;
            case 3:
                progress = 26;
                break;
            case 4:
                progress = 28;
                break;
            case 5:
                progress = 29;
                break;
            case 6:
                progress = 30;
                break;
            case 7:
                progress = 92;
                break;
        }
        return progress;
    }
}
