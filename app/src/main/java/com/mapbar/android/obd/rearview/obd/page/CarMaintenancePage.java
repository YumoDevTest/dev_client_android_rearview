package com.mapbar.android.obd.rearview.obd.page;


import android.app.DatePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.common.StringUtil;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.widget.CircleDrawable;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.mapdal.DateTime;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.MaintenanceError;
import com.mapbar.obd.MaintenanceState;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by liuyy on 2016/5/7.
 */
public class CarMaintenancePage extends AppPage implements View.OnClickListener {

    UserCar userCar;
    @ViewInject(R.id.line_upkeep)
    private LinearLayout line_upkeep;

    @ViewInject(R.id.line_upkeep_revise)
    private RelativeLayout line_upkeep_revise;

    @ViewInject(R.id.et_totalMileage)
    private EditText et_totalMileage;

    @ViewInject(R.id.et_lastMaintenanceMileage)
    private EditText et_lastMaintenanceMileage;

    @ViewInject(R.id.et_purchaseDate)
    private TextView et_purchaseDate;

    @ViewInject(R.id.et_lastMaintenanceDate)
    private TextView et_lastMaintenanceDate;

    @ViewInject(R.id.btn_save)
    private Button btn_save;

    @ViewInject(R.id.btn_alreadyUpkeep)
    private Button btn_alreadyUpkeep;

    @ViewInject(R.id.tv_next_mileage)
    private TextView tv_next_mileage;

    @ViewInject(R.id.tv_next_time)
    private TextView tv_next_time;

    @ViewInject(R.id.tv_upkeep_totle_item)
    private TextView tv_upkeep_totle_item;

    @ViewInject(R.id.view_upkeep)
    private ImageView view_upkeep;

    @ViewInject(R.id.view_upkeep_time)
    private ImageView view_upkeep_time;

    @ViewInject(R.id.tv_upkeep_totle_cost)
    private TextView tv_upkeep_totle_cost;

    @ViewInject(R.id.tv)
    private TextView tv;

    @ViewInject(R.id.tv2)
    private TextView tv2;
    private Calendar mCalendar;
    private MaintenanceState maintenanceState;
    private boolean boolPurchaseDate = false;
    private boolean boolLastMaintenanceDate = false;

    private CircleDrawable circleDrawable;
    private short year;
    private short month;
    private short day;
    private long overdueTime;
    private long nextDay;
    private long nextUpkeepDate;
    private DatePickerDialog.OnDateSetListener mBuyDateListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            et_purchaseDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            DateTime time_buy = new DateTime();
            time_buy.year = (short) year;
            time_buy.month = (short) (monthOfYear + 1);
            time_buy.day = (short) dayOfMonth;
            userCar.purchaseDate = time_buy;
        }
    };
    private DatePickerDialog.OnDateSetListener mUpKeepDateListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            et_lastMaintenanceDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            DateTime time_buy = new DateTime();
            time_buy.year = (short) year;
            time_buy.month = (short) (monthOfYear + 1);
            time_buy.day = (short) dayOfMonth;
            userCar.lastMaintenanceDate = time_buy;
        }
    };

    @Override
    public void initView() {
        mCalendar = Calendar.getInstance();
        LocalUserCarResult localUserCar = Manager.getInstance().queryLocalUserCar();
//        Manager.getInstance().queryRemoteMaintenanceInfo();
        UserCar[] userCars = localUserCar.userCars;
        if (userCars.length > 0) {
            userCar = localUserCar.userCars[0];
            if (userCar.purchaseDate.isValid()) {
                boolPurchaseDate = true;
                et_purchaseDate.setText("" + userCar.purchaseDate.year + "-" + userCar.purchaseDate.month + "-" + userCar.purchaseDate.day);
            } else {
                boolPurchaseDate = false;
                et_purchaseDate.setText("未设置");
            }

            if (userCar.lastMaintenanceDate.isValid()) {
                boolLastMaintenanceDate = true;
                et_lastMaintenanceDate.setText("" + userCar.lastMaintenanceDate.year + "-" + userCar.lastMaintenanceDate.month + "-" + userCar.lastMaintenanceDate.day);
            } else {
                boolLastMaintenanceDate = false;
                et_lastMaintenanceDate.setText("未设置");
            }

            et_totalMileage.setText(userCar.totalMileage / 1000 + "");
            et_lastMaintenanceMileage.setText(userCar.lastMaintenanceMileage / 1000 + "");
        } else {
        }


    }

    @Override
    public void onResume() {
        Manager.getInstance().queryRemoteMaintenanceInfo();
        // 日志
        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
            Log.v(LogTag.TEMP, "queryRemoteMaintenanceInfo -->>");
            Log.v(LogTag.TEMP, "onResume -->>");
        }
        super.onResume();
    }

    @Override
    public void setListener() {
        et_purchaseDate.setOnClickListener(this);
        btn_alreadyUpkeep.setOnClickListener(this);
        et_lastMaintenanceDate.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {

                switch (event) {
                    case Manager.Event.queryRemoteMaintenanceInfoSucc:
                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                            Log.v(LogTag.TEMP, "queryRemoteMaintenanceInfoSucc -->>");
                            Log.v(LogTag.TEMP, "Object -->>" + o);
                        }
                        line_upkeep_revise.setVisibility(View.GONE);
                        line_upkeep.setVisibility(View.VISIBLE);
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
                                // 日志
                                if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                                    Log.v(LogTag.TEMP, "invalid -->>");
                                }
                                // MaintenanceState对象无效
                                break;
                            case MaintenanceState.Tag.nextMaintenanceDateEstimatedByMileage:
                                // 表示未过保，并且下次保养日期是用里程估算得到的
                                steData(maintenanceState);

                                break;
                            case MaintenanceState.Tag.nextMaintenanceDateEstimatedByTime:
                                steData(maintenanceState);
                                break;
                            case MaintenanceState.Tag.overdue:
                                if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                                    Log.v(LogTag.TEMP, "overdue -->>");
                                }
                                setOverdueData(maintenanceState);
                                break;
                            default:
                                break;
                        }
                        break;
                    case Manager.Event.queryRemoteMaintenanceInfoFailed:
                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                            Log.v(LogTag.TEMP, "queryRemoteMaintenanceInfoFailed -->>");
                            Log.v(LogTag.TEMP, "Object -->>" + o);
                        }
                        MaintenanceError error = (MaintenanceError) o;
                        if (10 == error.errCode) {
                            //未填信息
                            line_upkeep_revise.setVisibility(View.VISIBLE);
                            line_upkeep.setVisibility(View.GONE);
                        } else {
                            StringUtil.toastStringShort(error.errMsg);
                        }
                        break;
                    case Manager.Event.carInfoUploadSucc:
                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                            Log.v(LogTag.TEMP, "carInfoUploadSucc -->>");
                        }
                        StringUtil.toastStringShort("设置成功");


                        break;
                    case Manager.Event.carInfoUploadFailed:
                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                            Log.v(LogTag.TEMP, "carInfoUploadFailed -->>");
                            Log.v(LogTag.TEMP, "O -->>" + o);
                        }
                        StringUtil.toastStringShort("设置失败");
                        break;
                    case Manager.Event.carInfoWriteDatabaseSucc:
                        break;
                    case Manager.Event.carInfoWriteDatabaseFailed:
                        break;
                }
                super.onEvent(event, o);
            }
        };
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_purchaseDate:
                if (boolPurchaseDate) {
                    String str = et_purchaseDate.getText().toString();
                    String time[] = str.split("-");
                    int year = Integer.parseInt(time[0]);
                    int monty = (Integer.parseInt(time[1]) - 1);
                    int date = Integer.parseInt(time[2]);
                    new MyDatePickerDialog(getContext(), mBuyDateListener, year, monty, date).show();
                } else {
                    new MyDatePickerDialog(getContext(), mBuyDateListener, mCalendar.get(Calendar.YEAR),
                            mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE)).show();
                }
                break;
            case R.id.et_lastMaintenanceDate:
                if (boolLastMaintenanceDate) {
                    String str = et_lastMaintenanceDate.getText().toString();
                    String time[] = str.split("-");
                    int year = Integer.parseInt(time[0]);
                    int monty = (Integer.parseInt(time[1]) - 1);
                    int date = Integer.parseInt(time[2]);
                    new MyDatePickerDialog(getContext(), mUpKeepDateListener, year, monty, date).show();
                } else {
                    new DatePickerDialog(getContext(), mUpKeepDateListener, mCalendar.get(Calendar.YEAR),
                            mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                break;
            case R.id.btn_save:
                if (et_purchaseDate.getText().equals(getContext().getResources().getString(R.string.isnotset)) ||
                        et_lastMaintenanceDate.getText().equals(getContext().getResources().getString(R.string.isnotset))
                        || TextUtils.isEmpty(et_totalMileage.getText().toString()) || TextUtils
                        .isEmpty(et_lastMaintenanceMileage.getText().toString())) {
                    StringUtil.toastStringShort("信息不完整");
                } else {
                    userCar.totalMileage = Integer.valueOf(et_totalMileage.getText().toString()) * 1000;
                    userCar.lastMaintenanceMileage = Integer.valueOf(et_lastMaintenanceMileage.getText().toString()) * 1000;
                    Manager.getInstance().setUserCar(userCar);
                    Manager.getInstance().queryRemoteMaintenanceInfo();
                    // 日志
                    if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                        Log.v(LogTag.TEMP, "setUserCar -->>");
                    }
                }
                break;
            case R.id.btn_alreadyUpkeep:
                line_upkeep_revise.setVisibility(View.VISIBLE);
                line_upkeep.setVisibility(View.GONE);
                break;

        }

    }

    private void steData(MaintenanceState maintenanceState) {
        tv_next_mileage.setText(String.valueOf(maintenanceState.getMileageToMaintenance() / 1000));
        tv_next_time.setText(String.valueOf(nextDay));
        tv_upkeep_totle_item.setText("下次保养项目 共" + maintenanceState.getTasks().length + "项");
        tv_upkeep_totle_cost.setText("预计费用 " + maintenanceState.getTotalPrice() + " 元");
        circleDrawable = new CircleDrawable(getContext());
        circleDrawable.setCricleProgressColor(getContext().getResources().getColor(R.color.upkeep_progress));
        circleDrawable.setCircleWidth(9);
        circleDrawable.setProgress((int) maintenanceState.getProgressPercentage());
        view_upkeep_time.setImageDrawable(circleDrawable);
        view_upkeep.setImageDrawable(circleDrawable);
    }

    private void setOverdueData(MaintenanceState maintenanceState) {
        tv.setText("超过保养里程");
        tv_next_mileage.setText(maintenanceState.getOverdueMileage() / 1000 + "");
        tv2.setText("超过保养期限");
        overdueTime = maintenanceState.getOverdueTime() / 86400000;
        tv_next_time.setText(String.valueOf(overdueTime));
        tv_upkeep_totle_item.setText("下次保养项目 共" + maintenanceState.getTasks().length + "项");
        tv_upkeep_totle_cost.setText("预计费用 " + maintenanceState.getTotalPrice() + " 元");
        circleDrawable = new CircleDrawable(getContext());
        circleDrawable.setCricleProgressColor(getContext().getResources().getColor(R.color.upkeep_progress));
        circleDrawable.setCircleWidth(9);
        circleDrawable.setProgress((int) maintenanceState.getProgressPercentage());
        view_upkeep_time.setImageDrawable(circleDrawable);
        view_upkeep.setImageDrawable(circleDrawable);
    }
}

class MyDatePickerDialog extends DatePickerDialog {

    public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }
}
