package com.mapbar.android.obd.rearview.framework.manager;

import com.mapbar.mapdal.DateTime;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.MaintenanceInfo;
import com.mapbar.obd.MaintenanceState;
import com.mapbar.obd.MaintenanceTask;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCar;

/**
 * Created by tianff on 2016/6/12.
 */
public class CarMaintenanceManager extends OBDManager {


    protected CarMaintenanceManager() {
    }

    /**
     * 获得单例
     *
     * @return
     */
    public static CarMaintenanceManager getInstance() {
        return (CarMaintenanceManager) OBDManager.getInstance(CarMaintenanceManager.class);
    }

    /**
     * 获取保养项目
     *
     * @return 保养项目
     */
    public MaintenanceTask[] getTasks(MaintenanceState maintenanceState) {
        return maintenanceState.getTasks();
    }

    /**
     * 获取预计下一次保养日期 注意：可能为无效值，使用前需要调用 DateTime.isValid()判断是否有效
     * 仅当 getTag() 返回 Tag.nextMaintenanceDateEstimatedByMileage 或者 Tag.nextMaintenanceDateEstimatedByTime 时可以调用。
     *
     * @param maintenanceState 车辆保养状态
     * @return DateTime预计下一次保养日期
     */
    public DateTime getNextMaintenanceDate(MaintenanceState maintenanceState) {
        return maintenanceState.getNextMaintenanceDate();
    }


    /**
     * 根据服务器车型保养方案在本地的缓存来计算用户当前车辆的保养状态
     *
     * @return 用户当前车辆的保养状态
     */
    public MaintenanceInfo queryMaintenanceInfoByLocalSchemeCache() {
        return Manager.getInstance().queryMaintenanceInfoByLocalSchemeCache();
    }

    /**
     * 从服务器获取用户当前车辆车型的保养方案，并据此计算车辆的保养状态
     * 结果通过 OBD 事件来通知调用者:
     * 如果成功，则会收到 Manager.Event.queryRemoteMaintenanceInfoSucc 此时事件附带的数据是一个 MaintenanceState 对象,存储着计算得到的保养状态
     * 如果失败，则会收到 Manager.Event.queryRemoteMaintenanceInfoFailed 此时事件附带的数据是一个 MaintenanceError 对象，存储着错误码与错误消息
     */
    public void queryRemoteMaintenanceInfo() {
        Manager.getInstance().queryRemoteMaintenanceInfo();
    }

    /**
     * 设置当前用户当前车辆的车辆信息
     * 注意：此接口中设置的UserCar对象需要来自于 queryLocalUserCar()返回值或 queryRemoteUserCar() 所产生的回调结果中的数据，修改回调中的对象数据后调用此方法设置车辆信息。 可能会收到如下事件:
     * 1、Manager.Event.carInfoUploadSucc车信息上传服务器成功,收到此消息时，回调中返回数据为Integer，对应于 Manager.CarInfoResponseErr的值，储存了对应的错误码
     * 2、Manager.Event.carInfoUploadFailed车信息上传服务器失败
     * 收到此消息时，回调中返回数据为Integer，对应于 Manager.CarInfoResponseErr的值，储存了对应的错误码
     * 3、Manager.Event.carInfoWriteDatabaseSucc车信息写入数据库成功
     * 收到此消息时，回调中返回数据为Integer，对应于 Manager.CarInfoResponseErr的值，储存了对应的错误码
     * 4、Manager.Event.carInfoWriteDatabaseFailed车信息写入数据库失败
     * 收到此消息时，回调中返回数据为Integer，对应于 Manager.CarInfoResponseErr的值，储存了对应的错误码
     */
    public void setUserCar(UserCar info) {
        Manager.getInstance().setUserCar(info);
    }

    /**
     * 查询用户本地车辆信息
     *
     * @return 本地查询车辆返回结果
     */
    public LocalUserCarResult queryLocalUserCar() {
        return Manager.getInstance().queryLocalUserCar();
    }


}
