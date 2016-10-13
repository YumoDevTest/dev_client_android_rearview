package com.mapbar.android.obd.rearview.modules.maintenance.contract;

import android.content.Context;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.config.MyApplication;

/**
 * Created by zhangyh on 2016/10/12.
 * 保养错误码类
 * 根据传过来的错误码查找对应的错误信息进行返回
 */
public class MaintenanceErrorCode {
    private MaintenanceErrorCode(){}

    public static String getErrorInfo(Context context,int errorCode){
        String[] errorInfos = context.getResources().getStringArray(R.array.maintenance_errorcode);
        if(errorCode <= errorInfos.length)
            return errorInfos[errorCode - 1];
        else
            return errorInfos[12];
    }
}
