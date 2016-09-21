package com.mapbar.android.obd.rearview.modules.permission;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.mapbar.android.obd.rearview.util.Utils;
import com.mapbar.android.obd.rearview.modules.permission.model.PermissionStatus;
import com.mapbar.obd.foundation.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.modules.push.events.PermissionChangedEvent;
import com.mapbar.android.obd.rearview.modules.permission.repo.PermissionRepository;
import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.android.obd.rearview.lib.config.Urls;
import com.mapbar.box.protobuf.bean.ObdRightBean;
import com.mapbar.obd.foundation.oknetpb.HttpPBCallback;
import com.mapbar.obd.foundation.oknetpb.HttpPBUtil;
import com.mapbar.obd.foundation.oknetpb.PBErrorCode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 权限管理类
 * Created by zhangyunfei on 16/8/4.
 */
public class PermissionManagerImpl implements PermissionManager {
    private static final String TAG = PermissionManagerImpl.class.getSimpleName();
    private PermissionRepository permissionRepository;
    private static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    public PermissionManagerImpl(Context context) {
        permissionRepository = new PermissionRepository(context);
        permissionRepository.setChangedListener(changedListener);
    }

    /**
     * 本地权限发生了改变后，发出通知
     */
    private PermissionRepository.ChangedListener changedListener = new PermissionRepository.ChangedListener() {
        @Override
        public void onChanged() {
            //通知权限发生了变化
            EventBusManager.post(new PermissionChangedEvent());
        }
    };

    @Override
    public void downloadPermissionList(final DownloadPermissionCallback callback) {
        String imei = getImei();
        LogUtil.d(TAG, "## 启动下载权限，imei=" + imei);
        ObdRightBean.ObdRightRequest obdRightRequest;
        obdRightRequest = ObdRightBean.ObdRightRequest.newBuilder().setImei(imei).build();

        HttpPBUtil.post(Urls.PERMISSION_DOWNLOAD, obdRightRequest, new HttpPBCallback() {
            @Override
            public void onFailure(Exception e) {
                if (callback != null)
                    callback.onFailure(e);
            }

            @Override
            public void onSuccess(byte[] bytes2) {
                ObdRightBean.ObdRightResponse obdProductResponse = null;
                try {
                    obdProductResponse = ObdRightBean.ObdRightResponse.parseFrom(bytes2);

                    int code = obdProductResponse.getCode();
                    ByteString msg = obdProductResponse.getMsg();
                    String msgStr = msg.toStringUtf8();

                    if (PBErrorCode.OK != code) {
                        throw new Exception("网络访问异常，错误码" + code);
                    }

                    ByteString data = obdProductResponse.getData();
                    Log.i(TAG, obdProductResponse.toString());

                    ObdRightBean.ObdRightData obdProductData = ObdRightBean.ObdRightData.parseFrom(data);
//                    long serverTime = obdProductData.getServerTime();
//                    long modifyTime = obdProductData.getModifyTime();
//                    String imei1 = obdProductData.getImei();
                    final List<ObdRightBean.ObdRight> obdRightList = obdProductData.getObdRightList();
                    LogUtil.d(TAG, "## 下载权限 成功");
                    //将权限信息中,保存到数据库
                    new AsyncTask<Void, Void, Boolean>() {
                        private Exception exception;

                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            try {
                                permissionRepository.saveAndReplacePermission(getImei(), obdRightList);
                                LogUtil.d(TAG, "## 保存权限到本地 成功");
                                permissionRepository.clearCache();
                                LogUtil.d(TAG, "## 清理本地权限 成功");
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                exception = e;
                                LogUtil.d(TAG, "## 保存权限到本地 失败");
                                return false;
                            }
                        }

                        @Override
                        protected void onPostExecute(Boolean isok) {
                            if (isok) {
                                if (callback != null)
                                    callback.onSuccess(obdRightList);
                            } else {
                                if (callback != null)
                                    callback.onFailure(exception);
                            }
                        }
                    }.execute();

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "## " + e.getMessage(), e);
                    if (callback != null)
                        callback.onFailure(e);
                }
            }
        });
    }

    /**
     * 检查权限
     *
     * @param permissionKey
     * @return
     */
    @Override
    public PermissionResult checkPermission(int permissionKey) {
//        LogUtil.d(TAG, "## 准备 检查本地权限");
        List<ObdRightBean.ObdRight> permissonList = permissionRepository.getPermissonList(getImei());
        if (permissonList == null) {
            return new PermissionResult(false, true, 0, permissionKey);
        }
//        boolean isValid = false;// 有效
//        boolean isTrial = false;// 试用
        ObdRightBean.ObdRight obdRight = null;
        ObdRightBean.ObdRight tmp = null;
        for (int i = 0; i < permissonList.size(); i++) {
            tmp = permissonList.get(i);
            if (tmp != null && tmp.getProductId().equals(permissionKey + "")) {
                obdRight = tmp;
                break;
            }
        }
        if (obdRight == null) {
            LogUtil.d(TAG, "## 检查本地权限" + permissionKey + ",结果=无权限");
            return new PermissionResult(false, true, 0, permissionKey);
        }
        //免费
        if (obdRight.getProducteStatus() == PermissionStatus.FREE) {
            LogUtil.d(TAG, "## 检查本地权限" + permissionKey + ",结果=免费");
            return new PermissionResult(true, false, Integer.MAX_VALUE, permissionKey);
        }
        //试用,或者已购买，要判断过期
        if (obdRight.getProducteStatus() == PermissionStatus.TRIAL
                || obdRight.getProducteStatus() == PermissionStatus.BUYED) {
            int trialDayOfReset = getDayOfReset(obdRight);
            boolean expired = trialDayOfReset <= 0;
            LogUtil.d(TAG, "## 检查本地权限" + permissionKey + ",结果=试用或已购买，剩余时长=" + trialDayOfReset);
            return new PermissionResult(true, expired, trialDayOfReset, permissionKey);
        }
//        if (permissionKey == PermissionKey.PERMISSION_CAR_STATE)
//            return new PermissionResult(isValid, true, 10, permissionKey);
        //default
        LogUtil.d(TAG, "## 检查本地权限" + permissionKey + ",结果=无权限");
        return new PermissionResult(false, true, 0, permissionKey);
    }

    /**
     * 获得权限概要,是过期，试用，或者购买过
     *
     * @return
     */
    @Override
    public PermissionSummary getPermissionSummary() {
//        LogUtil.d(TAG, "## 获得权限概要");
        List<ObdRightBean.ObdRight> permissonList = permissionRepository.getPermissonList(getImei());
        //一条也没有，认为过期
        if (permissonList == null) {
            return new PermissionSummary(PermissionSummary.NO_PERSSION);
        }

        //判断已购
        boolean foundBuyed = false;
        ObdRightBean.ObdRight obdRight = null;
        for (int i = 0; i < permissonList.size(); i++) {
            obdRight = permissonList.get(i);
            //只要遇到 有买过的
            if (obdRight.getProducteStatus() == PermissionStatus.BUYED) {
                foundBuyed = true;
                break;
            }
        }
        if (foundBuyed) {
            int trialDayOfReset = getDayOfReset(obdRight);
            PermissionSummary permissionSummary = new PermissionSummary(PermissionSummary.HAS_PAY);
            permissionSummary.numberOfDay = trialDayOfReset;
            permissionSummary.expired = trialDayOfReset <= 0;
            return permissionSummary;
        }

        //判断试用
        boolean foundTrial = false;
        for (int i = 0; i < permissonList.size(); i++) {
            obdRight = permissonList.get(i);
            if (obdRight.getProducteStatus() == PermissionStatus.TRIAL) {
                //只要有一个是试用
                foundTrial = true;
                break;
            }
        }
        if (foundTrial) {
            int trialDayOfReset = getDayOfReset(obdRight);
            PermissionSummary permissionSummary = new PermissionSummary(PermissionSummary.TRAIL);
            permissionSummary.numberOfDay = trialDayOfReset;
            permissionSummary.expired = trialDayOfReset <= 0;
            return permissionSummary;
        }
        //defalut
        return new PermissionSummary(PermissionSummary.HAS_PAY);
    }


    /**
     * 计算出 剩余多少天过期
     * 用目标时间减去当前时间
     *
     * @param obdRight
     * @return
     */
    public static int getDayOfReset(ObdRightBean.ObdRight obdRight) {
        String date = obdRight.getDeadline();
        Calendar calendarTarget;
        try {
            Date dateTarget = null;
            dateTarget = SIMPLE_DATE_FORMAT.parse(date);
            calendarTarget = Calendar.getInstance();
            calendarTarget.setTimeInMillis(dateTarget.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "日期格式异常" + e.getMessage(), e);
            return -1;
        }
        Calendar calendarNow = Calendar.getInstance();
        long numberOfDay = (calendarTarget.getTimeInMillis() - calendarNow.getTimeInMillis()) / (1000 * 3600 * 24);
        return (int) numberOfDay;
    }

    public List<ObdRightBean.ObdRight> getPermissonList() {
        LogUtil.d(TAG, "## 获得权限列表");
        return permissionRepository.getPermissonList(getImei());
    }

    private String getImei() {
//        return "311111-22-333333";
        return Utils.getImei(MyApplication.getInstance());
    }
}


/*


111111-22-333333 1001，1002，1003 全部在试用中 ，1004，1005免费
211111-22-333333 1001已购买。1002，1003在试用中，1004，1005免费
311111-22-333333 1001过期，1002，1003在试用中，1004，1005免费
411111-22-333333 1001，1002，1003 全部购买，1004，1005免费


* */
