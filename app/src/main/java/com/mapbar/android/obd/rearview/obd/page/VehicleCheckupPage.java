package com.mapbar.android.obd.rearview.obd.page;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mapbar.android.obd.rearview.MainActivity;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.model.AppPage;
import com.mapbar.android.obd.rearview.obd.adapter.VehicleCheckupAdapter;
import com.mapbar.obd.Physical;
import com.mapbar.obd.PhysicalData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by THINKPAD on 2016/5/9.
 */
public class VehicleCheckupPage extends AppPage {

    @ViewInject(R.id.test_rl)
    private RecyclerView rl_view;

    private List<PhysicalData> physicalList = new ArrayList<PhysicalData>();

    private VehicleCheckupAdapter recyclerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_physicaling);
    }

    @Override
    public void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.getInstance());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rl_view.setLayoutManager(layoutManager);
        physicalList = Physical.getInstance().getPhysicalSystem();
        recyclerAdapter = new VehicleCheckupAdapter(MainActivity.getInstance(), physicalList);
        rl_view.setAdapter(recyclerAdapter);
    }

    @Override
    public void setListener() {

    }
}
