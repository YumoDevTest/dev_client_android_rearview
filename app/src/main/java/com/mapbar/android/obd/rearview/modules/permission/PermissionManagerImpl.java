package com.mapbar.android.obd.rearview.modules.permission;

import android.content.Context;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mapbar.android.obd.rearview.lib.net.ErrorCode;
import com.mapbar.android.obd.rearview.lib.net.HttpPBUtil;
import com.mapbar.android.obd.rearview.modules.permission.repo.PermissionRepository;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.android.obd.rearview.obd.util.Urls;
import com.mapbar.box.protobuf.bean.ObdRightBean;

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
    }

    @Override
    public void downloadPermissionList(final DownloadPermissionCallback callback) {

        String imei = "221111-22-333333";
//        imei = Utils.getImei(Application.getInstance());
        ObdRightBean.ObdRightRequest obdRightRequest;
        obdRightRequest = ObdRightBean.ObdRightRequest.newBuilder().setImei(imei).build();

        HttpPBUtil.post(Urls.PERMISSION_DOWNLOAD_PERMISSIONS, obdRightRequest, new HttpPBUtil.HttpPBCallback() {
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

                    if (ErrorCode.OK != code) {
                        Log.i(TAG, "error: " + msgStr);
                        return;
                    } else {
                        Log.i(TAG, "OK : " + msgStr);
                    }

                    ByteString data = obdProductResponse.getData();
                    Log.i(TAG, obdProductResponse.toString());

                    ObdRightBean.ObdRightData obdProductData = ObdRightBean.ObdRightData.parseFrom(data);
                    long serverTime = obdProductData.getServerTime();
                    long modifyTime = obdProductData.getModifyTime();
                    String imei1 = obdProductData.getImei();
                    List<ObdRightBean.ObdRight> obdRightList = obdProductData.getObdRightList();

                    //TODO 将权限信息中,保存到数据库
                    try {
                        permissionRepository.saveAndReplacePermission(obdRightList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (callback != null)
                        callback.onSuccess(obdRightList);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage(), e);
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
        List<ObdRightBean.ObdRight> permissonList = permissionRepository.getPermissonList();
        if (permissonList == null) {
            return new PermissionResult(false, true, 0, permissionKey);
        }
        boolean isValid = false;// 有效
        boolean isTrial = false;// 试用
        ObdRightBean.ObdRight obdRight = null;
        for (int i = 0; i < permissonList.size(); i++) {
            obdRight = permissonList.get(i);
            if (obdRight != null && obdRight.getProductId().equals(permissionKey + "")) {
                break;
            }
        }
        if (obdRight == null) {
            return new PermissionResult(false, true, 0, permissionKey);
        }
        //免费
        if (obdRight.getProducteStatus() == PermissionStatus.FREE) {
            return new PermissionResult(true, false, Integer.MAX_VALUE, permissionKey);
        }
        //试用,或者已购买，要判断过期
        if (obdRight.getProducteStatus() == PermissionStatus.TRIAL
                || obdRight.getProducteStatus() == PermissionStatus.BUYED) {
            int trialDayOfReset = getDayOfReset(obdRight);
            boolean expired = trialDayOfReset <= 0;
            return new PermissionResult(true, expired, trialDayOfReset, permissionKey);
        }
//        if (permissionKey == PermissionKey.PERMISSION_CAR_STATE)
//            return new PermissionResult(isValid, true, 10, permissionKey);
        //default
        return new PermissionResult(false, true, 0, permissionKey);
    }

    /**
     * 获得权限概要,是过期，试用，或者购买过
     *
     * @return
     */
    @Override
    public PermissionSummary getPermissionSummary() {
        List<ObdRightBean.ObdRight> permissonList = permissionRepository.getPermissonList();
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
        Date dateTarget = null;
        try {
            dateTarget = SIMPLE_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "日期格式异常" + e.getMessage(), e);
            return -1;
        }
        Calendar calendar = Calendar.getInstance();
        Date dateNow = new Date(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        long numberOfDay = (dateTarget.getTime() - dateNow.getTime()) / (1000 * 3600 * 24);
        return (int) numberOfDay;
    }
}
