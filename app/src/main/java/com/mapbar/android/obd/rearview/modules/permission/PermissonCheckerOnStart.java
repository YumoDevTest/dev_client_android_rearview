package com.mapbar.android.obd.rearview.modules.permission;

import android.content.Context;
import android.content.Intent;

import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.common.Session;
import com.mapbar.android.obd.rearview.obd.Application;
import com.mapbar.obd.OBDFuncRightData;
import com.mapbar.obd.UserCenterError;

/**
 * 在app启动时，用本类查询权限，仅在app启动时检查一次
 * 如果有权限，则正常使用
 * 如果在试用，弹窗提醒试用
 * 如果没有权限，弹窗 提示过期
 * Created by zhangyunfei on 16/8/6.
 */
public class PermissonCheckerOnStart {
    //key，指示了是否在启动时 检查了权限
    private static final String KEY_FOR_HAS_CHECKED_ON_START = "KEY_FOR_HAS_CHECKED_ON_START";

    public synchronized void downloadPermision(final Context context) {
        final PermissionManager permissionManager = LogicFactory.createPermissionManager();
        permissionManager.downloadPermissionList(new PermissionManager.DownloadPermissionCallback() {
            @Override
            public void onSuccess(OBDFuncRightData result) {
                //表示更新权限成功。
                checkLocalPermissionSummary(context);
            }

            @Override
            public void onFuncRightServerFailed(UserCenterError userCenterError) {
                //提示用户，更新权限失败
                showDialog_DownloadFailure(context);
            }

            @Override
            public void onFailure(UserCenterError userCenterError) {
                //提示用户，更新权限失败
                showDialog_DownloadFailure(context);
            }
        });

    }

    /**
     * 检查本地权限
     */
    public void checkLocalPermissionSummary(Context context) {
        //判断 是否在启动时 检查过权限
        final Session session = Application.getInstance().getSession();
        if (session.getBoolean(KEY_FOR_HAS_CHECKED_ON_START, false)) {
            return;//已检查过，则无需再次检查
        }

        PermissionManager permissionManager = LogicFactory.createPermissionManager();
        PermissionManager.PermissionSummary permissionSummary = permissionManager.getPermissionSummary();
        if (permissionSummary.summary == PermissionManager.PermissionSummary.HAS_PAY) {
            //支付过，不弹窗
        } else if (permissionSummary.summary == PermissionManager.PermissionSummary.TRAIL) {
            //试用中，弹窗提示剩余多少天
            boolean expired = permissionSummary.expired;
            int numberOfDay = permissionSummary.numberOfDay;
            showDialog_checkResult(context, expired, numberOfDay);
        } else if (permissionSummary.summary == PermissionManager.PermissionSummary.NO_PERSSION) {
            //权限都过期而未买过，弹窗提示权限过期
            boolean expired = permissionSummary.expired;
            int numberOfDay = permissionSummary.numberOfDay;
            showDialog_checkResult(context, expired, numberOfDay);
        }

        session.put(KEY_FOR_HAS_CHECKED_ON_START, Boolean.TRUE);
    }

    private void showDialog_checkResult(Context context, boolean expired, int numberOfDay) {
        Intent intent = new Intent(context, PermissionTrialAlertDialog.class);
        intent.putExtra("expired", expired);
        intent.putExtra("numberOfDay", numberOfDay);
        context.startActivity(intent);
    }


    private void showDialog_DownloadFailure(Context context) {
        Intent intent = new Intent(context, PermissionUpdateFailureActivity.class);
        context.startActivity(intent);
    }

}
