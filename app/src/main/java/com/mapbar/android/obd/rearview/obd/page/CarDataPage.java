package com.mapbar.android.obd.rearview.obd.page;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.mapbar.android.net.HttpHandler;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.common.DecFormatUtil;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.common.OBDHttpHandler;
import com.mapbar.android.obd.rearview.framework.common.TimeUtils;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.obd.UpdateService;
import com.mapbar.android.obd.rearview.obd.bean.AppInfo;
import com.mapbar.android.obd.rearview.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.umeng.UmengConfigs;
import com.mapbar.obd.Manager;
import com.mapbar.obd.RealTimeData;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by THINKPAD on 2016/5/6.
 */
public class CarDataPage extends AppPage implements View.OnClickListener {
    private final int[] icons = {R.drawable.car_data_gas_consum, R.drawable.car_data_trip_time, R.drawable.car_data_trip_length, R.drawable.car_data_drive_cost,
            R.drawable.car_data_speed, R.drawable.car_data_rpm, R.drawable.car_data_voltage, R.drawable.car_data_temperature, R.drawable.car_data_average_gas_consum};
    boolean testAppUpdate = false;
    PopupWindow updatePopu;
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
    private RealTimeData realTimeData;
    private String[] dataNames = getContext().getResources().getStringArray(R.array.data_names);
    private String[] units = getContext().getResources().getStringArray(R.array.units);
    private boolean isFirst = true;
    private int spv0, spv1, spv2, spv3;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showAppUpdate((AppInfo) msg.obj);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_car_data);
    }

    @Override
    public void initView() {
        sharedPreferences = MainActivity.getInstance().getSharedPreferences("car_data", Context.MODE_PRIVATE);
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

        checkAppVersion();
    }

    /**
     * 应用升级
     */
    private void checkAppVersion() {

        HttpHandler http = new OBDHttpHandler(MainActivity.getInstance());
        http.addParamete("package_name", getActivity().getPackageName());

        String url = "http://wdservice.mapbar.com/appstorewsapi/checkexistlist/" + Build.VERSION.SDK_INT;//接口14.1

        http.setRequest(url, HttpHandler.HttpRequestType.GET);
        http.setCache(HttpHandler.CacheType.NOCACHE);
        http.setHeader("ck", "a7dc3b0377b14a6cb96ed3d18b5ed117");//TODO

        HttpHandler.HttpHandlerListener listener = new HttpHandler.HttpHandlerListener() {
            @Override
            public void onResponse(int httpCode, String str, byte[] responseData) {
                if (httpCode == HttpStatus.SC_OK) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(new String(responseData));
                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.DEBUG)) {
                            Log.d(LogTag.TEMP, " JSONObject-->> " + object);
                        }
                        int code = object.getInt("status");
                        switch (code) {
                            case 200:
                                //TODO 比较versionCode,有新版本则弹窗提示 相关信息封装到bean里
                                AppInfo info = parseAppInfo((JSONObject) object.getJSONArray("data").get(0));
                                if ((info != null && hasNewAppVersion(info)) || testAppUpdate) {
                                    Message msg = Message.obtain();
                                    msg.obj = info;
                                    handler.sendMessage(msg);
                                }
                                break;

                            default:
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        };
        http.setHttpHandlerListener(listener);
        http.execute();
    }

    private boolean hasNewAppVersion(AppInfo info) {
        int versionCode = -1;
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(getActivity().getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (Exception e) {
        }
        return (info.getVersion_no() > versionCode && versionCode != -1) ? true : false;
    }

    private AppInfo parseAppInfo(JSONObject data) {
        AppInfo info = new AppInfo();
        try {
            info.setDescription(data.getString("description"));
            info.setVersion_no(data.getInt("version_no"));
            info.setApk_path(data.getString("apk_path"));
            info.setName(data.getString("name"));
            info.setPackage_name(data.getString("package_name"));
            info.setIcon_path(data.getString("icon_path"));
            info.setApp_id(data.getString("app_id"));
            info.setVersion_id(data.getString("version_id"));
            info.setSize(data.getInt("size"));
        } catch (JSONException e) {
            info = null;
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 点击下载,则开启service,并通知栏同步下载进度
     *
     * @param info
     */
    private void showAppUpdate(final AppInfo info) {
        String versionName = "unknow";
        try {
            String pkName = getActivity().getPackageName();
            versionName = getActivity().getPackageManager().getPackageInfo(pkName, 0).versionName;
        } catch (Exception e) {
        }
        View popupView = View.inflate(getActivity(), R.layout.layout_app_update_pop, null);

        TextView tv_update_pop_content = (TextView) popupView.findViewById(R.id.tv_update_pop_content);
        TextView tv_update_app_info = (TextView) popupView.findViewById(R.id.tv_update_app_info);
        tv_update_pop_content.setText(info.getDescription());
        tv_update_app_info.setText("最新版本 " + versionName + "      " + "新版本大小:" + info.getSize() + "M");
        View tv_update_confirm = popupView.findViewById(R.id.tv_update_confirm);
        View tv_update_cancle = popupView.findViewById(R.id.tv_update_cancle);

        tv_update_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updatePopu != null)
                    updatePopu.dismiss();
                startAppDownload(info);
            }
        });

        tv_update_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updatePopu != null)
                    updatePopu.dismiss();
            }
        });

        updatePopu = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        updatePopu.setOutsideTouchable(false);
        updatePopu.setBackgroundDrawable(new BitmapDrawable());
        updatePopu.showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
//        test();

    }

    private void startAppDownload(AppInfo info) {
        Intent updateIntent = new Intent(getActivity(), UpdateService.class);
        updateIntent.putExtra("app_info", info);
        getActivity().startService(updateIntent);
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
        };
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgentEx.onPageStart("CarDataPage"); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgentEx.onPageEnd("CarDataPage");
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
        //当车速低于10时,更改单位
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
     * 设置时实数据
     */
    public void upData() {// 日志
        if (Log.isLoggable(LogTag.FRAMEWORK, Log.VERBOSE)) {
            Log.v(LogTag.FRAMEWORK, "upData+++++++");
            Log.v(LogTag.FRAMEWORK, "ThreadId:" + Thread.currentThread().getId());
            Log.v(LogTag.FRAMEWORK, "speed:" + realTimeData.rpm);
        }

        tv_carData_value_1.setText(transform(spv0));
        tv_carData_value_2.setText(transform(spv1));
        tv_carData_value_3.setText(transform(spv2));
        tv_carData_value_4.setText(transform(spv3));

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
                    uMeng(number, value1);
                }

                //更新数据
                getPopData();
                upDataView();
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(getContentView(), Gravity.CENTER, -100, -100);
    }

    /**
     * 友盟统计
     *
     * @param number
     * @param value1
     */
    private void uMeng(int number, int value1) {
        switch (value1) {
            case 0:
                MobclickAgentEx.onEvent(UmengConfigs.GASCONSUM);
                break;
            case 1:
                MobclickAgentEx.onEvent(UmengConfigs.TRIPTIME);
                break;
            case 2:
                MobclickAgentEx.onEvent(UmengConfigs.TRIPLENGTH);
                break;
            case 3:
                MobclickAgentEx.onEvent(UmengConfigs.DRIVECOST);
                break;
            case 4:
                MobclickAgentEx.onEvent(UmengConfigs.SPEED);
                break;
            case 5:
                MobclickAgentEx.onEvent(UmengConfigs.RPM);
                break;
            case 6:
                MobclickAgentEx.onEvent(UmengConfigs.VOLTAGE);
                break;
            case 7:
                MobclickAgentEx.onEvent(UmengConfigs.ENGINECOOLANTTEMPERATURE);
                break;
            case 8:
                MobclickAgentEx.onEvent(UmengConfigs.AVERAGEGASCONSUM);
                break;
        }
    }

}
