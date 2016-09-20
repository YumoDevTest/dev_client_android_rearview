package com.mapbar.android.obd.rearview.modules.common;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import com.mapbar.android.obd.rearview.BuildConfig;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.manager.CarStateManager;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.android.obd.rearview.lib.base.AppPage;
import com.mapbar.android.obd.rearview.lib.notify.Notification;
import com.mapbar.android.obd.rearview.lib.services.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.modules.carstate.CarStatePage;
import com.mapbar.android.obd.rearview.modules.maintenance.CarMaintenancePage;
import com.mapbar.android.obd.rearview.modules.controltest.ControlTestPage;
import com.mapbar.android.obd.rearview.modules.checkup.VehicleCheckupPage;
import com.mapbar.obd.foundation.tts.TextToSpeechManager;
import com.mapbar.android.obd.rearview.modules.cardata.CarDataPage;
import com.mapbar.android.obd.rearview.modules.common.contract.IMainPageView;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissonCheckerOnStart;
import com.mapbar.android.obd.rearview.util.TraceUtil;
import com.mapbar.android.obd.rearview.views.TimerDialog;
import java.util.ArrayList;


/**
 * 首页
 * 包含1个viewpager,其下有4个子fragment 页
 */
public class MainPage extends AppPage implements IMainPageView {
    private static final String TAG = MainPage.class.getSimpleName();

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
    private TimerDialog mAlarmTimerDialog;
    /***
     * 报警
     */
    private boolean mAlarmOn = false;
    private Handler mHandlerBuy = new Handler();
    private boolean isPush = true;
    private PermissonCheckerOnStart permissonCheckerOnStart;
    private PermissionManager permissionManager;
    private MainPagePersenter persenter;

    private FragmentPagerAdapter fragmentPagerAdapter;
    private OBDSDKListenerManager.SDKListener sdkListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_main);
        fragmentPagerAdapter = new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager());
    }

    @Override
    public void initView() {
        titles = getResources().getStringArray(R.array.page_titles);

        vehicleCheckupPage = new VehicleCheckupPage();
        carDataPage = new CarDataPage();
        carStatePage = new CarStatePage();
        carMaintenancePage = new CarMaintenancePage();
        controlTestPage = new ControlTestPage();

        fragments = new ArrayList<>();
        fragments.add(vehicleCheckupPage);
        fragments.add(carDataPage);
        fragments.add(carStatePage);
        fragments.add(carMaintenancePage);

        //是否开启 模拟测试车辆控制的页面"
        if (BuildConfig.IS_ENABLE_TEST_CAR_DEMO)
            fragments.add(controlTestPage);

        pager.setAdapter(fragmentPagerAdapter);

        pager.setOffscreenPageLimit(0);
        initDialog();
        //默认进入页面为数据页面
        pager.setCurrentItem(1);

        rg_tabs.check(R.id.page_tab2);

        permissionManager = LogicFactory.createPermissionManager(getActivity());

        //下载权限
        permissonCheckerOnStart = new PermissonCheckerOnStart();
        permissonCheckerOnStart.downloadPermision(getActivity());

        persenter = new MainPagePersenter(this);

        setListener();

        mHandlerBuy.postDelayed(new Runnable() {
            @Override
            public void run() {
                TraceUtil.stop();

            }
        }, 1000);
    }

    public void setListener() {
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rg_tabs.check(R.id.page_tab1);
                        break;
                    case 1:
                        rg_tabs.check(R.id.page_tab2);
                        break;
                    case 2:
                        rg_tabs.check(R.id.page_tab3);
                        CarStateManager.getInstance().startRefreshCarState();
                        carStatePage.onResume();
                        break;
                    case 3:
                        rg_tabs.check(R.id.page_tab4);
                        break;
                    case 4:
                        rg_tabs.check(R.id.page_tab4);
                        break;
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
                    case OBDManager.EVENT_OBD_OTA_HAS_NEWFIRMEWARE:
//                        carStatePage.showFirmwarePopu();
                        break;

                }
            }
        };
        OBDSDKListenerManager.getInstance().addSdkListener(sdkListener);
    }

    /**
     * 初始化通知的对话框
     */
    private void initDialog() {
        mAlarmTimerDialog = new TimerDialog(getActivity(), new TimerDialog.Listener() {

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
                //对话框被关闭，表示这个 通知完成了
                onNotificationFinished();
            }
        }, true, 5);
        mAlarmTimerDialog.setCancelable(false);
    }

    @Override
    public void onDestroy() {
        if (persenter != null)
            persenter.clear();
        super.onDestroy();
    }

    /**
     * 展示一个通知
     *
     * @param notification
     */
    @Override
    public void showNotification(Notification notification) {
        //是否 开始首页里的提醒对话框，开发人员每次都点很麻烦，就做了个开关
        if (!BuildConfig.IS_ENABLE_ALERM_ON_MAIN_PAGE)//
            return;
        if (mAlarmTimerDialog == null || mAlarmTimerDialog.isShowing())
            return;
        if (notification == null) return;
        //弹窗
        mAlarmTimerDialog.showAlerm(notification.getText());
        //语音播报
        TextToSpeechManager.speak(getActivity(), notification.getWord());
    }

    /**
     * 当完成一个通知后
     */
    @Override
    public void onNotificationFinished() {
        persenter.startCheckNotifications();//检查是否 还有剩余的通知要显示
    }

    /**
     * 是否正在显示一个通知
     *
     * @return
     */
    @Override
    public boolean isShowingNotification() {
        return mAlarmTimerDialog.isShowing();
    }


    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments == null ? null : fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
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
