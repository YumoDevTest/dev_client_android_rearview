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

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.control.VoiceManager;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.manager.CarStateManager;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.android.obd.rearview.framework.widget.TitleBar;
import com.mapbar.android.obd.rearview.modules.cardata.CarDataPage;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissionKey;
import com.mapbar.android.obd.rearview.modules.permission.PermissonCheckerOnStart;
import com.mapbar.android.obd.rearview.obd.Constants;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.obd.widget.TimerDialog;
import com.mapbar.obd.AlarmData;
import com.mapbar.obd.Manager;

import java.util.ArrayList;
import java.util.Locale;

import static com.mapbar.android.obd.rearview.framework.control.PageManager.ManagerHolder.pageManager;


/**
 * 首页
 * 包含1个viewpager,其下有4个子fragment 页
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
    private PermissonCheckerOnStart permissonCheckerOnStart;
    private PermissionManager permissionManager;

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
        titleBar.setText(titles[1], TitleBar.TitleArea.MID);

        vehicleCheckupPage = (VehicleCheckupPage) pageManager.createPage(VehicleCheckupPage.class);
        carDataPage = (CarDataPage) pageManager.createPage(CarDataPage.class);
        carStatePage = (CarStatePage) pageManager.createPage(CarStatePage.class);
        carMaintenancePage = (CarMaintenancePage) pageManager.createPage(CarMaintenancePage.class);
        controlTestPage = (ControlTestPage) pageManager.createPage(ControlTestPage.class);
        fragments = new ArrayList<>();
        fragments.add(vehicleCheckupPage);
        fragments.add(carDataPage);
        fragments.add(carStatePage);
//        fragments.add(carMaintenancePage);
//        fragments.add(controlTestPage);
        pager.setAdapter(fragmentPagerAdapter);
        currentPage = carDataPage;

        pager.setOffscreenPageLimit(3);
        mContext = MainActivity.getInstance();
        initDialog();
        title = titleBar;
        //默认进入页面为数据页面
        pager.setCurrentItem(1);

        rg_tabs.check(R.id.page_tab2);

        hideMainTitlebar();
        permissionManager = LogicFactory.createPermissionManager(getActivity());

        //下载权限
        permissonCheckerOnStart = new PermissonCheckerOnStart();
        permissonCheckerOnStart.downloadPermision(MainActivity.getInstance());

    }

    /**
     * 是否让 主fragment 的titlebar不可见。让子fragment的 titlebar 可见
     */
    private void hideMainTitlebar() {
        MainPage.title.setVisibilitySelf(false);
    }

    private void showMainTitlebar() {
        MainPage.title.setVisibilitySelf(true);
    }

    @Override
    public void setListener() {
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage.isUmenngWorking = true;
                currentPage.onPause();
                MainPage.title.setText("", TitleBar.TitleArea.RIGHT);
                switch (position) {
                    case 0:
                        rg_tabs.check(R.id.page_tab1);
                        titleBar.setText(titles[0], TitleBar.TitleArea.MID);
                        currentPage = vehicleCheckupPage;
                        break;
                    case 1:
                        rg_tabs.check(R.id.page_tab2);
                        titleBar.setText(titles[1], TitleBar.TitleArea.MID);
                        hideMainTitlebar();
                        currentPage = carDataPage;
                        break;
                    case 2:
                        rg_tabs.check(R.id.page_tab3);
                        titleBar.setText(titles[2], TitleBar.TitleArea.MID);
                        currentPage = carStatePage;
                        CarStateManager.getInstance().startRefreshCarState();
                        carStatePage.onResume();
                        break;
                    case 3:
                        rg_tabs.check(R.id.page_tab4);
                        MainPage.title.setText("保养校正", TitleBar.TitleArea.RIGHT);
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
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                super.onEvent(event, o);
                switch (event) {
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

                                        //检查是否有 体检模块权限，如果有，才弹出故障码提醒
                                        PermissionManager.PermissionResult result = permissionManager.checkPermission(PermissionKey.PERMISSION_CHECK_UP);
                                        if (!result.isValid)
                                            return;
                                        showNextTimerDialog();
                                    }
                                }
                            };
                            mHandlerBuy.postDelayed(r, 500);

                        }
                        break;

                    case OBDManager.EVENT_OBD_OTA_HAS_NEWFIRMEWARE:
                        carStatePage.showFirmwarePopu();
                        break;

                }
            }
        };
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);
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

//    /**
//     * 显示权限验证提醒，试用期，过期等
//     */
//    public void showPermissionAlert(){
//        final PermissionUpdateFailureDialog dialog = new PermissionUpdateFailureDialog(getActivity());
//        dialog.setOnRetryClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //do retry
//            }
//        });
//        dialog.setOnSkipClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }

//    /**
//     * 显示权限验证失败
//     */
//    public void showPermissionFailure(){
//        final PermissionUpdateFailureDialog dialog = new PermissionUpdateFailureDialog(getActivity());
//        dialog.setOnRetryClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //do retry
//            }
//        });
//        dialog.setOnSkipClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }
}
