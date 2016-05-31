package com.mapbar.android.obd.rearview.obd.page;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.CarStateManager;
import com.mapbar.android.obd.rearview.framework.widget.CarStateView;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.obd.CarStatusData;
import com.mapbar.obd.Manager;

import java.util.ArrayList;

/**
 * Created by liuyy on 2016/5/7.
 */
public class CarStatePage extends AppPage implements View.OnClickListener {

    private CarStateView carStateView;
    private CarStatusData data;
    @ViewInject(R.id.gv_state)
    private GridView gvState;
    @ViewInject(R.id.tv_state_record)
    private TextView tv_state_record;
    private Button btn_state_pop_close;
    private String[] stateNames;
    private int[] stateResCloseIds = {R.drawable.car_light_close, R.drawable.car_window_close, R.drawable.car_lock_close, R.drawable.car_door_close, R.drawable.car_trunk_close, R.drawable.car_sunroof_close};
    private int[] stateResOpenIds = {R.drawable.car_light_open, R.drawable.car_window_open, R.drawable.car_lock_open, R.drawable.car_door_open, R.drawable.car_trunk_open, R.drawable.car_sunroof_open};
    private int[] stateResNoneIds = {R.drawable.car_light_none, R.drawable.car_window_none, R.drawable.car_lock_none, R.drawable.car_door_none, R.drawable.car_trunk_none, R.drawable.car_sunroof_none};
    private StateAdapter adapter;
    private PopupWindow popupWindow;
    private StringBuilder sb = new StringBuilder();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_car_state);
    }

    @Override
    public void initView() {
        carStateView = new CarStateView(getContentView(), R.id.v_carstate);
        carStateView.setData(data);
        stateNames = getContext().getResources().getStringArray(R.array.state_names);
        gvState.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new StateAdapter();
        gvState.setAdapter(adapter);

    }

    @Override
    public void setListener() {
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                // 日志
                if (Log.isLoggable(LogTag.FRAMEWORK, Log.VERBOSE)) {
                    Log.v(LogTag.FRAMEWORK, "event:" + event);
                }
                switch (event) {
                    case Manager.Event.obdCarStatusgetSucc:
                        data = (CarStatusData) o;
                        carStateView.setData(data);
                        adapter.updateData();
                        adapter.notifyDataSetChanged();
                        break;
                    case Manager.Event.obdCarStatusgetFailed:
                        break;
                }
            }
        };
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);
        tv_state_record.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_state_record://查看不良状态记录
                showPopupWindow();
                break;
            case R.id.btn_state_pop_close://关闭不良状态记录
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CarStateManager.getInstance().startRefreshCarState();
    }

    @Override
    public void onPause() {
        super.onPause();
        CarStateManager.getInstance().stopRefreshCarState();
    }


    public void showPopupWindow() {
        final View popupView = View.inflate(Global.getAppContext(), R.layout.layout_state_pop, null);
        TextView tv_state_pop_content = (TextView) popupView.findViewById(R.id.tv_state_pop_content);
        btn_state_pop_close = (Button) popupView.findViewById(R.id.btn_state_pop_close);
        btn_state_pop_close.setOnClickListener(this);
        String popContent = getPopContent();
        if (!TextUtils.isEmpty(popContent)) {
            tv_state_pop_content.setText(popContent);
        }
        popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置点击PopupWindow以外的区域取消PopupWindow的显示
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
    }

    public String getPopContent() {
        sb.setLength(0);
        ArrayList<String> alarmDatas = CarStateManager.getInstance().alarmDatas;
        if (alarmDatas.size() > 3) {
            sb.append("故障码:");
            for (int i = 3; i < alarmDatas.size(); i++) {
                if (i == alarmDatas.size() - 1) {
                    sb.append(alarmDatas.get(i) + "；\n");
                } else {
                    sb.append(alarmDatas.get(i) + "、");
                }
            }
        }
        if (!"水温".equals(alarmDatas.get(0))) {
            sb.append("当前水温:" + alarmDatas.get(0) + "℃；\n");
        }
        if (!"电压".equals(alarmDatas.get(1))) {
            sb.append("当前电压:" + alarmDatas.get(1) + "v；\n");
        }
        if (!"疲劳".equals(alarmDatas.get(2))) {
            sb.append("您已驾驶:" + alarmDatas.get(2) + "小时。");
        }
        return sb.toString();
    }

    class StateAdapter extends BaseAdapter {
        private int[] dataStates;

        public StateAdapter() {
            if (data != null) {
                dataStates = new int[]{data.lights, data.windows, data.lock, data.doors, data.trunk, data.sunroof};
            }
        }

        @Override
        public int getCount() {
            return stateNames.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getContext(), R.layout.item_grid_state, null);
                holder.iv = (ImageView) convertView.findViewById(R.id.iv_item_state);
                holder.tv = (TextView) convertView.findViewById(R.id.tv_item_state);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (data == null) {
                holder.iv.setImageDrawable(getContext().getResources().getDrawable(stateResNoneIds[position]));
            } else {
                switch (dataStates[position]) {
                    case -1:
                    case 0:
                        holder.iv.setImageDrawable(getContext().getResources().getDrawable(stateResNoneIds[position]));
                        break;
                    case 1:
                        holder.iv.setImageDrawable(getContext().getResources().getDrawable(stateResOpenIds[position]));
                        break;
                    case 2:
                        if (position == 3 || position == 4) {
                            holder.iv.setImageDrawable(getContext().getResources().getDrawable(stateResCloseIds[position]));
                        } else {
                            holder.iv.setImageDrawable(getContext().getResources().getDrawable(stateResOpenIds[position]));
                        }
                        break;
                    case 3:
                        holder.iv.setImageDrawable(getContext().getResources().getDrawable(stateResCloseIds[position]));
                        break;
                }
            }
            holder.tv.setText(stateNames[position]);
            return convertView;
        }

        public void updateData() {
            if (data != null) {
                dataStates = new int[]{data.lights, data.windows, data.lock, data.doors, data.trunk, data.sunroof};
            }
        }

        class ViewHolder {
            ImageView iv;
            TextView tv;
        }
    }
}
