package com.mapbar.android.obd.rearview.obd.page;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.manager.CarStateManager;
import com.mapbar.android.obd.rearview.framework.manager.OBDManager;
import com.mapbar.android.obd.rearview.framework.widget.TitleBar;
import com.mapbar.android.obd.rearview.lib.notify.Notification;
import com.mapbar.android.obd.rearview.lib.tts.TextToSpeechManager;
import com.mapbar.android.obd.rearview.modules.cardata.CarDataPage;
import com.mapbar.android.obd.rearview.modules.common.LogicFactory;
import com.mapbar.android.obd.rearview.modules.common.MainPagePersenter;
import com.mapbar.android.obd.rearview.modules.common.contract.IMainPageView;
import com.mapbar.android.obd.rearview.modules.permission.PermissionManager;
import com.mapbar.android.obd.rearview.modules.permission.PermissonCheckerOnStart;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.obd.widget.TimerDialog;

import java.util.ArrayList;

import static com.mapbar.android.obd.rearview.framework.control.PageManager.ManagerHolder.pageManager;


/**
 * 首页
 * 包含1个viewpager,其下有4个子fragment 页
 */
public class MainPage extends AppPage implements IMainPageView {
    private static final String TAG = MainPage.class.getSimpleName();

    public static TitleBar title;

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
    private MainPagePersenter persenter;

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
        fragments.add(carMaintenancePage);
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

        persenter = new MainPagePersenter(this);
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
                    case OBDManager.EVENT_OBD_OTA_HAS_NEWFIRMEWARE:
                        carStatePage.showFirmwarePopu();
                        break;

                }
            }
        };
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);
    }

    /**
     * 初始化通知的对话框
     */
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
        if (mAlarmTimerDialog == null || mAlarmTimerDialog.isShowing())
            return;
        if (notification == null) return;
        //弹窗
        mAlarmTimerDialog.showAlerm(notification.getText());
        //语音播报
        TextToSpeechManager.speak(notification.getWord());
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
