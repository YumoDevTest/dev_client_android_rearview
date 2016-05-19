package com.mapbar.android.obd.rearview.obd.page;


import android.app.DatePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.common.StringUtil;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.mapdal.DateTime;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.MaintenanceError;
import com.mapbar.obd.MaintenanceState;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCar;

import java.util.Calendar;

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
    @ViewInject(R.id.tv_next_mileage)
    private TextView tv_next_mileage;
    @ViewInject(R.id.tv_next_time)
    private TextView tv_next_time;
    private Calendar mCalendar;
    private MaintenanceState maintenanceState;
    private boolean boolPurchaseDate = false;
    private boolean boolLastMaintenanceDate = false;
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
        }
        super.onResume();
    }

    @Override
    public void setListener() {
        et_purchaseDate.setOnClickListener(this);
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
                        maintenanceState = (MaintenanceState) o;
                        int tag = maintenanceState.getTag();
                        switch (tag) {
                            case MaintenanceState.Tag.invalid:
                                // MaintenanceState对象无效
                                break;
                            case MaintenanceState.Tag.nextMaintenanceDateEstimatedByMileage:
                                // 表示未过保，并且下次保养日期是用里程估算得到的
                                double progressPercentage = maintenanceState.getProgressPercentage();
                                tv_next_mileage.setText(maintenanceState.getMileageToMaintenance() / 1000 + "");
                                break;
                            case MaintenanceState.Tag.nextMaintenanceDateEstimatedByTime:
                                // 表示未过保，并且下次保养日期是用时间估算得到的
                                /*rb_byMileage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mAif.showAlert(R.string.bytime);
                                    }
                                });
                                if (nextDay < 20) {
                                    tv_kilometer.setTextColor(mContext.getResources().getColor(R.color.dark_yellow));
                                    tv_unit.setTextColor(mContext.getResources().getColor(R.color.dark_yellow));
                                } else {
                                    tv_kilometer.setTextColor(mContext.getResources().getColor(R.color.dark_green));
                                    tv_unit.setTextColor(mContext.getResources().getColor(R.color.dark_green));
                                }
                                tv_time.setText(mContext.getResources().getString(R.string.nextupkeep_date) + year + "-" + month + "-" + day);
                                tv_kilometer.setText(nextDay + "");
                                tv_unit.setText(R.string.tian);
                                rb_byTime.setChecked(true);
                                break;
                            case MaintenanceState.Tag.overdue:
                                // 表示过保了
                                tv_time.setVisibility(View.GONE);
                                if (state.getOverdueTime() > 0) {
                                    // 时间过保
                                    mHandler.sendEmptyMessage(1);
                                }
                                if (state.getOverdueMileage() > 0) {
                                    // 里程超过保养
                                    mHandler.sendEmptyMessage(2);
                                }
                                rg_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                                        switch (checkedId) {
                                            case R.id.rb_byMileage:
                                                com.mapbar.android.log.Log.i("zc", "guobaorb_byMileage");
//							rb_byMileage.setTextColor(mContext.getResources().getColor(R.color.calendar_active_month_bg));
//							rb_byTime.setTextColor(mContext.getResources().getColor(R.color.dark_green));
                                                tv_last.setText(R.string.OverdueMileage);
                                                tv_kilometer.setText(state.getOverdueMileage() / 1000 + "");
                                                tv_unit.setText("km");
                                                break;
                                            case R.id.rb_byTime:
                                                com.mapbar.android.log.Log.i("zc", "guobaorb_byTime");
//							rb_byTime.setTextColor(mContext.getResources().getColor(R.color.calendar_active_month_bg));
//							rb_byMileage.setTextColor(mContext.getResources().getColor(R.color.dark_green));
                                                tv_last.setText(R.string.OverdueTime);
                                                tv_kilometer.setText(overdueTime + "");
                                                tv_unit.setText(mContext.getResources().getString(R.string.tian));
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                });

                                btu_alreadyupkeep.setVisibility(View.VISIBLE);
                                int count = state.getOverduePeriodCount();
                                String str1 = mContext.getString(R.string.noinfo);
                                String str2 = mContext.getResources().getString(R.string.noinfo2);
                                tv_noinfo.setText(Html.fromHtml(str1 + "<font color=\"#ff5777\">" + count + "</font>" + str2));*/
                                break;
                            default:
                                break;
                        }
                        tv_next_mileage.setText(String.valueOf(maintenanceState
                                .getMileageToMaintenance() / 1000));
//                        tv_next_time.setText();
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
                        }
                        break;
                    case Manager.Event.carInfoUploadSucc:
                        break;
                    case Manager.Event.carInfoUploadFailed:
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
                    // 日志
                    if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                        Log.v(LogTag.TEMP, "btn_save -->>");
                    }
                    userCar.totalMileage = Integer.valueOf(et_totalMileage.getText().toString()) * 1000;
                    userCar.lastMaintenanceMileage = Integer.valueOf(et_lastMaintenanceMileage.getText().toString()) * 1000;
                    Manager.getInstance().setUserCar(userCar);
                }

                break;

        }

    }


}

class MyDatePickerDialog extends DatePickerDialog {

    public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }
}
