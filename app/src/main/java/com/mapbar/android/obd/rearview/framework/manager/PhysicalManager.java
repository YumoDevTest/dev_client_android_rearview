package com.mapbar.android.obd.rearview.framework.manager;

import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.obd.Manager;
import com.mapbar.obd.Physical;
import com.mapbar.obd.PhysicalData;
import com.mapbar.obd.ReportHead;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：车辆体检功能
 * 抛出事件
 * event1：EVENT_OBD_PHYSICAL_CHECK_PROGRESS,体检完成的进度 ;o 是体检进度 int型
 */
public class PhysicalManager extends OBDManager {
    public static final int EVENT_OBD_PHYSICAL_CHECK_PROGRESS = 0xF00004;
    private List<PhysicalData> physicalList = new ArrayList<>();
    private int progress = 0;
    private ArrayList<Integer> statuses;

    public PhysicalManager() {
        super();
//        sdkListener = new SDKListenerManager.SDKListener() {
//            @Override
//            public void onEvent(int event, Object o) {
//                onSDKEvent(event, o);
//            }
//        };
//        SDKListenerManager.getInstance().setSdkListener(sdkListener);
    }

    /**
     * 获取PhysicalManager单例
     *
     * @return PhysicalManager实例
     */
    public static PhysicalManager getInstance() {
        return (PhysicalManager) OBDManager.getInstance(PhysicalManager.class);
    }

    /**
     * 开始一键体检
     * 可以接收sdk回调事件：
     * Manager.Event.obdPhysicalConditionFailed//体检失败
     * Manager.Event.obdPhysicalCheckStart//体检开始事件；o=NULL
     * Manager.Event.obdPhysicalCheckResult//体检结果此时 o参数是Physical.SystemInfo，存储着体检系统ID和检测结果的信息
     * Manager.Event.obdPhysicalCheckEnd//体检结束
     * PhysicalManager.EVENT_OBD_PHYSICAL_CHECK_PROGRESS//体检进度 o参数为进度（int型）
     *
     * @return false:不满足体检条件，请保持OBD设备在非行程被手动终止状态;true:体检开始进行
     */
    public boolean startExam() {
        Manager.getInstance().stopTrip(false);
        statuses = new ArrayList<>();
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
                    progress = getProgress(((PhysicalData) o).getId());
                    baseObdListener.onEvent(EVENT_OBD_PHYSICAL_CHECK_PROGRESS, progress);
                    // 日志
                    if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                        Log.v(LogTag.TEMP, "obdPhysicalCheckResult -->>");
                        Log.v(LogTag.TEMP, "progress -->>" + progress);
                    }
                    break;
                case Manager.Event.obdPhysicalCheckEnd:
                    progress = 100;
                    baseObdListener.onEvent(EVENT_OBD_PHYSICAL_CHECK_PROGRESS, progress);
                    break;
            }
        }
//        super.onSDKEvent(event, o);
    }

    /**
     * 体检页，返回所有分类列表
     *
     * @return 分类列表
     */
    public ArrayList<PhysicalData> getPhysicalSystem() {
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

    /**
     * 获取体检每大项的体检结果（正常、异常、正在体检、无法判定）集合
     *
     * @return ArrayList 每大项体检结果集合
     */
    public ArrayList<Integer> getStatuses() {
        return statuses;
    }

    /**
     * 获取报告列表显示用的信息
     *
     * @return 报告列表
     */
    public ReportHead getReportHead() {
        ArrayList<ReportHead> heads = Physical.getInstance().getPhysicalReportByMonth(1970, 1);
        if (heads != null && heads.size() > 0) {
            return heads.get(0);
        } else {
            return null;
        }
    }
}
