package com.mapbar.android.obd.rearview.obd.page;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.CarStateManager;
import com.mapbar.android.obd.rearview.framework.widget.CarStateView;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.obd.CarStatusData;
import com.mapbar.obd.Manager;

/**
 * Created by liuyy on 2016/5/7.
 */
public class CarStatePage extends AppPage {

    private CarStateView carStateView;
    private CarStatusData data;
    @ViewInject(R.id.gv_state)
    private GridView gvState;
    private String[] stateNames;
    private int[] stateResCloseIds = {R.drawable.car_light_close, R.drawable.car_window_close, R.drawable.car_lock_close, R.drawable.car_door_close, R.drawable.car_trunk_close, R.drawable.car_sunroof_close};
    private int[] stateResOpenIds = {R.drawable.car_light_open, R.drawable.car_window_open, R.drawable.car_lock_open, R.drawable.car_door_open, R.drawable.car_trunk_open, R.drawable.car_sunroof_open};
    private int[] stateResNoneIds = {R.drawable.car_light_none, R.drawable.car_window_none, R.drawable.car_lock_none, R.drawable.car_door_none, R.drawable.car_trunk_none, R.drawable.car_sunroof_none};
    private StateAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        carStateView = new CarStateView(getContentView(), R.id.v_carstate);
        data = CarStateManager.getInstance().getCarStatusData();
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
                    Log.v(LogTag.FRAMEWORK, "CarStatePage -->> event:" + event);
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
                // 日志
                if (Log.isLoggable(LogTag.FRAMEWORK, Log.VERBOSE)) {
                    for (int i = 0; i < dataStates.length; i++) {
                        Log.v(LogTag.FRAMEWORK, "CarStatePage carState==" + dataStates[i]);
                    }
                }
            }
        }

        class ViewHolder {
            ImageView iv;
            TextView tv;
        }
    }
}
