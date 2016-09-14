package com.mapbar.android.obd.rearview.modules.permission;

import android.content.Context;
import android.content.Intent;

import com.mapbar.android.obd.rearview.BuildConfig;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.box.protobuf.bean.ObdRightBean;

import java.util.List;

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
    private static final String TAG = PermissonCheckerOnStart.class.getSimpleName();

    public void downloadPermision(final Context context, PermissionManager.DownloadPermissionCallback callback1) {
        //如果开启了 关闭了权限验证。则不下载
        if (!BuildConfig.IS_ENABLE_PERMISSION)
            return;
        final PermissionManager permissionManager = LogicFactory.createPermissionManager(context);
        permissionManager.downloadPermissionList(callback1);
    }

    public void downloadPermision(final Context context) {
        //如果开启了 关闭了权限验证。则不下载
        if (!BuildConfig.IS_ENABLE_PERMISSION)
            return;
        final PermissionManager permissionManager = LogicFactory.createPermissionManager(context);
        permissionManager.downloadPermissionList(new PermissionManager.DownloadPermissionCallback() {
            @Override
            public void onSuccess(List<ObdRightBean.ObdRight> permissionList) {
                //表示更新权限成功。
                LogUtil.d(TAG, "## 例行下载权限成功");
                checkLocalPermissionSummary(context);
            }

            @Override
            public void onFailure(Exception ex) {
                //提示用户，更新权限失败
                showDialog_DownloadFailure(context, ex.getMessage());
            }
        });

    }

    /**
     * 检查本地权限
     */
    public void checkLocalPermissionSummary(Context context) {
        LogUtil.d(TAG, "## 准备 判断本地权限摘要");
        //判断 是否在启动时 检查过权限
//        final Session session = MyApplication.create().getSession();
//        if (session.getBoolean(KEY_FOR_HAS_CHECKED_ON_START, false)) {
//            return;//已检查过，则无需再次检查
//        }

        PermissionManager permissionManager = LogicFactory.createPermissionManager(context);
        PermissionManager.PermissionSummary permissionSummary = permissionManager.getPermissionSummary();
        if (permissionSummary.summary == PermissionManager.PermissionSummary.HAS_PAY) {
            //支付过，不弹窗
            LogUtil.d(TAG, "## 本地权限摘要结果:支付过");
        } else if (permissionSummary.summary == PermissionManager.PermissionSummary.TRAIL) {
            //试用中，弹窗提示剩余多少天
            LogUtil.d(TAG, "## 本地权限摘要结果:试用中");
            boolean expired = permissionSummary.expired;
            int numberOfDay = permissionSummary.numberOfDay;
            showDialog_checkResult(context, expired, numberOfDay);
        } else { //if (permissionSummary.summary == PermissionManager.PermissionSummary.NO_PERSSION) {
            //权限都过期而未买过，弹窗提示权限过期
            LogUtil.d(TAG, "## 本地权限摘要结果:权限都过期而未买过");
            boolean expired = permissionSummary.expired;
            int numberOfDay = permissionSummary.numberOfDay;
            showDialog_checkResult(context, expired, numberOfDay);
        }

//        session.put(KEY_FOR_HAS_CHECKED_ON_START, Boolean.TRUE);
    }

    private void showDialog_checkResult(Context context, boolean expired, int numberOfDay) {
        LogUtil.d(TAG, "## 准备展示页面：试用中或者过期");
        Intent intent = new Intent(context, PermissionTrialAlertDialog.class);
        intent.putExtra("expired", expired);
        intent.putExtra("numberOfDay", numberOfDay);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }


    private void showDialog_DownloadFailure(Context context, String error) {
        LogUtil.d(TAG, "## 准备展示页面：下载权限失败");
        Intent intent = new Intent(context, PermissionUpdateFailureActivity.class);
        intent.putExtra("error", error);
        context.startActivity(intent);
    }

    /**
     * 是否本地的权限存储为空
     *
     * @param context
     * @return
     */
    public boolean isLocalPermissionEmpty(Context context) {
        LogUtil.d(TAG, "## 检查本地权限是否为空");
        PermissionManager permissionManager = LogicFactory.createPermissionManager(context);
        List<ObdRightBean.ObdRight> permissonList = permissionManager.getPermissonList();
        return permissonList == null || permissonList.size() == 0;
    }
}
