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

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.manager.CarDataManager;
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
    @ViewInject(R.id.ll_car_data_pop)
    private LinearLayout ll_car_data_pop;

    private SharedPreferences sharedPreferences = MainActivity.getInstance().getSharedPreferences("data", Context.MODE_PRIVATE);

    private SimpleAdapter simpleAdapter;

    private String[] dataNames = {"瞬时油耗", "本次行程时间", "本次已行驶里程", "本次行程花费", "车速", "转速", "电压", "水温", "平均油耗"};
    private String[] units = {"L/100KM", "H", "KM", "元", "KM/H", "R/MIN", "V", "℃", "L/100KM"};
    private int[] icons = {R.drawable.car_data_gas_consum, R.drawable.car_data_trip_time, R.drawable.car_data_trip_length, R.drawable.car_data_drive_cost,
            R.drawable.car_data_speed, R.drawable.car_data_rpm, R.drawable.car_data_voltage, R.drawable.car_data_temperature, R.drawable.car_data_average_gas_consum};

    private ArrayList<HashMap<String, Object>> datas = new ArrayList<>();

    @Override
    public void initView() {

        //初始化pop数据
        for (int i = 4; i < 9; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", dataNames[sharedPreferences.getInt(i + "", i)]);
            map.put("icon", icons[sharedPreferences.getInt(i + "", i)]);
            datas.add(map);
        }
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
                super.onEvent(event, o);
                switch (event) {
                    case Manager.Event.dataUpdate:
                        RealTimeData data = CarDataManager.getInstance().getRealTimeData();
                        if (data != null) {

                        }
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_car_data_1:
                showPopupWindow();
                break;
        }
    }

    public void showPopupWindow() {
        View popupView = View.inflate(Global.getAppContext(), R.layout.layout_car_data_pop, null);
        GridView gridView = (GridView) popupView.findViewById(R.id.gv_data_pop);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        ImageView tv_pop_close = (ImageView) popupView.findViewById(R.id.iv_pop_close);


        simpleAdapter = new SimpleAdapter(MainActivity.getInstance(), datas, R.layout.item_grid_state, new String[]{"name", "icon"}, new int[]{R.id.tv_item_state, R.id.iv_item_state});
        gridView.setAdapter(simpleAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.findViewById(R.id.iv_item_state).setEnabled(false);
                simpleAdapter.notifyDataSetChanged();
            }
        });
        PopupWindow popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //第一种方式：设置点击PopupWindow以外的区域取消PopupWindow的显示
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //第二种方式 调用dismiss()方法让popupWindow自动消失
        //popupWindow.dismiss();
        popupWindow.showAtLocation(ll_car_data_pop, Gravity.BOTTOM, 0, 0);
    }

}
