package com.mapbar.android.obd.rearview.modules.carstate;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.CarStateManager;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.android.obd.rearview.lib.base.AppPage2;
import com.mapbar.android.obd.rearview.lib.config.Configs;
import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.android.obd.rearview.lib.services.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.model.QRInfo;
import com.mapbar.android.obd.rearview.modules.carstate.contract.ICarStateView;
import com.mapbar.android.obd.rearview.modules.carstate.contract.IVinChangeView;
import com.mapbar.android.obd.rearview.modules.external.ExternalManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissionAlertViewAdapter;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAdatper;
import com.mapbar.android.obd.rearview.views.CarStateView;
import com.mapbar.android.obd.rearview.views.TitleBarView;
import com.mapbar.android.obd.rearview.views.VinBarcodeView;
import com.mapbar.obd.CarStatusData;
import com.mapbar.obd.Manager;
import com.mapbar.obd.foundation.utils.SafeHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 车辆 状态
 * Created by liuyy on 2016/5/7.
 */
public class CarStatePage extends AppPage2 implements View.OnClickListener, ICarStateView, IVinChangeView {

    private CarStateView carStateView;
    private CarStatusData data;
    private GridView gvState;
    private TextView tv_state_record;
    private ImageView iv_state_safe;
    private TextView tv_ota_alert_text;
    //车辆不良状态的提示语，点击能有详细故障码，被 体检功能权限控制可见性
    private ViewGroup viewgrounp_stage_record;

    private Handler myHandler;
    private List<String> state_list;
    private String[] stateNames;
    private int[] stateResCloseIds = {R.drawable.car_light_close, R.drawable.car_window_close, R.drawable.car_lock_close, R.drawable.car_door_close, R.drawable.car_trunk_close, R.drawable.car_sunroof_close};
    private int[] stateResOpenIds = {R.drawable.car_light_open, R.drawable.car_window_open, R.drawable.car_lock_open, R.drawable.car_door_open, R.drawable.car_trunk_open, R.drawable.car_sunroof_open};
    private int[] stateResNoneIds = {R.drawable.car_light_none, R.drawable.car_window_none, R.drawable.car_lock_none, R.drawable.car_door_none, R.drawable.car_trunk_none, R.drawable.car_sunroof_none};
    private StateAdapter adapter;
    private PopupWindow popupWindow;
    private PopupWindow firmwarePopu;
    private StringBuilder sb = new StringBuilder();
    private TitleBarView titlebarview1;
    //    private FirmwareDialogHandler firmwareDialogHandler;
    private CarStatePresenter persenter;
    private IPermissionAlertViewAdatper permissionAlertAbleAdapter;

    private VinBarcodeView vinBarcodeView;
    private boolean hasCheckedVersion = false;

    private int screenWidth;
    private int screenHeight;
    private OBDSDKListenerManager.SDKListener sdkListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentView() == null) {
            createContenttView(R.layout.page_car_state);
            initView();
        }
        return getContentView();
    }

    public void initView() {
        gvState = (GridView) getContentView().findViewById(R.id.gv_state);
        tv_state_record = (TextView) getContentView().findViewById(R.id.tv_state_record);
        iv_state_safe = (ImageView) getContentView().findViewById(R.id.iv_state_safe);
        tv_ota_alert_text = (TextView) getContentView().findViewById(R.id.tv_ota_alert_text);
        viewgrounp_stage_record = (ViewGroup) getContentView().findViewById(R.id.viewgrounp_stage_record);

        titlebarview1 = (TitleBarView) getContentView().findViewById(R.id.titlebarview1);
        titlebarview1.setTitle(R.string.page_title_car_state);

        myHandler = new MyHandler(this);
        viewgrounp_stage_record.setOnClickListener(this);
        tv_ota_alert_text.setOnClickListener(this);

        WindowManager windowManager = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeight = windowManager.getDefaultDisplay().getHeight();
        carStateView = new CarStateView(getActivity(), getContentView(), R.id.v_carstate);
        carStateView.setData(data);
        stateNames = getActivity().getResources().getStringArray(R.array.state_names);
        gvState.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new StateAdapter();
        gvState.setAdapter(adapter);

//        firmwareDialogHandler = new FirmwareDialogHandler();
        persenter = new CarStatePresenter(this);
    }

    public void attachListener() {
        if (sdkListener == null) {
            sdkListener = new OBDSDKListenerManager.SDKListener() {
                @Override
                public void onEvent(int event, Object o) {

                    // 日志
                    if (Log.isLoggable(LogTag.FRAMEWORK, Log.VERBOSE)) {
//                    Log.v(LogTag.FRAMEWORK, "whw -->> event:" + event);
                    }
                    switch (event) {
                        case Manager.Event.obdCarStatusgetSucc:
                            data = (CarStatusData) o;
                            carStateView.setData(data);
                            adapter.updateData();
                            adapter.notifyDataSetChanged();
                            if (getActivity() != null && !getActivity().isFinishing()) {
                                //发送外部消息广播
                                ExternalManager.postCarStatus(getActivity(), data);
                            }
                            break;
                        case Manager.Event.obdCarStatusgetFailed:
                            break;

                        case OBDManager.EVENT_OBD_OTA_NEED_VIN:
                            break;

                        case OBDManager.EVENT_OBD_OTA_SCANVIN_SUCC://TODO 扫描成功
                            if (vinBarcodeView != null) {
                                QRInfo qrInfo_scan_succ = (QRInfo) o;
                                vinBarcodeView.setText(qrInfo_scan_succ.getContent());
                            }
                            break;

                        case OBDManager.EVENT_OBD_USER_BINDVIN_SUCC:
                            hideVinInputDialog();
                            break;

                        case OBDManager.EVENT_OBD_USER_BINDVIN_FAILED:
//                        showVinInputDialog();
                            break;

                        case Manager.Event.dataUpdate:
                            if (!hasCheckedVersion) {
                                //检查是否有新的固件版本
                                persenter.beginCheckFirmwareVersion();
                                hasCheckedVersion = true;
                            }
                            break;

                    }
                }
            };
        }
        OBDSDKListenerManager.getInstance().addSdkListener(sdkListener);
    }

    private void detaichListener() {
        if (sdkListener != null)
            OBDSDKListenerManager.getInstance().removeSdkListener(sdkListener);
    }


    /**
     * 提示有可升级的固件
     */
    public void showOtaAlert_can_upgrade() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_ota_alert_text.setText(R.string.firmware_can_upgrade);
            }
        });
    }

    /**
     * 提示支持车辆控制
     */
    public void showOtaAlert_NoSupportControl() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_ota_alert_text.setText(R.string.firmware_no_support_control);
            }
        });
    }

    /**
     * 提示不支持车辆控制
     */
    public void showOtaAlert_SupportControl() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_ota_alert_text.setText(R.string.firmware_support_control);
            }
        });
    }

    /**
     * 显示vin 二维码 的对话框
     */
    public void showVinInputDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (vinBarcodeView != null && vinBarcodeView.getParent() != null)
                    return;
                if (getContentView() instanceof FrameLayout) {
                    FrameLayout frameLayout = (FrameLayout) getContentView();
                    if (vinBarcodeView == null) {
                        vinBarcodeView = new VinBarcodeView(getActivity());
                        frameLayout.addView(vinBarcodeView);
                    }
                    vinBarcodeView.showQrBarcode();
                }
            }
        });
    }

    /**
     * 显示 VIN修改 扫码成功
     */
    public void showVinScanOK() {
        if (getContentView() instanceof FrameLayout) {
            FrameLayout frameLayout = (FrameLayout) getContentView();
            if (vinBarcodeView == null) {
                vinBarcodeView = new VinBarcodeView(getActivity());
                frameLayout.addView(vinBarcodeView);
            }
            vinBarcodeView.setText(R.string.vin_alert_dialog_scan_success);
        }
    }

    /**
     * 隐藏vin 二维码 的对话框
     */
    public void hideVinInputDialog() {
        if (vinBarcodeView != null) {
            if (vinBarcodeView.getParent() != null && vinBarcodeView.getParent() == getContentView()
                    && vinBarcodeView.getParent() instanceof FrameLayout) {
                FrameLayout frameLayout = (FrameLayout) getContentView();
                frameLayout.removeView(vinBarcodeView);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewgrounp_stage_record://查看不良状态记录
                if (state_list.size() > 0) {
                    showPopupWindow();
                }
                break;
            case R.id.tv_ota_alert_text://提示有固件升级
                if (tv_ota_alert_text.getText().toString().equals(getString(R.string.firmware_can_upgrade))) {
                    persenter.beginCheckFirmwareVersion();

                } else if (Configs.TEST_SERIALPORT) {
                    MyApplication.getInstance().restartApplication();
                }
                break;

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (persenter != null)
            persenter.checkPermission();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            attachListener();
            CarStateManager.getInstance().startRefreshCarState();//保证在当前界面时,刷新车辆状态界面
            if (myHandler != null) myHandler.sendEmptyMessage(0);
        } else {
            detaichListener();
            CarStateManager.getInstance().stopRefreshCarState();
            if (myHandler != null) myHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        if (persenter != null) {
            persenter.clear();
            persenter = null;
        }
        super.onDestroy();
    }

    /**
     * 点击故障码，展示故障码popwindow
     */
    public void showPopupWindow() {
        final View popupView = View.inflate(getContext(), R.layout.layout_state_pop, null);
        ListView lv_state_pop_content = (ListView) popupView.findViewById(R.id.lv_state_pop_content);
        lv_state_pop_content.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.item_state_pop, R.id.tv_item_state, state_list));
        lv_state_pop_content.setScrollbarFadingEnabled(false);
        TextView textView = new TextView(getActivity());
        textView.setText("有" + state_list.size() + "个预警信息");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(22);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 0, 0, 5);
        lv_state_pop_content.addHeaderView(textView);
        popupWindow = new PopupWindow(popupView, screenWidth / 2, screenHeight / 2);
        //设置点击PopupWindow以外的区域取消PopupWindow的显示
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
    }

    public void getPopContent() {
        if (state_list == null) {
            state_list = new ArrayList<String>();
        }
        state_list.clear();
        ArrayList<String> alarmDatas = CarStateManager.getInstance().alarmDatas;
        if (alarmDatas != null) {
            if (alarmDatas.size() > 3) {
//            sb.append("故障码:");
                for (int i = 3; i < alarmDatas.size(); i++) {
                    state_list.add(alarmDatas.get(i));
                }
            }
            if (!"水温".equals(alarmDatas.get(0))) {
                state_list.add("当前水温:" + alarmDatas.get(0) + "℃");
            }
            if (!"电压".equals(alarmDatas.get(1))) {
                state_list.add("当前电压:" + alarmDatas.get(1) + "v");
            }
            if (!"疲劳".equals(alarmDatas.get(2))) {
                state_list.add("您已驾驶:" + alarmDatas.get(2) + "小时");
            }
        }
        //判断有无故障
        if (state_list.size() > 0) {
            tv_state_record.setTextColor(getActivity().getResources().getColor(R.color.check_red));
            tv_state_record.setText("车辆存在故障");
            iv_state_safe.setBackgroundResource(R.drawable.trouble);
        } else {
            tv_state_record.setTextColor(getActivity().getResources().getColor(R.color.white));
            tv_state_record.setText("车辆无不良状态");
            iv_state_safe.setBackgroundResource(R.drawable.car_state_safe);
        }
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
                convertView = View.inflate(getActivity(), R.layout.item_grid_state, null);
                holder.iv = (ImageView) convertView.findViewById(R.id.iv_item_state);
                holder.tv = (TextView) convertView.findViewById(R.id.tv_item_state);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (data == null) {
                holder.iv.setImageDrawable(getActivity().getResources().getDrawable(stateResNoneIds[position]));
            } else {
                switch (dataStates[position]) {
                    case -1:
                    case 0:
                        holder.iv.setImageDrawable(getActivity().getResources().getDrawable(stateResNoneIds[position]));
                        break;
                    case 1:
                        holder.iv.setImageDrawable(getActivity().getResources().getDrawable(stateResOpenIds[position]));
                        break;
                    case 2:
                        if (position == 3 || position == 4) {
                            holder.iv.setImageDrawable(getActivity().getResources().getDrawable(stateResCloseIds[position]));
                        } else {
                            holder.iv.setImageDrawable(getActivity().getResources().getDrawable(stateResOpenIds[position]));
                        }
                        break;
                    case 3:
                        holder.iv.setImageDrawable(getActivity().getResources().getDrawable(stateResCloseIds[position]));
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


    public void showPermissionAlertView_FreeTrial(boolean isExpired, int numberOfDay) {
        if (permissionAlertAbleAdapter == null)
            permissionAlertAbleAdapter = new PermissionAlertViewAdapter(this);
        permissionAlertAbleAdapter.showPermissionAlertView_FreeTrial(isExpired, numberOfDay);
    }

    public void hidePermissionAlertView_FreeTrial() {
        if (permissionAlertAbleAdapter != null)
            permissionAlertAbleAdapter.hidePermissionAlertView_FreeTrial();
    }

    /**
     * 是否显示 车辆状态错误码 的提示语
     *
     * @param isVisiable
     */
    @Override
    public void setCarStateRecordVisiable(boolean isVisiable) {
        if (viewgrounp_stage_record != null)
            viewgrounp_stage_record.setVisibility(isVisiable ? View.VISIBLE : View.GONE);
    }

    public static class MyHandler extends SafeHandler<CarStatePage> {

        public MyHandler(CarStatePage object) {
            super(object);
        }

        @Override
        public void handleMessage(Message msg) {
            CarStatePage innerObject = getInnerObject();
            if (getInnerObject() == null)
                return;
            if (msg.what == 0) {
                innerObject.getPopContent();
                innerObject.myHandler.sendEmptyMessageDelayed(0, 10000);
            }
        }
    }
}
