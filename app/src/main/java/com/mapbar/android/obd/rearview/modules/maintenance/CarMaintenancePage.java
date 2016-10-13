package com.mapbar.android.obd.rearview.modules.maintenance;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.base.AppPage2;
import com.mapbar.android.obd.rearview.lib.services.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.lib.umeng.UmengConfigs;
import com.mapbar.android.obd.rearview.modules.checkup.UpkeepItemAdapter;
import com.mapbar.android.obd.rearview.modules.maintenance.contract.IMaintenanceView;
import com.mapbar.android.obd.rearview.modules.permission.PermissionAlertViewAdapter;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAdatper;
import com.mapbar.android.obd.rearview.util.LayoutUtils;
import com.mapbar.android.obd.rearview.util.StringUtil;
import com.mapbar.android.obd.rearview.views.CircleDrawable;
import com.mapbar.android.obd.rearview.views.TitleBarView;
import com.mapbar.mapdal.DateTime;
import com.mapbar.obd.MaintenanceError;
import com.mapbar.obd.MaintenanceInfo;
import com.mapbar.obd.MaintenanceResult;
import com.mapbar.obd.MaintenanceState;
import com.mapbar.obd.MaintenanceTask;
import com.mapbar.obd.Manager;
import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.foundation.umeng.MobclickAgentEx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 保养 页
 * Created by liuyy on 2016/5/7.
 */
public class CarMaintenancePage extends AppPage2 implements View.OnClickListener, IMaintenanceView {
    private Button btn_alreadyUpkeep;
    private TextView tv_next_mileage;
    private TextView tv_next_time;
    private TextView tv_upkeep_totle_item;
    private ImageView view_upkeep;
    private ImageView view_upkeep_time;
    private TextView tv_upkeep_totle_cost;
    private TextView tv;
    private TextView tv2;
    private RecyclerView rl_view;
    private MaintenanceState maintenanceState;
    private CircleDrawable circleDrawable;
    private short year;
    private short month;
    private short day;
    private long overdueTime;
    private long nextDay;
    private long nextUpkeepDate;
    private UpkeepItemAdapter upkeepItemAdapter;
    private TitleBarView titlebarview1;
    private MaintenancePresenter presenter;
    private IPermissionAlertViewAdatper permissionAlertAbleAdapter;
    private OBDSDKListenerManager.SDKListener sdkListener;
    private static final int REQUEST_CARMAINTENANCEREVISE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentView() == null) {
            createContenttView(R.layout.page_upkeep);
            initView();
            getLocalSchemeCache();
        }
        return getContentView();
    }

    public void initView() {
        btn_alreadyUpkeep = (Button) getContentView().findViewById(R.id.btn_alreadyUpkeep);
        tv_next_mileage = (TextView) getContentView().findViewById(R.id.tv_next_mileage);
        tv_next_time = (TextView) getContentView().findViewById(R.id.tv_next_time);
        tv_upkeep_totle_item = (TextView) getContentView().findViewById(R.id.tv_upkeep_totle_item);
        view_upkeep = (ImageView) getContentView().findViewById(R.id.view_upkeep);
        view_upkeep_time = (ImageView) getContentView().findViewById(R.id.view_upkeep_time);
        tv_upkeep_totle_cost = (TextView) getContentView().findViewById(R.id.tv_upkeep_totle_cost);
        tv = (TextView) getContentView().findViewById(R.id.tv);
        tv2 = (TextView) getContentView().findViewById(R.id.tv2);
        rl_view = (RecyclerView) getContentView().findViewById(R.id.rl_view);
        titlebarview1 = (TitleBarView) getContentView().findViewById(R.id.titlebarview1);
        titlebarview1.setTitle(R.string.page_title_baoyang);
        titlebarview1.setButtonRightText("保养校正");
        titlebarview1.setButtonRightVisibility(true);
        titlebarview1.setButtonRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgentEx.onEvent(getActivity(), UmengConfigs.SETMAINTENANCE);
                startActivityForResult(new Intent(getActivity(), CarMaintenanceReviseActivity.class),REQUEST_CARMAINTENANCEREVISE);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rl_view.setLayoutManager(layoutManager);
        presenter = new MaintenancePresenter(this);
        setListener();
    }

    /**
     * 获取本地保养信息
     */
    private void getLocalSchemeCache() {
        // 获取本地缓存保养数据
        MaintenanceInfo localSchemeCache = Manager.getInstance().queryMaintenanceInfoByLocalSchemeCache();
        switch (localSchemeCache.errCode) {
            case MaintenanceResult.noSchemeFound:
                // 本地没有保养信息,网络获取保养信息
                Manager.getInstance().queryRemoteMaintenanceInfo();
                break;
            case MaintenanceResult.ok:
                DateTime mDate = localSchemeCache.state.getNextMaintenanceDate();
                year = mDate.year;
                month = mDate.month;
                day = mDate.day;
                String.valueOf(year);
                String data2 = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d = null;
                try {
                    d = sdf.parse(data2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                nextUpkeepDate = c.getTimeInMillis();
                Date nowDate = new Date();
                long time = nowDate.getTime();
                if (time < nextUpkeepDate) {
                    nextDay = (nextUpkeepDate - time) / 86400000;
                }
                int tag = localSchemeCache.state.getTag();
                switch (tag) {
                    case MaintenanceState.Tag.invalid:
                        break;
                    case MaintenanceState.Tag.nextMaintenanceDateEstimatedByMileage:
                        // 表示未过保，并且下次保养日期是用里程估算得到的
                        steData(localSchemeCache.state);
                        break;
                    case MaintenanceState.Tag.nextMaintenanceDateEstimatedByTime:
                        steData(localSchemeCache.state);
                        break;
                    case MaintenanceState.Tag.overdue:
                        setOverdueData(localSchemeCache.state);
                        break;
                    default:
                        break;
                }
                break;
            case MaintenanceResult.carGenerationNotSpecified:
                StringUtil.toastStringShort("尚未指定车型");
                break;
            case MaintenanceResult.networkRequestCancelled:
                StringUtil.toastStringShort("网络请求被取消了");
                break;
            case MaintenanceResult.networkRequestFailed:
                StringUtil.toastStringShort("网络请求失败");
                break;
            case MaintenanceResult.networkResponseEmpty:
                StringUtil.toastStringShort("网络请求得到的响应为空");
                break;
            case MaintenanceResult.networkResponseError:
                StringUtil.toastStringShort("网络请求得到的响应有误");
                break;
            case MaintenanceResult.notLoggedIn:
                StringUtil.toastStringShort("当前未登录");
                break;
            case MaintenanceResult.outOfData:
                StringUtil.toastStringShort("行驶里程超出了该车保养范围");
                break;
            case MaintenanceResult.parameterError:
                startActivityForResult(new Intent(getActivity(), CarMaintenanceReviseActivity.class),REQUEST_CARMAINTENANCEREVISE);
                StringUtil.toastStringShort("保养参数有误");
                break;
            case MaintenanceResult.parameterIncomplete:
                startActivityForResult(new Intent(getActivity(), CarMaintenanceReviseActivity.class),REQUEST_CARMAINTENANCEREVISE);
                StringUtil.toastStringShort("保养参数不完整");
                break;
            default:
                break;
        }

    }

    @Override
    public void onResume() {

        super.onResume();

        if (presenter != null) presenter.checkPermission();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.clear();
            presenter = null;
        }
        super.onDestroy();
    }

    public void setListener() {
        btn_alreadyUpkeep.setOnClickListener(this);
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {

                switch (event) {
                    case Manager.Event.queryRemoteMaintenanceInfoSucc:
                        maintenanceState = (MaintenanceState) o;
                        DateTime mDate = maintenanceState.getNextMaintenanceDate();
                        year = mDate.year;
                        month = mDate.month;
                        day = mDate.day;
                        String.valueOf(year);
                        String data2 = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date d = null;
                        try {
                            d = sdf.parse(data2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar c = Calendar.getInstance();
                        c.setTime(d);
                        nextUpkeepDate = c.getTimeInMillis();
                        Date nowDate = new Date();
                        long time = nowDate.getTime();
                        if (time < nextUpkeepDate) {
                            nextDay = (nextUpkeepDate - time) / 86400000;
                        }
                        int tag = maintenanceState.getTag();
                        switch (tag) {
                            case MaintenanceState.Tag.invalid:
                                break;
                            case MaintenanceState.Tag.nextMaintenanceDateEstimatedByMileage:
                                // 表示未过保，并且下次保养日期是用里程估算得到的
                                steData(maintenanceState);
                                break;
                            case MaintenanceState.Tag.nextMaintenanceDateEstimatedByTime:
                                steData(maintenanceState);
                                break;
                            case MaintenanceState.Tag.overdue:
                                setOverdueData(maintenanceState);
                                break;
                            default:
                                break;
                        }
                        break;
                    case Manager.Event.queryRemoteMaintenanceInfoFailed:
                        MaintenanceError error = (MaintenanceError) o;
                        if (10 == error.errCode) {
                            //未填信息
                            startActivityForResult(new Intent(getActivity(), CarMaintenanceReviseActivity.class),REQUEST_CARMAINTENANCEREVISE);
                        } else {
                            StringUtil.toastStringShort(error.errMsg);
                        }
                        break;
                    case Manager.Event.carInfoUploadSucc:
                        break;
                }
                super.onEvent(event, o);
            }
        };
        OBDSDKListenerManager.getInstance().addSdkListener(sdkListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_alreadyUpkeep:
                MobclickAgentEx.onEvent(getActivity(), UmengConfigs.MAINTENANCED);
                startActivityForResult(new Intent(getActivity(), CarMaintenanceReviseActivity.class),REQUEST_CARMAINTENANCEREVISE);
                break;
        }
    }

    /**
     * 未过保数据
     *
     * @param maintenanceState
     */
    private void steData(MaintenanceState maintenanceState) {

        final MaintenanceTask[] tasks = maintenanceState.getTasks();
        if (0 < tasks.length) {
            upkeepItemAdapter = new UpkeepItemAdapter(getActivity(), tasks);
            rl_view.setAdapter(upkeepItemAdapter);
            rl_view.post(new Runnable() {
                @Override
                public void run() {
                    rl_view.setLayoutParams(new LinearLayout.LayoutParams((int) (tasks.length
                            * 80 *
                            (LayoutUtils.getDensity() + 0.5)), ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            });
        }
        tv.setText("距离下次保养里程");
        tv2.setText("距离下次保养时间");
        tv_next_mileage.setText(String.valueOf(maintenanceState.getMileageToMaintenance() / 1000));
        tv_next_time.setText(String.valueOf(nextDay));
        tv_upkeep_totle_item.setText("下次保养项目 共" + maintenanceState.getTasks().length + "项");
        tv_upkeep_totle_cost.setText("预计费用 " + maintenanceState.getTotalPrice() + " 元");
        circleDrawable = new CircleDrawable(getActivity());
        circleDrawable.setCricleProgressColor(getActivity().getResources().getColor(R.color.upkeep_progress));
        circleDrawable.setCircleWidth(9);
        circleDrawable.setProgress((int) maintenanceState.getProgressPercentage());
        view_upkeep_time.setImageDrawable(circleDrawable);
        view_upkeep.setImageDrawable(circleDrawable);
    }

    /**
     * 设置过保数据
     *
     * @param maintenanceState
     */
    private void setOverdueData(MaintenanceState maintenanceState) {
        final MaintenanceTask[] tasks = maintenanceState.getTasks();
        if (0 < tasks.length) {
            upkeepItemAdapter = new UpkeepItemAdapter(getActivity(), tasks);
            rl_view.setAdapter(upkeepItemAdapter);
            rl_view.post(new Runnable() {
                @Override
                public void run() {
                    rl_view.setLayoutParams(new LinearLayout.LayoutParams((int) (tasks.length
                            * 80 *
                            (LayoutUtils.getDensity() + 0.5)), ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            });
        }
        tv.setText("超过保养里程");
        tv_next_mileage.setText(maintenanceState.getOverdueMileage() / 1000 + "");
        tv2.setText("超过保养期限");
        overdueTime = maintenanceState.getOverdueTime() / 86400000;
        tv_next_time.setText(String.valueOf(overdueTime));
        tv_upkeep_totle_item.setText("下次保养项目 共" + maintenanceState.getTasks().length + "项");
        tv_upkeep_totle_cost.setText("预计费用 " + maintenanceState.getTotalPrice() + " 元");
        circleDrawable = new CircleDrawable(getActivity());
        circleDrawable.setCricleProgressColor(getActivity().getResources().getColor(R.color.upkeep_progress));
        circleDrawable.setCircleWidth(9);
        circleDrawable.setProgress((int) maintenanceState.getProgressPercentage());
        view_upkeep_time.setImageDrawable(circleDrawable);
        view_upkeep.setImageDrawable(circleDrawable);

    }

    public void showPermissionAlertView_FreeTrial(boolean isExpired, int numberOfDay) {
        if (permissionAlertAbleAdapter == null)
            permissionAlertAbleAdapter = new PermissionAlertViewAdapter(this);
        permissionAlertAbleAdapter.showPermissionAlertView_FreeTrial(isExpired, numberOfDay);
    }

    public void hidePermissionAlertView_FreeTrial() {
        if (permissionAlertAbleAdapter != null)
            permissionAlertAbleAdapter.hidePermissionAlertView_FreeTrial();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_CARMAINTENANCEREVISE == requestCode){
            if( resultCode == Activity.RESULT_OK){
                getLocalSchemeCache();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

class MyDatePickerDialog extends DatePickerDialog {

    public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }
}
