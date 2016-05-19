package com.mapbar.android.obd.rearview.obd.page;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.common.DecFormatUtil;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.common.TimeUtils;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.obd.Manager;
import com.mapbar.obd.RealTimeData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by THINKPAD on 2016/5/6.
 */
public class CarDataPage extends AppPage implements View.OnClickListener {
    @ViewInject(R.id.ll_car_data_1)
    private LinearLayout ll_car_data_1;
    @ViewInject(R.id.ll_car_data_2)
    private LinearLayout ll_car_data_2;
    @ViewInject(R.id.ll_car_data_3)
    private LinearLayout ll_car_data_3;
    @ViewInject(R.id.ll_car_data_4)
    private LinearLayout ll_car_data_4;
    /**
     * ------------------------------------------------------
     */
    @ViewInject(R.id.tv_carData_name_1)
    private TextView tv_carData_name_1;
    @ViewInject(R.id.tv_carData_name_2)
    private TextView tv_carData_name_2;
    @ViewInject(R.id.tv_carData_name_3)
    private TextView tv_carData_name_3;
    @ViewInject(R.id.tv_carData_name_4)
    private TextView tv_carData_name_4;

    @ViewInject(R.id.tv_carData_unit_1)
    private TextView tv_carData_unit_1;
    @ViewInject(R.id.tv_carData_unit_2)
    private TextView tv_carData_unit_2;
    @ViewInject(R.id.tv_carData_unit_3)
    private TextView tv_carData_unit_3;
    @ViewInject(R.id.tv_carData_unit_4)
    private TextView tv_carData_unit_4;

    @ViewInject(R.id.tv_carData_value_1)
    private TextView tv_carData_value_1;
    @ViewInject(R.id.tv_carData_value_2)
    private TextView tv_carData_value_2;
    @ViewInject(R.id.tv_carData_value_3)
    private TextView tv_carData_value_3;
    @ViewInject(R.id.tv_carData_value_4)
    private TextView tv_carData_value_4;

    private SharedPreferences sharedPreferences = MainActivity.getInstance().getSharedPreferences("car_data", Context.MODE_PRIVATE);
    private ArrayList<HashMap<String, Object>> datas = new ArrayList<>();
    private int number = -1;//显示数据空间的编号
    private PopupWindow popupWindow;
    private RealTimeData realTimeData;

    private String[] dataNames = getContext().getResources().getStringArray(R.array.data_names);
    private String[] units = getContext().getResources().getStringArray(R.array.units);
    private int[] icons = {R.drawable.car_data_gas_consum, R.drawable.car_data_trip_time, R.drawable.car_data_trip_length, R.drawable.car_data_drive_cost,
            R.drawable.car_data_speed, R.drawable.car_data_rpm, R.drawable.car_data_voltage, R.drawable.car_data_temperature, R.drawable.car_data_average_gas_consum};
    private boolean isFirst = true;
    private int spv0, spv1, spv2, spv3;

    @Override
    public void initView() {
        isFirst = sharedPreferences.getBoolean("isFirst", true);
        if (isFirst) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (int i = 0; i < icons.length; i++) {
                editor.putInt(i + "", i);
            }
            editor.putBoolean("isFirst", false);
            editor.commit();
        }
        getPopData();
        upDataView();
    }

    @Override
    public void setListener() {
        ll_car_data_1.setOnClickListener(this);
        ll_car_data_2.setOnClickListener(this);
        ll_car_data_3.setOnClickListener(this);
        ll_car_data_4.setOnClickListener(this);

        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                // 日志
                if (Log.isLoggable(LogTag.FRAMEWORK, Log.VERBOSE)) {
                    Log.v(LogTag.FRAMEWORK, "event:" + event);
                }
                super.onEvent(event, o);
                switch (event) {
                    case Manager.Event.dataUpdate:
                        realTimeData = (RealTimeData) o;
                        if (realTimeData != null) {
                            upData();
                        }
                        break;
                }
            }
        }

        ;
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_car_data_1:
                number = 0;
                showPopupWindow();
                break;
            case R.id.ll_car_data_2:
                number = 1;
                showPopupWindow();
                break;
            case R.id.ll_car_data_3:
                number = 2;
                showPopupWindow();
                break;
            case R.id.ll_car_data_4:
                number = 3;
                showPopupWindow();
                break;
            case R.id.iv_pop_close://关闭popupWindow
                popupWindow.dismiss();
                break;
        }
    }

    /**
     * 更新View数据
     */

    public void upDataView() {
        tv_carData_name_1.setText(dataNames[spv0]);
        tv_carData_name_2.setText(dataNames[spv1]);
        tv_carData_name_3.setText(dataNames[spv2]);
        tv_carData_name_4.setText(dataNames[spv3]);
        tv_carData_unit_1.setText(units[spv0]);
        tv_carData_unit_2.setText(units[spv1]);
        tv_carData_unit_3.setText(units[spv2]);
        tv_carData_unit_4.setText(units[spv3]);
    }

    /**
     * 设置时实数据
     */
    public void upData() {// 日志
        if (Log.isLoggable(LogTag.FRAMEWORK, Log.VERBOSE)) {
            Log.v(LogTag.FRAMEWORK, "upData+++++++");
            Log.v(LogTag.FRAMEWORK, "ThreadId:" + Thread.currentThread().getId());
            Log.v(LogTag.FRAMEWORK, "speed:" + realTimeData.rpm);
        }
        tv_carData_value_1.post(new Runnable() {
            @Override
            public void run() {
                tv_carData_value_1.setText(transform(spv0));
                tv_carData_value_2.setText(transform(spv1));
                tv_carData_value_3.setText(transform(spv2));
                tv_carData_value_4.setText(transform(spv3));
            }
        });

    }

    /**
     * 通过数组索引获取时实数据
     *
     * @param index {@link #dataNames} 数组索引
     * @return 时实数据
     */
    public String transform(int index) {
        switch (index) {
            case 0:
                return DecFormatUtil.format2dot1(realTimeData.gasConsum);
            case 1:
                return TimeUtils.parseTime(realTimeData.tripTime);
            case 2:
                return DecFormatUtil.format2dot1(realTimeData.tripLength);
            case 3:
                return DecFormatUtil.format2dot1(realTimeData.driveCost);
            case 4:
                return "" + realTimeData.speed;
            case 5:
                return "" + realTimeData.rpm;
            case 6:
                return DecFormatUtil.format2dot1(realTimeData.voltage);
            case 7:
                return "" + realTimeData.engineCoolantTemperature;
            case 8:
                return DecFormatUtil.format2dot1(realTimeData.averageGasConsum);
        }
        return null;
    }


    /**
     * 初始化pop中gridView数据
     */
    public void getPopData() {
        spv0 = sharedPreferences.getInt("0", 0);
        spv1 = sharedPreferences.getInt("1", 1);
        spv2 = sharedPreferences.getInt("2", 2);
        spv3 = sharedPreferences.getInt("3", 3);
        for (int i = 4; i < 9; i++) {
            if (datas.size() <= 5) {
                HashMap<String, Object> map = new HashMap<>();
                datas.add(map);
            }
            final HashMap<String, Object> map = datas.get(i - 4);
            map.put("name", dataNames[sharedPreferences.getInt(i + "", i)]);
            map.put("icon", icons[sharedPreferences.getInt(i + "", i)]);
        }
    }


    public void showPopupWindow() {
        final View popupView = View.inflate(Global.getAppContext(), R.layout.layout_car_data_pop, null);
        popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置点击PopupWindow以外的区域取消PopupWindow的显示
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        GridView gridView = (GridView) popupView.findViewById(R.id.gv_data_pop);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));//使gridView点击每个item没有颜色变化；进而可以自定义点击变化颜色；
        ImageView tv_pop_close = (ImageView) popupView.findViewById(R.id.iv_pop_close);
        tv_pop_close.setOnClickListener(this);
        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.getInstance(), datas, R.layout.item_grid_car_data, new String[]{"name", "icon"}, new int[]{R.id.tv_item_state, R.id.iv_item_state});
        gridView.setAdapter(simpleAdapter);
        //gridView监听点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int value1 = sharedPreferences.getInt((4 + position) + "", -1);
                int value2 = sharedPreferences.getInt(number + "", -1);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!(value1 == -1 || value2 == -1)) {
                    editor.putInt(number + "", value1);
                    editor.putInt((4 + position) + "", value2).commit();
                }

                //更新数据
                getPopData();
                upDataView();
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
    }

}
