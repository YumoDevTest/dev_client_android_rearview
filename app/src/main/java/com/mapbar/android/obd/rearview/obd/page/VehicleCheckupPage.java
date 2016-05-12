package com.mapbar.android.obd.rearview.obd.page;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.mapbar.android.obd.rearview.MainActivity;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.obd.adapter.CheckupGridAdapter;
import com.mapbar.android.obd.rearview.obd.adapter.VehicleCheckupAdapter;
import com.mapbar.obd.Manager;
import com.mapbar.obd.Physical;
import com.mapbar.obd.PhysicalData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by THINKPAD on 2016/5/9.
 */
public class VehicleCheckupPage extends AppPage implements View.OnClickListener {

    @ViewInject(R.id.test_rl)
    private RecyclerView rl_view;

    @ViewInject(R.id.gridv)
    private GridView grid;

//    @ViewInject(R.id.view_upkeep)
//    private ImageView view_upkeep;

    @ViewInject(R.id.btn_start_physical)
    private Button btn_start_physical;

    private List<PhysicalData> physicalList = new ArrayList<PhysicalData>();

    private VehicleCheckupAdapter recyclerAdapter;

    private CheckupGridAdapter checkupGridAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.getInstance());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rl_view.setLayoutManager(layoutManager);
        physicalList = Physical.getInstance().getPhysicalSystem();
        recyclerAdapter = new VehicleCheckupAdapter(MainActivity.getInstance(), physicalList);
        rl_view.setAdapter(recyclerAdapter);
        physicalList = Physical.getInstance().getPhysicalSystem();
        checkupGridAdapter = new CheckupGridAdapter(MainActivity.getInstance(), physicalList);
        grid.setAdapter(checkupGridAdapter);
//        CircleDrawable circleDrawable = new CircleDrawable(getContext());
//        circleDrawable.setProgress(75);
//        circleDrawable.setCricleProgressColor(getContext().getResources().getColor(R.color.upkeep_progress));
//        circleDrawable.setCircleWidth(15);
//        view_upkeep.setImageDrawable(circleDrawable);
    }

    @Override
    public void setListener() {

        btn_start_physical.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_physical:
                int state = Manager.getInstance().getState();
                //手动停止行程时满足体检条件
                if (state != Manager.State.tripStoppedManually) {// FIXME: 2015/9/23
                    return;
                }
                break;
        }
    }
}
