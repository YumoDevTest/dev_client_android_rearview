package com.mapbar.android.obd.rearview.modules.maintenance;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.base.AppPage2;
import com.mapbar.android.obd.rearview.modules.maintenance.contract.IMaintenanceReviseView;
import com.mapbar.android.obd.rearview.modules.maintenance.model.MaintenanceData;
import com.mapbar.android.obd.rearview.util.StringUtil;
import com.mapbar.mapdal.DateTime;

import java.util.Calendar;

/**
 * 保养校正的fragment页面
 * Created by zhangyh on 2016/9/23.
 */
public class CarMaintenanceReviseFragment extends AppPage2 implements IMaintenanceReviseView {


    private CarMaintenanceRevisePresenter carMaintenanceRevisePresenter;
    private EditText et_totalMileage;
    private EditText et_lastMaintenanceMileage;
    private TextView et_purchaseDate;
    private TextView et_lastMaintenanceDate;
    private Calendar mCalendar;
    private DatePickerDialog.OnDateSetListener purchaseDateListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            et_purchaseDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            carMaintenanceRevisePresenter.setUserCarPurchaseDate(getDateTime(year, monthOfYear, dayOfMonth));
        }
    };
    private DatePickerDialog.OnDateSetListener lastMaintenanceDateListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            et_lastMaintenanceDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            carMaintenanceRevisePresenter.setUserCarlastMaintenanceDate(getDateTime(year, monthOfYear, dayOfMonth));
        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_save)
                carMaintenanceRevisePresenter.saveData();
            else if (view.getId() == R.id.et_purchaseDate)
                setDate(carMaintenanceRevisePresenter.boolPurchaseDate, purchaseDateListener);
            else if (view.getId() == R.id.et_lastMaintenanceDate)
                setDate(carMaintenanceRevisePresenter.boolLastMaintenanceDate, lastMaintenanceDateListener);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentView() == null) {
            createContenttView(R.layout.maintenance_revise_fragment);
            carMaintenanceRevisePresenter = new CarMaintenanceRevisePresenter(this);
            initView();
            mCalendar = Calendar.getInstance();
        }
        return getContentView();
    }

    private void initView() {
        et_totalMileage = (EditText) getContentView().findViewById(R.id.et_totalMileage);
        et_lastMaintenanceMileage = (EditText) getContentView().findViewById(R.id.et_lastMaintenanceMileage);
        et_purchaseDate = (TextView) getContentView().findViewById(R.id.et_purchaseDate);
        et_lastMaintenanceDate = (TextView) getContentView().findViewById(R.id.et_lastMaintenanceDate);
        et_purchaseDate.setOnClickListener(onClickListener);
        et_lastMaintenanceDate.setOnClickListener(onClickListener);
        getContentView().findViewById(R.id.btn_save).setOnClickListener(onClickListener);
        carMaintenanceRevisePresenter.loadLastData();
        carMaintenanceRevisePresenter.setOnListener();
    }

    /**
     * 设置弹出时间设置框
     *
     * @param boolDate
     * @param onDateSerLinstener
     */
    private void setDate(boolean boolDate, DatePickerDialog.OnDateSetListener onDateSerLinstener) {
        if (boolDate) {
            String str = et_purchaseDate.getText().toString();
            String time[] = str.split("-");
            int year = Integer.parseInt(time[0]);
            int monty = (Integer.parseInt(time[1]) - 1);
            int date = Integer.parseInt(time[2]);
            new MyDatePickerDialog(getActivity(), onDateSerLinstener, year, monty, date).show();
        } else {
            new MyDatePickerDialog(getActivity(), onDateSerLinstener, mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE)).show();
        }
    }

    private DateTime getDateTime(int year, int monthOfYear, int dayOfMonth) {
        DateTime time_buy = new DateTime();
        time_buy.year = (short) year;
        time_buy.month = (short) (monthOfYear + 1);
        time_buy.day = (short) dayOfMonth;
        return time_buy;
    }

    @Override
    public void finishView() {
        //这里要进行判断,会出现异常,待验证
        if (getActivity() != null) {
            CarMaintenanceReviseActivity activity = (CarMaintenanceReviseActivity) getActivity();
            activity.finishView();
        }
    }

    @Override
    public MaintenanceData getMaintenanceData() {
        MaintenanceData maintenanceData = new MaintenanceData();
        maintenanceData.totalMileage = et_totalMileage.getText().toString();
        maintenanceData.lastMaintenanceMileage = et_lastMaintenanceMileage.getText().toString();
        maintenanceData.purchaseDate = et_purchaseDate.getText().toString();
        maintenanceData.lastMaintenanceDate = et_lastMaintenanceDate.getText().toString();
        return maintenanceData;
    }

    @Override
    public void setMaintenanceData(MaintenanceData maintenanceData) {
        et_totalMileage.setText(maintenanceData.totalMileage);
        et_lastMaintenanceMileage.setText(maintenanceData.lastMaintenanceMileage);
        et_purchaseDate.setText(maintenanceData.purchaseDate);
        et_lastMaintenanceDate.setText(maintenanceData.lastMaintenanceDate);
    }

    @Override
    public void alert(String msg) {
        StringUtil.toastStringShort(msg);
    }
}
