package com.mapbar.android.obd.rearview.modules.permission;

/**
 * 授权 对应的key
 * Created by zhangyunfei on 16/8/4.
 */
public abstract class PermissionKey {

    public static final int PERMISSION_CHECK_UP = 1001;//	OBD全车体检	车辆状态异常预警提示；车辆全面体检功能，故障码清除；	收费	　
    public static final int PERMISSION_TIRE_PRESSURE = 1002;//	胎压监测	胎压异常报警；	收费	　
    public static final int PERMISSION_CAR_STATE = 1003;//	车辆状态和车辆控制	车灯、车窗、车锁、车门、后备箱、天窗的状态；包含语音控制功能；	收费	　
    public static final int PERMISSION_CAR_DATA = 1004;//	OBD普通数据	车速、转速、电压、水温、瞬时油耗、平均油耗、空调	免费	　
    public static final int PERMISSION_MAINTENANCE = 1005;//	保养提醒	根据输入值保存用户输入信息；计算和显示下次保养距离、时间、项目。免费

}
