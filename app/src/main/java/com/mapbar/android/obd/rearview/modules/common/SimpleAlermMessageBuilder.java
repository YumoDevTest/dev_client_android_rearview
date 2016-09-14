package com.mapbar.android.obd.rearview.modules.common;

import android.content.Context;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.notify.Notification;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.obd.AlarmData;
import com.mapbar.obd.Manager;

import java.util.Locale;

/**
 * 基础提醒消息，构造提醒文本
 * Created by zhangyunfei on 16/8/18.
 */
public class SimpleAlermMessageBuilder {

    private static final String TAG = SimpleAlermMessageBuilder.class.getSimpleName();

    public static Notification from(Context mContext, AlarmData data) {
        String content = null;
        String speak = null;
        int type = data.getType();
        if (type == Manager.AlarmType.errCode) {
            content = data.getString();
            content = mContext.getResources().getString(R.string.dlg_errCode, content);
            if (Constants.IS_OVERSEAS_EDITION) {
                StringBuilder contentEn = new StringBuilder();
                if (!TextUtils.isEmpty(content)) {
                    for (int i = 0; i < content.length(); i++) {
                        contentEn.append(content.charAt(i)).append(" ");
                    }
                }
                content = contentEn.toString();
            }
            speak = mContext.getResources().getString(R.string.bca_errCode, content);

        } else if (type == Manager.AlarmType.temperature) {
            content = String.format(Locale.getDefault(), "%d", data.getInt());
            content = mContext.getResources().getString(R.string.dlg_temperature, content);
            speak = mContext.getResources().getString(R.string.bca_temperature, content);

        } else if (type == Manager.AlarmType.voltage) {
            content = String.format(Locale.getDefault(), "%.1f", data.getFloat());
            content = mContext.getResources().getString(R.string.dlg_voltage, content);
            speak = mContext.getResources().getString(R.string.bca_voltage, content);

        } else if (type == Manager.AlarmType.tired) {
            content = String.format(Locale.getDefault(), "%.1f", data.getFloat());
            content = mContext.getResources().getString(R.string.dlg_tired, content);
            speak = mContext.getResources().getString(R.string.bca_tired, content);
        } else {
            LogUtil.e(TAG, "## 发现无法处理的普通提醒事件 type=" + type);
            return null;
        }
//        return new String[]{content, speak};
        //TODO 故意写成这样。水温，电压 的语音提醒的文字内容重复，待修复
        return new Notification(content, content);
    }
}