package com.mapbar.android.obd.rearview.obd.page;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import com.ixintui.pushsdk.PushSdkApi;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.common.LayoutUtils;
import com.mapbar.android.obd.rearview.framework.common.QRUtils;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.control.VoiceManager;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiPushManager;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.widget.TitleBar;
import com.mapbar.android.obd.rearview.obd.Constants;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.obd.widget.TimerDialog;
import com.mapbar.obd.AlarmData;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCar;
import com.mapbar.obd.UserCenter;

import java.util.ArrayList;
import java.util.Locale;

import static com.mapbar.android.obd.rearview.framework.control.PageManager.ManagerHolder.pageManager;


/**
 *
 */
public class MainPage extends AppPage {

    public static TitleBar title;
    protected static ArrayList<Object> sAlarmDataList = new ArrayList<Object>();
    private TitleBar titleBar;
    @ViewInject(R.id.pager_main)
    private ViewPager pager;
    @ViewInject(R.id.rg_tabs)
    private RadioGroup rg_tabs;
    private String[] titles;
    private CarDataPage carDataPage;
    private CarStatePage carStatePage;
    private CarMaintenancePage carMaintenancePage;
    private VehicleCheckupPage vehicleCheckupPage;
    private ControlTestPage controlTestPage;
    private ArrayList<Fragment> fragments;
    private AppPage currentPage;
    private TimerDialog mAlarmTimerDialog;
    /***
     * 报警
     */
    private boolean mAlarmOn = false;
    private Context mContext;
    private Handler mHandlerBuy = new Handler();
    private boolean isPush = true;

    private FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(MainActivity.getInstance().getSupportFragmentManager()) {

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_main);
    }

    @Override
    public void initView() {
        titles = getResources().getStringArray(R.array.page_titles);
        titleBar = new TitleBar(this, R.id.title_main);
        vehicleCheckupPage = (VehicleCheckupPage) pageManager.createPage(VehicleCheckupPage.class);
        carDataPage = (CarDataPage) pageManager.createPage(CarDataPage.class);
        carStatePage = (CarStatePage) pageManager.createPage(CarStatePage.class);
        carMaintenancePage = (CarMaintenancePage) pageManager.createPage(CarMaintenancePage.class);
        controlTestPage = (ControlTestPage) pageManager.createPage(ControlTestPage.class);
        fragments = new ArrayList<>();
        fragments.add(vehicleCheckupPage);
        fragments.add(carDataPage);
        fragments.add(carStatePage);
        fragments.add(carMaintenancePage);
        fragments.add(controlTestPage);
        pager.setAdapter(fragmentPagerAdapter);
        currentPage = vehicleCheckupPage;

        pager.setOffscreenPageLimit(3);
        mContext = MainActivity.getInstance();
        initDialog();
        title = titleBar;
    }


    @Override
    public void setListener() {
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage.onPause();
                switch (position) {
                    case 0:
                        rg_tabs.check(R.id.page_tab1);
                        titleBar.setText(titles[0], TitleBar.TitleArea.MID);
                        currentPage = vehicleCheckupPage;
                        break;
                    case 1:
                        rg_tabs.check(R.id.page_tab2);
                        titleBar.setText(titles[1], TitleBar.TitleArea.MID);
                        currentPage = carDataPage;
                        break;
                    case 2:
                        rg_tabs.check(R.id.page_tab3);
                        titleBar.setText(titles[2], TitleBar.TitleArea.MID);
                        currentPage = carStatePage;
                        break;
                    case 3:
                        rg_tabs.check(R.id.page_tab4);
                        titleBar.setText(titles[3], TitleBar.TitleArea.MID);
                        currentPage = carMaintenancePage;
                        break;
                    case 4:
                        rg_tabs.check(R.id.page_tab4);
                        titleBar.setText(titles[3], TitleBar.TitleArea.MID);
                        currentPage = controlTestPage;
                        break;
                }
                if (!currentPage.isInited()) {
                    currentPage.setIsInited(true);
                } else {
                    currentPage.onResume();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //监听禁止ViewPager切换页面
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                super.onEvent(event, o);
                switch (event) {
                    /*case Manager.Event.obdPhysicalCheckStart:
                        pager.setNoScroll(true);
                        break;
                    case Manager.Event.obdPhysicalConditionFailed:
                        pager.setNoScroll(false);
                        break;
                    case Manager.Event.obdPhysicalCheckEnd:
                        pager.post(new Runnable() {
                            @Override
                            public void run() {
                                pager.setNoScroll(false);
                            }
                        });
                        break;*/
                    case Manager.Event.alarm:
                        if (!sAlarmDataList.contains(o)) {
                            sAlarmDataList.add(o);
                        }

                        if (!mAlarmTimerDialog.isShowing()) {
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    setAlarmOn(true);
                                    if (!mAlarmTimerDialog.isShowing() && 0 < sAlarmDataList.size()) {
                                        showNextTimerDialog();
                                    }
                                }
                            };
                            mHandlerBuy.postDelayed(r, 500);
//                            }
                        }
                        break;
                    /*case Manager.Event.loginSucc:
                        Manager.getInstance().queryRemoteUserCar();
                        break;*/
                    case Manager.Event.queryCarSucc:
                        UserCar[] cars = (UserCar[]) o;
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> 查询远程车辆成功");
                        }
                        if (cars != null && cars.length > 0) {
                            if (TextUtils.isEmpty(cars[0].carGenerationId)) {
                                // 日志
                                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                                    Log.d(LogTag.OBD, " -->> 未注册，数据无效");
                                }
                                showRegQr(Global.getAppContext().getResources().getString(R.string.reg_info));
                                outTime();
                            } else {
                                // 日志
                                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                                    Log.d(LogTag.OBD, " -->> 已注册，数据有效");
                                    Log.d(LogTag.OBD, "carGenerationId -->> " + cars[0].carGenerationId);
                                }
                                //关闭二维码
                                LayoutUtils.disQrPop();
                                startServer();
                            }
                        } else {
                            // 日志
                            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                                Log.d(LogTag.OBD, " -->> 未注册，数据无效");
                            }
                            showRegQr(Global.getAppContext().getResources().getString(R.string.reg_info));
                            outTime();
                        }
                        break;
                    case Manager.Event.queryCarFailed:
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> 查询远程车辆失败");
                        }
                        //显示二维码
                        showRegQr(Global.getAppContext().getResources().getString(R.string.reg_info));
                        outTime();
                        break;
                    case Manager.Event.DeviceloginSucc:
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> 设备登录成功");
                        }
                        login2();
                        break;
                    case Manager.Event.DeviceloginFailed:
                        // 日志
                        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                            Log.d(LogTag.OBD, " -->> 设备登录失败");
                        }
                        //延迟，最大重试次数
                        mHandlerBuy.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                UserCenter.getInstance().DeviceLoginlogin(Utils.getImei());
                            }
                        }, 5 * 1000);
                        break;
                }
            }
        };
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);
        login1();
        //监听推送消息
        AixintuiPushManager.getInstance().setPushCallBack(new AixintuiPushManager.PushCallBack() {
            @Override
            public void pushData(int type, int state, String userId, String token) {
                // 日志
                if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                    Log.d(LogTag.PUSH, " -->> MainPag回调");
                }
                switch (type) {
                    case 0:
                        if (state == 1)
                            showRegQr(Global.getAppContext().getResources().getString(R.string.scan_succ));
                        break;
                    case 1:
                        if (state == 1 || state == 3) { //注册成功

                            showRegQr(Global.getAppContext().getResources().getString(R.string.reg_succ));
                            isPush = false;//推送成功
                            // 更新本地用户信息
                            if (userId != null && token != null) {
                                // 日志
                                if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                                    Log.d(LogTag.PUSH, "userId -->> " + userId + "    token--->" + token);
                                    Log.d(LogTag.PUSH, "当前userId -->>" + UserCenter.getInstance().getCurrentIdAndType().userId);
                                }
                                boolean isUpdata = UserCenter.getInstance().UpdateUserInfoByRemoteLogin(userId, null, token, "zs");
                                if (isUpdata) {
                                    //远程查询车辆信息
                                    Manager.getInstance().queryRemoteUserCar();
                                } else {
                                    //显示二维码
                                    showRegQr(Global.getAppContext().getResources().getString(R.string.reg_info));
                                    // 日志
                                    if (Log.isLoggable(LogTag.PUSH, Log.DEBUG)) {
                                        Log.d(LogTag.PUSH, " -->>更新本地用户信息失败 ");
                                    }
                                }
                            }

                        } else if (state == 2) {
                            showRegQr(Global.getAppContext().getResources().getString(R.string.reg_info));
                        }

                }
            }
        });
    }


    private void initDialog() {
        mAlarmTimerDialog = new TimerDialog(mContext, new TimerDialog.Listener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.rela_close:
                        if (mAlarmTimerDialog != null && mAlarmTimerDialog.isShowing()) {
                            mAlarmTimerDialog.cancel();
                        }
                }
            }

            @Override
            public void onCancel(DialogInterface dialog) {
                showNextTimerDialog();
            }

        }, true, 5);

        mAlarmTimerDialog.setCancelable(false);

    }

    private void showNextTimerDialog() {
        if (sAlarmDataList.size() > 0 && sAlarmDataList.get(0) instanceof AlarmData) {
            AlarmData data = (AlarmData) sAlarmDataList.get(0);
            sAlarmDataList.remove(0);
            mAlarmTimerDialog.setCountdown(TimerDialog.DEFAULT_COUNTDOWN_NUMBER);

            String content = "";
            int type = data.getType();
            switch (type) {
                case Manager.AlarmType.errCode: {
                    // 故障预警
                    content = data.getString();
                    mAlarmTimerDialog.show(mContext.getResources().getString(R.string.dlg_errCode, content));
                    if (Constants.IS_OVERSEAS_EDITION) {
                        StringBuilder contentEn = new StringBuilder();
                        if (!TextUtils.isEmpty(content)) {
                            for (int i = 0; i < content.length(); i++) {
                                contentEn.append(content.charAt(i)).append(" ");
                            }
                        }
                        content = contentEn.toString();
                    }
                    VoiceManager.getInstance().sendBroadcastTTS(mContext.getResources().getString(R.string.bca_errCode, content));
                }
                break;
                case Manager.AlarmType.temperature: {
                    // 水温预警
                    content = String.format(Locale.getDefault(), "%d", data.getInt());
                    mAlarmTimerDialog.show(mContext.getResources().getString(R.string.dlg_temperature, content));
                    VoiceManager.getInstance().sendBroadcastTTS(mContext.getResources().getString(R.string.bca_temperature, content));
                }
                break;
                case Manager.AlarmType.voltage: {
                    // 电压预警
                    content = String.format(Locale.getDefault(), "%.1f", data.getFloat());
                    mAlarmTimerDialog.show(mContext.getResources().getString(R.string.dlg_voltage, content));
                    VoiceManager.getInstance().sendBroadcastTTS(mContext.getResources().getString(R.string.bca_voltage, content));
                }
                break;
                case Manager.AlarmType.tired: {
                    // 疲劳驾驶预警
                    content = String.format(Locale.getDefault(), "%.1f", data.getFloat());
                    mAlarmTimerDialog.show(mContext.getResources().getString(R.string.dlg_tired, content));
                    VoiceManager.getInstance().sendBroadcastTTS(mContext.getResources().getString(R.string.bca_tired, content));
                }
                break;

            }
            if (mAlarmTimerDialog.getContentTextView() != null) {
                mAlarmTimerDialog.getContentTextView().setTextColor(mContext.getResources().getColor(R.color.dashboard_red_text));
            }

        }
    }


    /**
     * @return the mAlarmOn
     */
    public boolean isAlarmOn() {
        return mAlarmOn;
    }

    /**
     * @param on the mAlarmOn to set
     */
    public void setAlarmOn(boolean on) {
        this.mAlarmOn = on;
    }


    /**
     * 自动登录、设备登录
     */
    private void login1() {
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, " -->> 登录开始");
        }
//        UserCenter.getInstance().login("18610857365", "111111");
        if (UserCenter.getInstance().loginAutomatically()) {
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, " -->> 自动登录成功");
            }
            login2();
        } else {
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, " -->> 自动登录失败");
            }
            UserCenter.getInstance().DeviceLoginlogin(Utils.getImei());

        }
    }

    /**
     * 查询本地车辆信息
     */
    private void login2() {
        LocalUserCarResult result = Manager.getInstance().queryLocalUserCar();
        if (result != null) {
            UserCar car = result.userCars == null || result.userCars.length == 0 ? null : result.userCars[0];
            if (car != null && !TextUtils.isEmpty(car.carGenerationId)) {
                // 日志
                if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                    Log.d(LogTag.OBD, " -->> 查询本地车辆成功");
                }
                //启动业务
                startServer();
                return;
            }
        }
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, " -->> 查询本地车辆失败");
        }
        //查询远程车辆信息
        Manager.getInstance().queryRemoteUserCar();
    }

    public void startServer() {
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, " -->> 启动业务");
        }
        Manager.getInstance().openDevice(Utils.getImei());
    }

    /**
     * 弹出二维码
     */
    private void showRegQr(String info) {

        if (AixintuiConfigs.push_token != null) {
            QRUtils.showRegQr(info);
        } else {
            //注册爱心推
            PushSdkApi.register(MainActivity.getInstance(), AixintuiConfigs.AIXINTUI_APPKEY, Utils.getChannel(MainActivity.getInstance()), Utils.getVersion(MainActivity.getInstance()) + "");
        }
    }


    public void outTime() {
        mHandlerBuy.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPush) {//推送失败
                    login1();//设备登录
                }
            }
        }, 1000 * 30);
    }
}
