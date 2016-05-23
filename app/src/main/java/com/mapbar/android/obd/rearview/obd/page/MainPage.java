package com.mapbar.android.obd.rearview.obd.page;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.widget.MyViewPage;
import com.mapbar.android.obd.rearview.framework.widget.TitleBar;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.obd.Manager;

import java.util.ArrayList;

import static com.mapbar.android.obd.rearview.framework.control.PageManager.ManagerHolder.pageManager;

public class MainPage extends AppPage {

    private TitleBar titleBar;
    @ViewInject(R.id.pager_main)
    private MyViewPage pager;
    @ViewInject(R.id.rg_tabs)
    private RadioGroup rg_tabs;

    private String[] titles;
    private CarDataPage carDataPage;
    private CarStatePage carStatePage;
    private CarMaintenancePage carMaintenancePage;
    private VehicleCheckupPage vehicleCheckupPage;
    private ControlTestPage controlTestPage;
    private ArrayList<View> views;
    private AppPage currentPage;
    private PagerAdapter adapter = new PagerAdapter() {

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(views.get(position), 0);
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
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
        views = new ArrayList<>();
        vehicleCheckupPage = (VehicleCheckupPage) pageManager.createPage(VehicleCheckupPage.class);
        vehicleCheckupPage.initByCustom(R.layout.layout_physical);
        carDataPage = (CarDataPage) pageManager.createPage(CarDataPage.class);
        carDataPage.initByCustom(R.layout.page_car_data);
        carStatePage = (CarStatePage) pageManager.createPage(CarStatePage.class);
        carStatePage.initByCustom(R.layout.page_car_state);
        carMaintenancePage = (CarMaintenancePage) pageManager.createPage(CarMaintenancePage.class);
        carMaintenancePage.initByCustom(R.layout.page_upkeep);
//        controlTestPage = (ControlTestPage) pageManager.createPage(ControlTestPage.class);
//        controlTestPage.initByCustom(R.layout.page_control_test);
        controlTestPage = (ControlTestPage) pageManager.createPage(ControlTestPage.class);
        controlTestPage.initByCustom(R.layout.page_control_test);
        views.add(vehicleCheckupPage.getContentView());
        views.add(carDataPage.getContentView());
        views.add(carStatePage.getContentView());
        views.add(carMaintenancePage.getContentView());
//        views.add(controlTestPage.getContentView());
        pager.setOffscreenPageLimit(3);
        views.add(controlTestPage.getContentView());
        pager.setAdapter(adapter);
        currentPage = vehicleCheckupPage;
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
                }
                currentPage.onResume();
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
                    case Manager.Event.obdPhysicalCheckStart:
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

                        break;
                }


            }
        };
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);

    }

}
