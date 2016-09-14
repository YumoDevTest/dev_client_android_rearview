package com.mapbar.android.obd.rearview.modules.cardata;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mapbar.android.obd.rearview.framework.common.TimeUtils;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.modules.cardata.contract.ICarDataView;
import com.mapbar.android.obd.rearview.modules.permission.PermissionAlertViewAdapter;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAdatper;
import com.mapbar.android.obd.rearview.modules.external.ExternalManager;
import com.mapbar.android.obd.rearview.modules.setting.SettingActivity;
import com.mapbar.android.obd.rearview.modules.tirepressure.TirePressurePresenter;
import com.mapbar.android.obd.rearview.modules.tirepressure.contract.ITirePressureView;
import com.mapbar.android.obd.rearview.modules.tirepressure.model.TirePressure4ViewModel;
import com.mapbar.android.obd.rearview.modules.common.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.lib.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.lib.umeng.UmengConfigs;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.android.obd.rearview.obd.util.SafeHandler;
import com.mapbar.android.obd.rearview.views.TirePressureViewDigital;
import com.mapbar.android.obd.rearview.views.TirePressureViewSimple;
import com.mapbar.android.obd.rearview.views.TitleBarView;
import com.mapbar.obd.Manager;
import com.mapbar.obd.RealTimeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * 车辆 数据 页
 * Created by THINKPAD on 2016/5/6.
 */
public class CarDataPage extends AppPage implements View.OnClickListener, ICarDataView, ITirePressureView {
    private static final int MSG_GET_A_CAR_DATA = 2;//当获得一个车辆数据
    private final int[] icons = {R.drawable.car_data_gas_consum, R.drawable.car_data_trip_time, R.drawable.car_data_trip_length, R.drawable.car_data_drive_cost,
            R.drawable.car_data_speed, R.drawable.car_data_rpm, R.drawable.car_data_voltage, R.drawable.car_data_temperature, R.drawable.car_data_average_gas_consum};

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
    private SharedPreferences sharedPreferences;
    private ArrayList<HashMap<String, Object>> datas = new ArrayList<>();
    private int number = -1;//显示数据空间的编号
    private PopupWindow popupWindow;
    private String[] dataNames;// = getActivity().getResources().getStringArray(R.array.data_names);
    private String[] units;// = getActivity().getResources().getStringArray(R.array.units);
    private boolean isFirst = true;
    private int spv0, spv1, spv2, spv3;
    private TitleBarView titlebarview1;
    private CarDataPresenter cardataPresenter;
    private TirePressurePresenter tirePressurePresenter;


    private TirePressureViewSimple[] tirePressureSimpleViewArray;//单一胎压指示视图
    private TirePressureViewDigital[] tirePressureForeViewArray;//4胎压指示视图

    private IPermissionAlertViewAdatper permissionAlertAbleAdapter;
    private MyHandler myHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_car_data);

        dataNames = getActivity().getResources().getStringArray(R.array.data_names);
        units = getActivity().getResources().getStringArray(R.array.units);
        myHandler = new MyHandler(this);
    }

    public void initView() {
        titlebarview1 = (TitleBarView) getContentView().findViewById(R.id.titlebarview1);
        titlebarview1.setTitle(R.string.page_title_car_data);
        titlebarview1.setButtonRightVisibility(true);
        titlebarview1.setButtonRightImage(R.drawable.ic_settng_selector);
        titlebarview1.setButtonRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        });
        //单一胎压
        TirePressureViewSimple tire_simple_left_top = (TirePressureViewSimple) getContentView().findViewById(R.id.tire_simple_left_top);
        TirePressureViewSimple tire_simple_right_top = (TirePressureViewSimple) getContentView().findViewById(R.id.tire_simple_right_top);
        TirePressureViewSimple tire_simple_left_bottom = (TirePressureViewSimple) getContentView().findViewById(R.id.tire_simple_left_bottom);
        TirePressureViewSimple tire_simple_right_bottom = (TirePressureViewSimple) getContentView().findViewById(R.id.tire_simple_right_bottom);
        tirePressureSimpleViewArray = new TirePressureViewSimple[]{tire_simple_left_top, tire_simple_right_top, tire_simple_left_bottom, tire_simple_right_bottom};
        //4胎压
        TirePressureViewDigital tire_pressure_left_top = (TirePressureViewDigital) getContentView().findViewById(R.id.tire_pressure_left_top);
        TirePressureViewDigital tire_pressure_left_bottom = (TirePressureViewDigital) getContentView().findViewById(R.id.tire_pressure_left_bottom);
        TirePressureViewDigital tire_pressure_rignt_top = (TirePressureViewDigital) getContentView().findViewById(R.id.tire_pressure_rignt_top);
        TirePressureViewDigital tire_pressure_rignt_bottom = (TirePressureViewDigital) getContentView().findViewById(R.id.tire_pressure_rignt_bottom);
        tirePressureForeViewArray = new TirePressureViewDigital[]{tire_pressure_left_top, tire_pressure_rignt_top, tire_pressure_left_bottom, tire_pressure_rignt_bottom};

        sharedPreferences = getActivity().getSharedPreferences("car_data", Context.MODE_PRIVATE);
        isFirst = sharedPreferences.getBoolean("isFirst", true);
        if (isFirst) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (int i = 0; i < icons.length; i++) {
                editor.putInt(i + "", i);
            }
            editor.putBoolean("isFirst", false);
            editor.commit();
        }
        cardataPresenter = new CarDataPresenter(this);
        tirePressurePresenter = new TirePressurePresenter(this);
        getPopData();
        upDataView();

        setListener();
    }

    public void setListener() {
        ll_car_data_1.setOnClickListener(this);
        ll_car_data_2.setOnClickListener(this);
        ll_car_data_3.setOnClickListener(this);
        ll_car_data_4.setOnClickListener(this);
    }

    private OBDSDKListenerManager.SDKListener sdkListener = new OBDSDKListenerManager.SDKListener() {
        @Override
        public void onEvent(int event, Object o) {
            // 日志
            if (Log.isLoggable(LogTag.FRAMEWORK, Log.VERBOSE)) {
                Log.v(LogTag.FRAMEWORK, "event:" + event);
            }
            super.onEvent(event, o);
            if (event == Manager.Event.dataUpdate) {
                LogUtil.d("TAG", "## when realTimeData :" + event);
                RealTimeData realTimeData = (RealTimeData) o;
                if (realTimeData != null) {
                    myHandler.obtainMessage(MSG_GET_A_CAR_DATA, realTimeData).sendToTarget();
                    //发送广播，传出实时数据
                    ExternalManager.postRealTimeData(getActivity(), realTimeData);
                }

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (cardataPresenter != null) cardataPresenter.checkPermission();
        if (tirePressurePresenter != null) tirePressurePresenter.checkPermission();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            //fragment可见时执行加载数据或者进度条等
            OBDSDKListenerManager.getInstance().addSdkListener(sdkListener);
        } else {
            //不可见时不执行操作
            OBDSDKListenerManager.getInstance().removeSdkListener(sdkListener);
        }
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        if (permissionAlertAbleAdapter != null)
            permissionAlertAbleAdapter.clear();
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        if (tirePressurePresenter != null)
            tirePressurePresenter.clear();
        if (cardataPresenter != null)
            cardataPresenter.clear();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_car_data_1:
                number = 0;
                showPopupWindow(getContext());
                break;
            case R.id.ll_car_data_2:
                number = 1;
                showPopupWindow(getContext());
                break;
            case R.id.ll_car_data_3:
                number = 2;
                showPopupWindow(getContext());
                break;
            case R.id.ll_car_data_4:
                number = 3;
                showPopupWindow(getContext());
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
     *
     * @param realTimeData
     */
    public void bindCardataToView(RealTimeData realTimeData) {// 日志
        tv_carData_value_1.setText(transform(spv0, realTimeData));
        tv_carData_value_2.setText(transform(spv1, realTimeData));
        tv_carData_value_3.setText(transform(spv2, realTimeData));
        tv_carData_value_4.setText(transform(spv3, realTimeData));
        //当车速低于10时,更改瞬时油耗单位,每次更新数据的时候都检测更新这个数据
        if (realTimeData != null && realTimeData.speed <= 10) {
            if (spv0 == 0) tv_carData_unit_1.setText("L/H");
            else if (spv1 == 0) tv_carData_unit_2.setText("L/H");
            else if (spv2 == 0) tv_carData_unit_3.setText("L/H");
            else if (spv3 == 0) tv_carData_unit_4.setText("L/H");
        } else if (realTimeData != null && realTimeData.speed > 10) {
            if (spv0 == 0) tv_carData_unit_1.setText("L/100KM");
            else if (spv1 == 0) tv_carData_unit_2.setText("L/100KM");
            else if (spv2 == 0) tv_carData_unit_3.setText("L/100KM");
            else if (spv3 == 0) tv_carData_unit_4.setText("L/100KM");
        }
    }

    /**
     * 通过数组索引获取时实数据
     *
     * @param index        {@link #dataNames} 数组索引
     * @param realTimeData
     * @return 时实数据
     */
    public String transform(int index, RealTimeData realTimeData) {
        switch (index) {
            case 0:
                //如果车速小于10获取小时单位的瞬时油耗,反之获取100KM油耗;
                return realTimeData.speed < 10 ? DecFormatUtil.format2dot1(realTimeData.gasConsumInLPerHour) : DecFormatUtil.format2dot1(realTimeData.gasConsum);
            case 1:
                return TimeUtils.parseTime(realTimeData.tripTime);
            case 2:
                return DecFormatUtil.format2dot1(realTimeData.tripLength / 1000);
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


    public void showPopupWindow(Context context) {
        final View popupView = View.inflate(context, R.layout.layout_car_data_pop, null);
        popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置点击PopupWindow以外的区域取消PopupWindow的显示
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        GridView gridView = (GridView) popupView.findViewById(R.id.gv_data_pop);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));//使gridView点击每个item没有颜色变化；进而可以自定义点击变化颜色；
        ImageView tv_pop_close = (ImageView) popupView.findViewById(R.id.iv_pop_close);
        tv_pop_close.setOnClickListener(this);
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), datas, R.layout.item_grid_car_data, new String[]{"name", "icon"}, new int[]{R.id.tv_item_state, R.id.iv_item_state});
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
                    uMeng(getActivity(), number, value1);
                }

                //更新数据
                getPopData();
                upDataView();
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
    }

    /**
     * 友盟统计
     *
     * @param number
     * @param value1
     */
    private void uMeng(Context context, int number, int value1) {
        switch (value1) {
            case 0:
                MobclickAgentEx.onEvent(context, UmengConfigs.GASCONSUM);
                break;
            case 1:
                MobclickAgentEx.onEvent(context, UmengConfigs.TRIPTIME);
                break;
            case 2:
                MobclickAgentEx.onEvent(context, UmengConfigs.TRIPLENGTH);
                break;
            case 3:
                MobclickAgentEx.onEvent(context, UmengConfigs.DRIVECOST);
                break;
            case 4:
                MobclickAgentEx.onEvent(context, UmengConfigs.SPEED);
                break;
            case 5:
                MobclickAgentEx.onEvent(context, UmengConfigs.RPM);
                break;
            case 6:
                MobclickAgentEx.onEvent(context, UmengConfigs.VOLTAGE);
                break;
            case 7:
                MobclickAgentEx.onEvent(context, UmengConfigs.ENGINECOOLANTTEMPERATURE);
                break;
            case 8:
                MobclickAgentEx.onEvent(context, UmengConfigs.AVERAGEGASCONSUM);
                break;
        }
    }

    /**
     * 显示单一胎压样式
     */
    @Override
    public void showTirePresstureSingle(TirePressure4ViewModel[] tirePressureArray) {
        if (tirePressureArray == null || tirePressureArray.length != 4) {
            alert("胎压数据异常");
            return;
        }
        boolean isHasNull = false;
        for (int i = 0; i < tirePressureArray.length; i++) {
            if (tirePressureArray[i] == null) {
                isHasNull = true;
                break;
            }
        }
        if (isHasNull) {
            alert("读取胎压数据时发生错误");
            return;
        }
        for (int i = 0; i < tirePressureForeViewArray.length; i++) {
            tirePressureSimpleViewArray[i].setVisibility(View.VISIBLE);
            tirePressureSimpleViewArray[i].setWarning(tirePressureArray[i].isWarning);
        }
    }

    /**
     * 隐藏单一胎压样式。
     */
    @Override
    public void hideTirePresstureSingleView() {
        for (int i = 0; i < tirePressureSimpleViewArray.length; i++) {
            tirePressureSimpleViewArray[i].setVisibility(View.GONE);
        }
    }


    /**
     * 显示四胎压样式。数组里显示4个胎压，分别对应，左上，右上，左下，右下
     *
     * @param tirePressureArray
     */
    @Override
    public void showTirePresstureFour(TirePressure4ViewModel[] tirePressureArray) {
        if (tirePressureArray == null || tirePressureArray.length != 4) {
            alert("胎压数据异常");
            return;
        }
        boolean isHasNull = false;
        for (int i = 0; i < tirePressureArray.length; i++) {
            if (tirePressureArray[i] == null) {
                isHasNull = true;
                break;
            }
        }
        if (isHasNull) {
            alert("读取胎压数据时发生错误");
            return;
        }
        for (int i = 0; i < tirePressureForeViewArray.length; i++) {
            tirePressureForeViewArray[i].setVisibility(View.VISIBLE);
            tirePressureForeViewArray[i].setTirePressure(String.format(Locale.US, "%.1f", tirePressureArray[i].tirePressure));
            tirePressureForeViewArray[i].setTireTemperature(String.format(Locale.US, "%d", (int) tirePressureArray[i].itreTemprature));
            tirePressureForeViewArray[i].setWarning(tirePressureArray[i].isWarning);
        }
    }

    /**
     * 隐藏 四胎压样式。
     */
    @Override
    public void hideTirePresstureFoureView() {
        for (int i = 0; i < tirePressureSimpleViewArray.length; i++) {
            tirePressureForeViewArray[i].setVisibility(View.GONE);
        }
    }

    /**
     * 根节点是个framentView,可以放入 授权提醒的视图作为浮层
     *
     * @param numberOfDay
     */
    public void showPermissionAlertView_FreeTrial(boolean isExpired, int numberOfDay) {
        if (permissionAlertAbleAdapter == null)
            permissionAlertAbleAdapter = new PermissionAlertViewAdapter(this);
        permissionAlertAbleAdapter.showPermissionAlertView_FreeTrial(isExpired, numberOfDay);
    }

    public void hidePermissionAlertView_FreeTrial() {
        if (permissionAlertAbleAdapter != null)
            permissionAlertAbleAdapter.hidePermissionAlertView_FreeTrial();
    }


    private static class MyHandler extends SafeHandler<CarDataPage> {

        public MyHandler(CarDataPage object) {
            super(object);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_GET_A_CAR_DATA) {
                RealTimeData realTimeData = (RealTimeData) msg.obj;
                if (realTimeData != null) {
                    getInnerObject().bindCardataToView(realTimeData);
                }
            }
            super.handleMessage(msg);
        }
    }
}
