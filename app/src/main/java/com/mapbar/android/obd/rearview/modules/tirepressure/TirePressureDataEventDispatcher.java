package com.mapbar.android.obd.rearview.modules.tirepressure;

import com.mapbar.android.obd.rearview.lib.base.BaseEventDispatcher;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.Manager;
import com.mapbar.obd.RealTimeDataTPMSAll;

/**
 * 胎压数据事件 分发器
 * 从sdk收到消息，再次分发
 * Created by zhangyunfei on 16/8/17.
 */
public class TirePressureDataEventDispatcher extends BaseEventDispatcher {
    public static final String TAG = "TirePressure";
    private TirePressurePresenter tirePressureDataEventHandler;

    public TirePressureDataEventDispatcher(TirePressurePresenter tirePressureDataEventHandler) {
        this.tirePressureDataEventHandler = tirePressureDataEventHandler;
    }

    @Override
    protected void onSDKEvent(int event, Object o) {
        switch (event) {
            case Manager.Event.dataUpdateTPMS://精确胎压数据
                LogUtil.i(TAG, "## 收到胎压事件 - 精确胎压 ");
                if (o instanceof RealTimeDataTPMSAll) {
                    RealTimeDataTPMSAll realTimeDataTPMSAll = (RealTimeDataTPMSAll) o;
//                            if (tirePressureDataEventHandler != null)
//                                tirePressureDataEventHandler.onReceiveTirePressureFromImmediate(realTimeDataTPMSAll);
                }
                break;
            case Manager.Event.dataUpdateWSBTPMS://算法胎压数据
                LogUtil.i(TAG, "## 收到胎压事件 - 算法胎压数据");
                //存储着更新了的实时数据,此时数据中只有轮胎位置和轮胎欠压标志位有效
                if (o instanceof RealTimeDataTPMSAll) {
                    RealTimeDataTPMSAll realTimeDataTPMSAll = (RealTimeDataTPMSAll) o;
                    if (tirePressureDataEventHandler != null)
                        tirePressureDataEventHandler.onReceiveTirePressureFromIndirect(realTimeDataTPMSAll);
                }
                break;
        }
    }


    public interface TirePressureDataEventHandler {
        /**
         * 当收到 直接胎压数据，精确胎压数据
         *
         * @param realTimeDataTPMSAll
         */
        void onReceiveTirePressureFromImmediate(RealTimeDataTPMSAll realTimeDataTPMSAll);

        /***
         * 当收到，间接胎压数据，算法胎压数据
         * <p/>
         * 此时数据中只有轮胎位置和轮胎欠压标志位有效
         *
         * @param realTimeDataTPMSAll
         */
        void onReceiveTirePressureFromIndirect(RealTimeDataTPMSAll realTimeDataTPMSAll);

    }
}
