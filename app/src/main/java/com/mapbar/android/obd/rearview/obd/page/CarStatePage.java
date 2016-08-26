package com.mapbar.android.obd.rearview.obd.page;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.bean.QRInfo;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.CarStateManager;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.android.obd.rearview.lib.ota.OTAManager;
import com.mapbar.android.obd.rearview.framework.widget.CarStateView;
import com.mapbar.android.obd.rearview.modules.carstate.contract.IVinChangeView;
import com.mapbar.android.obd.rearview.obd.FirmwareDialogHandler;
import com.mapbar.android.obd.rearview.modules.carstate.CarStatePresenter;
import com.mapbar.android.obd.rearview.modules.carstate.contract.ICarStateView;
import com.mapbar.android.obd.rearview.modules.permission.PermissionAlertViewAdapter;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAdatper;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.views.TitleBarView;
import com.mapbar.android.obd.rearview.views.VinBarcodeView;
import com.mapbar.obd.CarStatusData;
import com.mapbar.obd.Manager;

import java.util.ArrayList;

/**
 * 车辆 状态
 * Created by liuyy on 2016/5/7.
 */
public class CarStatePage extends AppPage implements View.OnClickListener, ICarStateView, IVinChangeView {

    private CarStateView carStateView;
    private CarStatusData data;
    @ViewInject(R.id.gv_state)
    private GridView gvState;
    @ViewInject(R.id.tv_state_record)
    private TextView tv_state_record;
    @ViewInject(R.id.iv_state_safe)
    private ImageView iv_state_safe;
    @ViewInject(R.id.tv_state)
    private TextView tv_state;

    //车辆不良状态的提示语，点击能有详细故障码，被 体检功能权限控制可见性
    @ViewInject(R.id.viewgrounp_stage_record)
    private ViewGroup viewgrounp_stage_record;


    private Button btn_state_pop_close;
    private String[] stateNames;
    private int[] stateResCloseIds = {R.drawable.car_light_close, R.drawable.car_window_close, R.drawable.car_lock_close, R.drawable.car_door_close, R.drawable.car_trunk_close, R.drawable.car_sunroof_close};
    private int[] stateResOpenIds = {R.drawable.car_light_open, R.drawable.car_window_open, R.drawable.car_lock_open, R.drawable.car_door_open, R.drawable.car_trunk_open, R.drawable.car_sunroof_open};
    private int[] stateResNoneIds = {R.drawable.car_light_none, R.drawable.car_window_none, R.drawable.car_lock_none, R.drawable.car_door_none, R.drawable.car_trunk_none, R.drawable.car_sunroof_none};
    private StateAdapter adapter;
    private PopupWindow popupWindow;
    private PopupWindow firmwarePopu;
    private StringBuilder sb = new StringBuilder();
    private boolean isFirstDataUpdate = true;
    private TitleBarView titlebarview1;
    private FirmwareDialogHandler firmwareDialogHandler;
    private CarStatePresenter presenter;
    private IPermissionAlertViewAdatper permissionAlertAbleAdapter;
    private VinBarcodeView vinBarcodeView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_car_state);
    }

    @Override
    public void initView() {
        titlebarview1 = (TitleBarView) getContentView().findViewById(R.id.titlebarview1);
        titlebarview1.setTitle(R.string.page_title_car_state);


        carStateView = new CarStateView(getContentView(), R.id.v_carstate);
        carStateView.setData(data);
        stateNames = getContext().getResources().getStringArray(R.array.state_names);
        gvState.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new StateAdapter();
        gvState.setAdapter(adapter);

        firmwareDialogHandler = new FirmwareDialogHandler();
        presenter = new CarStatePresenter(this);
    }

    @Override
    public void setListener() {
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
                        //TODO 当有固件升级时则自动弹出升级或取消的按钮
                        //当没有vin则在车辆状态页弹出vin二维码并且能够左右滑动
                        if (isFirstDataUpdate) {
                            isFirstDataUpdate = false;
                            OTAManager.getInstance().checkVinVersion(getActivity());
                        }
                        presenter.notifyBeginCheckFirmwareVersion();
                        break;
                    case OBDManager.EVENT_OBD_OTA_HAS_NEWFIRMEWARE://TODO 弹窗 到处都可以弹
                        tv_state.setText(getResources().getString(R.string.firmware_update_tip));
                        break;
                }
            }
        };
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);
        tv_state_record.setOnClickListener(this);
        tv_state.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showVinInputDialog();

            }
        }, 5000);
    }

    /**
     * 显示vin 二维码 的对话框
     */
    public void showVinInputDialog() {
        if (getContentView() instanceof FrameLayout) {
            FrameLayout frameLayout = (FrameLayout) getContentView();
            if (vinBarcodeView == null)
                vinBarcodeView = new VinBarcodeView(getActivity());
            vinBarcodeView.showQrBarcode();
            frameLayout.addView(vinBarcodeView);
        }
    }

    /**
     * 显示 VIN修改 扫码成功
     */
    public void showVinScanOK() {
        if (getContentView() instanceof FrameLayout) {
            FrameLayout frameLayout = (FrameLayout) getContentView();
            if (vinBarcodeView == null)
                vinBarcodeView = new VinBarcodeView(getActivity());
            vinBarcodeView.setText(R.string.vin_alert_dialog_scan_success);
            vinBarcodeView.showQrBarcode();
            frameLayout.addView(vinBarcodeView);
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
            case R.id.tv_state_record://查看不良状态记录
                showPopupWindow();
                break;
            case R.id.btn_state_pop_close://关闭不良状态记录
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                break;
            case R.id.tv_state://提示有固件升级
                if (tv_state != null && tv_state.getText().toString().trim().equals(getResources().getString(R.string.firmware_update_tip))) {
                    showFirmwarePopu();
                } else if (Configs.TEST_SERIALPORT) {
                    MainActivity.getInstance().restartApp();
                }
                break;

        }
    }

    public void showFirmwarePopu() {
        firmwareDialogHandler.showAtLocation(getContentView(), Gravity.CENTER, 0, 0, new FirmwareDialogHandler.FlashListener() {
            @Override
            public void onFlashSucc() {
                tv_state.setText("当前车型支持车辆控制功能");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null)
            presenter.checkPermission();
        //判断有无故障
        if (!TextUtils.isEmpty(getPopContent())) {
            tv_state_record.setTextColor(MainActivity.getInstance().getResources().getColor(R.color.check_red));
            tv_state_record.setText("车辆存在故障码");

            iv_state_safe.setBackgroundResource(R.drawable.trouble);
        }

        if (isUmenngWorking) {
            MobclickAgentEx.onPageStart("CarStatePage"); //统计页面
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CarStateManager.getInstance().stopRefreshCarState();
        if (isUmenngWorking) {
            MobclickAgentEx.onPageEnd("CarStatePage");
        }
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.clear();
            presenter = null;
        }
        super.onDestroy();
    }

    /**
     * 点击故障码，展示故障码popwindow
     */
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
}
