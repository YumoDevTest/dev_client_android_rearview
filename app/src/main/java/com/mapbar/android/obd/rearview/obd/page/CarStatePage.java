package com.mapbar.android.obd.rearview.obd.page;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.bean.QRInfo;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.common.QRUtils;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.CarStateManager;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.android.obd.rearview.framework.manager.OTAManager;
import com.mapbar.android.obd.rearview.framework.widget.CarStateView;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.umeng.MobclickAgentEx;
import com.mapbar.obd.CarStatusData;
import com.mapbar.obd.Firmware;
import com.mapbar.obd.Manager;

import java.io.File;
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
    @ViewInject(R.id.tv_state)
    private TextView tv_state;

    @ViewInject(R.id.qr_content_view)
    private View qr_content_view;
    @ViewInject(R.id.content)
    private View content;
    @ViewInject(R.id.btn_close_QRpop)
    private Button btn_close_QRpop;
    @ViewInject(R.id.iv_qr)
    private ImageView iv_qr;
    @ViewInject(R.id.tv_qr_info)
    private TextView tv_qr_info;

    private Button btn_state_pop_close;
    private String[] stateNames;
    private int[] stateResCloseIds = {R.drawable.car_light_close, R.drawable.car_window_close, R.drawable.car_lock_close, R.drawable.car_door_close, R.drawable.car_trunk_close, R.drawable.car_sunroof_close};
    private int[] stateResOpenIds = {R.drawable.car_light_open, R.drawable.car_window_open, R.drawable.car_lock_open, R.drawable.car_door_open, R.drawable.car_trunk_open, R.drawable.car_sunroof_open};
    private int[] stateResNoneIds = {R.drawable.car_light_none, R.drawable.car_window_none, R.drawable.car_lock_none, R.drawable.car_door_none, R.drawable.car_trunk_none, R.drawable.car_sunroof_none};
    private StateAdapter adapter;
    private PopupWindow popupWindow;
    private PopupWindow firmwarePopu;
    private StringBuilder sb = new StringBuilder();
    private int showtimes;

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
//                        StringUtil.toastStringShort("绑定vin");
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                OTAManager.getInstance().checkVinVersion(getContext());
//                            }
//                        }, 1000);
                        QRInfo qrInfo = (QRInfo) o;//TODO 设置url
                        Bitmap bmQR = QRUtils.createQR(qrInfo.getUrl());
                        iv_qr.setImageBitmap(bmQR);
                        tv_qr_info.setText(qrInfo.getContent());
                        setQrViewVisiable(true);

                        break;

                    case OBDManager.EVENT_OBD_OTA_SCANVIN_SUCC://TODO 扫描成功
                        QRInfo qrInfo_scan_succ = (QRInfo) o;
                        tv_qr_info.setText(qrInfo_scan_succ.getContent());
                        setQrViewVisiable(true);
                        break;

                    case OBDManager.EVENT_OBD_USER_BINDVIN_SUCC:
                        setQrViewVisiable(false);
                        break;

                    case OBDManager.EVENT_OBD_USER_BINDVIN_FAILED:
                        setQrViewVisiable(true);
                        break;

                    case Manager.Event.dataCollectSucc:
                        //TODO 当有固件升级时则自动弹出升级或取消的按钮
                        //当没有vin则在车辆状态页弹出vin二维码并且能够左右滑动
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                OTAManager.getInstance().checkVinVersion(getContext());
                            }
                        }, 2000);

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
        btn_close_QRpop.setOnClickListener(this);

    }

    private void setQrViewVisiable(boolean isVisiable) {
        if (isVisiable) {
            content.setVisibility(View.GONE);
            qr_content_view.setVisibility(View.VISIBLE);
        } else {
            content.setVisibility(View.VISIBLE);
            qr_content_view.setVisibility(View.GONE);
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
            case R.id.btn_close_QRpop:
                setQrViewVisiable(false);
                break;
        }
    }

    public void showFirmwarePopu() {
        final View popupView = View.inflate(MainActivity.getInstance(), R.layout.layout_firmware_pop, null);

        final View prompt_content = popupView.findViewById(R.id.firmware_update_prompt_content);
        final View succ_content = popupView.findViewById(R.id.firmware_update_succ_content);
        final View fail_content = popupView.findViewById(R.id.firmware_update_fail_content);
        final View progress_content = popupView.findViewById(R.id.firmware_update_progress_content);

        prompt_content.setVisibility(View.VISIBLE);
        succ_content.setVisibility(View.GONE);
        fail_content.setVisibility(View.GONE);
        progress_content.setVisibility(View.GONE);

        final TextView tv_download_progress = (TextView) progress_content.findViewById(R.id.tv_download_progress);
        final ProgressBar progressbar = (ProgressBar) progress_content.findViewById(R.id.progressbar);

        final TextView tv_firmware_pop_update = (TextView) popupView.findViewById(R.id.tv_firmware_pop_update);
        TextView tv_firmware_pop_cancle = (TextView) popupView.findViewById(R.id.tv_firmware_pop_cancle);
        tv_firmware_pop_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //刷新固件
                Log.e(LogTag.OBD, "whw -->>固件");
                //TODO 显示刷固件进度条
                prompt_content.setVisibility(View.GONE);
                succ_content.setVisibility(View.GONE);
                fail_content.setVisibility(View.GONE);
                progress_content.setVisibility(View.VISIBLE);

                OTAManager.getInstance().upgrade(MainActivity.getInstance(), new Firmware.UpgradeCallback() {
                    @Override
                    public void onDownResult(int statusCode, File file) {
                        //TODO 下载失败
                    }

                    @Override
                    public void onDownProgress(int bytesWritten, int totalSize) {

                    }

                    @SuppressLint("NewApi")
                    @Override
                    public void onFlashResult(int statusCode, File file) {
                        progress_content.setVisibility(View.GONE);
                        switch (statusCode) {
                            case Firmware.UpgradeCallback.STATUSCODE_FLASH_OK:
                                Log.d(LogTag.OTA, "whw -->> 升级成功 == ");
                                //TODO 显示升级成功的ui 点击完成 重启客户端
                                succ_content.setVisibility(View.VISIBLE);
                                succ_content.findViewById(R.id.firmware_update_succ_confirm).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        prompt_content.setVisibility(View.GONE);
                                        succ_content.setVisibility(View.VISIBLE);
                                        fail_content.setVisibility(View.GONE);
                                        progress_content.setVisibility(View.GONE);
                                        firmwarePopu.dismiss();
                                        //TODO 重启客户端
                                        MainActivity.getInstance().restartApp();
                                    }
                                });
                                tv_state.setText("当前车型支持宝马车辆控制功能");
                                break;
                            case Firmware.UpgradeCallback.STATUSCODE_FLASH_FAILED:
                                //TODO 显示升级失败的ui 点击重试重新升级

                                prompt_content.setVisibility(View.GONE);
                                succ_content.setVisibility(View.GONE);
                                fail_content.setVisibility(View.VISIBLE);
                                progress_content.setVisibility(View.GONE);

                                fail_content.findViewById(R.id.firmware_update_fail_confirm).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        prompt_content.setVisibility(View.GONE);
                                        succ_content.setVisibility(View.GONE);
                                        fail_content.setVisibility(View.GONE);
                                        progress_content.setVisibility(View.GONE);
                                        tv_firmware_pop_update.callOnClick();
                                    }
                                });
                                fail_content.findViewById(R.id.tv_firmware_pop_fail_cancle).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //TODO 取消popu
                                        firmwarePopu.dismiss();
                                        fail_content.setVisibility(View.GONE);
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFlashProgress(int bytesWritten, int totalSize) {
                        //TODO 显示刷固件进度
                        progress_content.setVisibility(View.VISIBLE);
                        int pb = (int) (Float.intBitsToFloat(bytesWritten) / Float.intBitsToFloat(totalSize) * 100f);
                        progressbar.setProgress(pb);
                        tv_download_progress.setText(pb + "%");
                    }
                });
            }
        });
        tv_firmware_pop_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消直接消失popu
                firmwarePopu.dismiss();
            }
        });
        firmwarePopu = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置点击PopupWindow以外的区域取消PopupWindow的显示
//        firmwarePopu.setOutsideTouchable(false);
        firmwarePopu.setBackgroundDrawable(new BitmapDrawable());
        firmwarePopu.showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        CarStateManager.getInstance().startRefreshCarState();
        showtimes++;
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
