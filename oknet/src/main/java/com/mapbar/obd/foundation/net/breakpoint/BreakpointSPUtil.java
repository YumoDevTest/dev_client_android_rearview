package com.mapbar.obd.foundation.net.breakpoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

/**
 * Created by zhangyh on 2016/10/18.
 * 保存查询到的串口的工具类
 */

public class BreakpointSPUtil {

    private static SharedPreferences sp;
    private static Gson gson = new Gson();

    public static DownloadTask getTask(Context context) {
        sp = context.getSharedPreferences("Breakpoint", Context.MODE_PRIVATE);
        String task = sp.getString("task", "");
        if (TextUtils.isEmpty(task)) {
            return null;
        }
        DownloadTask downloadTask = gson.fromJson(task, DownloadTask.class);
        return downloadTask;
    }

    public static void setTask(Context context, DownloadTask task) {
        String json;
        if (task != null) {
            json = gson.toJson(task);
        } else {
            json = "";
        }
        if (sp == null) {
            sp = context.getSharedPreferences("Breakpoint", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("task", json);
        edit.commit();
    }
}
