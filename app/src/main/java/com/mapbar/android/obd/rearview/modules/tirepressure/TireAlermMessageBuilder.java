package com.mapbar.android.obd.rearview.modules.tirepressure;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.notify.Notification;
import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.Manager;
import com.mapbar.obd.TPMSAlarmData;

import java.util.Random;

/**
 * 构造胎压提醒文字
 * Created by zhangyunfei on 16/8/18.
 */
public class TireAlermMessageBuilder {

    private static final String TAG = TireAlermMessageBuilder.class.getSimpleName();

    /**
     * 构造胎压提醒文字
     *
     * @param data
     * @return
     */
    public static Notification from(@NonNull Context mContext, @NonNull TPMSAlarmData data) {
        int type = data.getType();
        String tyreStr = null;
        //左前胎or 右前胎 or..
        if (type == 0) {
            tyreStr = mContext.getResources().getString(R.string.left_top_text);
        } else if (type == 1) {
            tyreStr = mContext.getResources().getString(R.string.right_top_text);
        } else if (type == 2) {
            tyreStr = mContext.getResources().getString(R.string.left_bot_text);
        } else if (type == 3) {
            tyreStr = mContext.getResources().getString(R.string.right_bot_text);
        } else {
            LogUtil.e(TAG, "## 发现无法处理的胎压提醒事件 type=" + tyreStr);
            return null;
        }
        String speekStr = tyreStr;
        int[] dataArr = data.getData();
        //一个轮胎的信息，返回的ddaArr数据中安装优先级排序
        for (int i = 0; i < dataArr.length; i++) {
            if (dataArr[i] == Manager.TPMSAlarmType.tyrequickLeak) {
                tyreStr = tyreStr + (mContext.getResources().getString(R.string.rapid_leak) + "\n");
                speekStr += mContext.getResources().getString(R.string.rapid_leak);

            } else if (dataArr[i] == Manager.TPMSAlarmType.tyreslowLeak) {
                tyreStr = tyreStr + (mContext.getResources().getString(R.string.slow_leak) + "\n");
                speekStr += mContext.getResources().getString(R.string.slow_leak);

            } else if (dataArr[i] == Manager.TPMSAlarmType.tyreHot) {
                tyreStr = tyreStr + (mContext.getResources().getString(R.string.overheating) + "\n");
                speekStr += mContext.getResources().getString(R.string.overheating);

            } else if (dataArr[i] == Manager.TPMSAlarmType.tyreUnderPa) {
                tyreStr = tyreStr + (mContext.getResources().getString(R.string.under_pressure) + "\n");
                speekStr += mContext.getResources().getString(R.string.under_pressure);

            } else if (dataArr[i] == Manager.TPMSAlarmType.tyreOverPa) {
                tyreStr = tyreStr + (mContext.getResources().getString(R.string.over_pressure) + "\n");
                speekStr += mContext.getResources().getString(R.string.over_pressure);

            } else if (dataArr[i] == Manager.TPMSAlarmType.sensorInvalided) {
                tyreStr = tyreStr + (mContext.getResources().getString(R.string.lost_connect_text) + "\n");
                speekStr += mContext.getResources().getString(R.string.lost_connect_text);

            } else if (dataArr[i] == Manager.TPMSAlarmType.tyrelowPower) {
                tyreStr = tyreStr + (mContext.getResources().getString(R.string.low_power) + "\n");
                speekStr += mContext.getResources().getString(R.string.low_power);

            } else {
                LogUtil.e(TAG, "## 发现无法处理的胎压提醒事件 data=" + dataArr[i]);
                return null;
            }
            if (i == dataArr.length - 1) {
                tyreStr = tyreStr.substring(0, tyreStr.length() - 1);
            }
        }
        return new Notification(tyreStr, speekStr);
    }


    static Random random = new Random();

//    public static TPMSAlarmData createDemoData() {
//
//        int type = random.nextInt(4);
//
//        int n = random.nextInt(3) + 1;
//        int[] dataArr = new int[n];
//        for (int i = 0; i < dataArr.length; i++) {
//            dataArr[i] = random.nextInt(8);
//        }
//        TPMSAlarmData tpmsAlarmData = new TPMSAlarmData(type, 0, 0, dataArr);
//        return tpmsAlarmData;
//    }
}
