package com.mapbar.android.obd.rearview.modules.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.base.MyBaseFragment;
import com.mapbar.android.obd.rearview.modules.vin.VinManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置 页
 * Created by zhangyunfei on 16/7/25.
 */
public class SettingFragment extends MyBaseFragment {
    private View rootView;
    private ListView listView1;
    private List<MyMenuItem> datasource;
    private QuickAdapter<MyMenuItem> adapter;
    private static final int MY_MENU_ITEM_MODIFY_VIN = 1;
    private static final int MY_MENU_ITEM_MODIFY_PHONE_NUMER = 2;
    private static final int MY_MENU_ITEM_ABOUT_US = 3;
    private boolean isGetVinFromCar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.setting_fragment, null);
            VinManager vinManager = new VinManager();
            isGetVinFromCar = TextUtils.isEmpty(vinManager.getVinFromCar());
            listView1 = (ListView) rootView.findViewById(R.id.listview1);

            datasource = new ArrayList<>();
            datasource.add(new MyMenuItem(MY_MENU_ITEM_MODIFY_VIN, "修改VIN", R.drawable.ic_barcode_selector));
            datasource.add(new MyMenuItem(MY_MENU_ITEM_MODIFY_PHONE_NUMER, "修改手机号或车型", R.drawable.ic_barcode_selector));
            datasource.add(new MyMenuItem(MY_MENU_ITEM_ABOUT_US, "关于我们", 0));


            adapter = new QuickAdapter<MyMenuItem>(getActivity(), R.layout.setting_fragment_item, datasource) {
                @Override
                protected void convert(BaseAdapterHelper helper, MyMenuItem item) {
                    if(!isGetVinFromCar && item.index==MY_MENU_ITEM_MODIFY_VIN){
                        helper.setAlpha(R.id.text, 0.2f);
                        helper.setAlpha(R.id.more, 0.2f);
                        helper.setAlpha(R.id.img, 0.2f);
                    }
                    helper.setText(R.id.text, item.text);
                    helper.setImageResource(R.id.img, item.imageResouceID);

                }
            };
            listView1.setAdapter(adapter);
            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MyMenuItem myMenuItem = adapter.getItem(position);
                    if (myMenuItem == null) return;
                    if (myMenuItem.index == MY_MENU_ITEM_MODIFY_VIN && isGetVinFromCar) {
                        //跳转到修改VIN界面
                        startActivity(new Intent(getActivity(), ChangeVINActivity.class));
                    } else if (myMenuItem.index == MY_MENU_ITEM_MODIFY_PHONE_NUMER) {
                        //跳转到 修改手机号
                        startActivity(new Intent(getActivity(), ChangePhoneActivity.class));
                    } else if (myMenuItem.index == MY_MENU_ITEM_ABOUT_US) {
                        //跳转到关于页
                        getParentActivity().showFragment(new AboutFragment(), true);
                    }
                }
            });

        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getParentActivity().getTitlebarview() != null) {
            getParentActivity().getTitlebarview().setTitle(R.string.page_title_setting);
            getParentActivity().getTitlebarview().setEnableBackButton(true);
        }
    }

    /**
     * 描述 菜单列表内容
     */
    private static class MyMenuItem {
        public int index;
        public String text;
        public int imageResouceID;

        public MyMenuItem(int index, String text, int resouceID) {
            this.index = index;
            this.text = text;
            this.imageResouceID = resouceID;
        }
    }
}
