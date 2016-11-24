package com.mapbar.android.obd.rearview.modules.common;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.mapbar.android.obd.rearview.BuildConfig;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.manager.CarStateManager;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.android.obd.rearview.lib.base.AppPage2;
import com.mapbar.android.obd.rearview.lib.notify.Notification;
import com.mapbar.android.obd.rearview.lib.services.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.modules.cardata.CarDataPage;
import com.mapbar.android.obd.rearview.modules.carstate.CarStatePage;
import com.mapbar.android.obd.rearview.modules.checkup.VehicleCheckupPage;
import com.mapbar.android.obd.rearview.modules.common.contract.IMainPageView;
import com.mapbar.android.obd.rearview.modules.controltest.ControlTestPage;
import com.mapbar.android.obd.rearview.modules.maintenance.CarMaintenancePage;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissonCheckerOnStart;
import com.mapbar.android.obd.rearview.util.TraceUtil;
import com.mapbar.android.obd.rearview.views.TimerDialog;
import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.foundation.tts.TextToSpeechManager;

import java.util.ArrayList;
import java.util.List;

import static com.mapbar.android.obd.rearview.util.LayoutUtils.getScreenArea;


/**
 * 首页
 * 包含1个viewpager,其下有4个子fragment 页
 */
public class MainPage extends AppPage2 implements IMainPageView {
    private static final String TAG = MainPage.class.getSimpleName();

    private ViewPager pager;
    private RadioGroup rg_tabs;
    private String[] titles;
    private CarDataPage carDataPage;
    private CarStatePage carStatePage;
    private CarMaintenancePage carMaintenancePage;
    private VehicleCheckupPage vehicleCheckupPage;
    private ControlTestPage controlTestPage;
    private List<Fragment> fragments;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, String.format("## %s onCreateView", getThisClassName()));
        if (getContentView() == null) {
            createContenttView(R.layout.page_main);
            initView();
        }
        return getContentView();
    }

    public void initView() {
        titles = getResources().getStringArray(R.array.page_titles);
        pager = (ViewPager) getContentView().findViewById(R.id.pager_main);
        rg_tabs = (RadioGroup) getContentView().findViewById(R.id.rg_tabs);

        vehicleCheckupPage = new VehicleCheckupPage();
        carDataPage = new CarDataPage();
        carStatePage = new CarStatePage();
        carMaintenancePage = new CarMaintenancePage();
        controlTestPage = new ControlTestPage();

        if (fragments == null) {
            fragments = new ArrayList<>();
            fragments.add(vehicleCheckupPage);
            fragments.add(carDataPage);
            fragments.add(carStatePage);
            fragments.add(carMaintenancePage);
            //是否开启 模拟测试车辆控制的页面"
            if (BuildConfig.IS_ENABLE_TEST_CAR_DEMO)
                fragments.add(controlTestPage);
        }
        fragmentPagerAdapter = new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager()
                , fragments);

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

        mHandlerBuy.postDelayed(new Runnable() {
            @Override
            public void run() {
                TraceUtil.stop();

            }
        }, 1000);
    }

    private void attachListener() {
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

    private void deattachListener() {
        OBDSDKListenerManager.getInstance().removeSdkListener(sdkListener);
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
    public void onResume() {
        super.onResume();
        attachListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        deattachListener();
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
        setDialogLocation();
        //弹窗
        mAlarmTimerDialog.showAlerm(notification.getText());
        //语音播报
        TextToSpeechManager.speak(getActivity(), notification.getWord());
    }

    private void setDialogLocation() {
        int[] ints = new int[2];
        //获取控件所在位置
        getContentView().getLocationOnScreen(ints);
        Window dialogWindow = mAlarmTimerDialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        int removeW = getScreenArea().width() / 2 - getContentView().getWidth() / 2 - ints[0];
        layoutParams.x = -removeW;
        dialogWindow.setAttributes(layoutParams);
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


    private static class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> lst) {
            super(fm);
            fragmentList = new ArrayList<>();
            fragmentList.addAll(lst);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList == null ? null : fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }

}
