package com.mapbar.android.obd.rearview.obd.page;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.common.StringUtil;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.PhysicalManager;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.obd.adapter.CheckupGridAdapter;
import com.mapbar.android.obd.rearview.obd.adapter.VehicleCheckupAdapter;
import com.mapbar.obd.Manager;
import com.mapbar.obd.Physical;
import com.mapbar.obd.PhysicalData;
import com.mapbar.obd.ReportHead;

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

    @ViewInject(R.id.tv_score)
    private TextView tv_score;

    @ViewInject(R.id.tv_level)
    private TextView tv_level;

    //体检中
    @ViewInject(R.id.rela_physicaling)
    private RelativeLayout rela_physicaling;

    //未体检过
    @ViewInject(R.id.rela_no_physical)
    private RelativeLayout rela_no_physical;

    //上次体检结果
    @ViewInject(R.id.line_last_result)
    private LinearLayout line_last_result;
    @ViewInject(R.id.tv_last_check_score)
    private TextView tvLastScore;
    @ViewInject(R.id.tv_last_check_level)
    private TextView tvLastLevel;
    @ViewInject(R.id.btn_last_check)
    private Button btnLastCheck;

    //本次体检结果
    @ViewInject(R.id.rela_result)
    private RelativeLayout rela_result;

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

//        CircleDrawable circleDrawable = new CircleDrawable(getContext());
//        circleDrawable.setProgress(75);
//        circleDrawable.setCricleProgressColor(getContext().getResources().getColor(R.color.upkeep_progress));
//        circleDrawable.setCircleWidth(15);
//        view_upkeep.setImageDrawable(circleDrawable);
    }

    @Override
    public void onResume() {
        super.onResume();
        ReportHead head = PhysicalManager.getInstance().getReportHead();
        if (head != null) {
            rela_physicaling.setVisibility(View.GONE);
            rela_no_physical.setVisibility(View.GONE);
            line_last_result.setVisibility(View.VISIBLE);
            rela_result.setVisibility(View.GONE);
            int score = head.getScore();
            tvLastScore.setText("" + score);
            if (score >= 0 && score <= 50) {
                tvLastLevel.setText("高危级别");
                tvLastScore.setTextColor(Color.RED);
            } else if (score > 50 && score <= 70) {
                tvLastLevel.setText("亚健康级别");
                tvLastScore.setTextColor(Color.YELLOW);
            } else {
                tvLastLevel.setText("健康级别");
                tvLastScore.setTextColor(Color.GREEN);
            }
        } else {
            rela_physicaling.setVisibility(View.GONE);
            rela_no_physical.setVisibility(View.VISIBLE);
            line_last_result.setVisibility(View.GONE);
            rela_result.setVisibility(View.GONE);
        }
    }

    @Override
    public void setListener() {
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                super.onEvent(event, o);
                switch (event) {
                    case Manager.Event.obdPhysicalConditionFailed:
                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                            Log.v(LogTag.TEMP, "obdPhysicalConditionFailed -->>");
                            Log.v(LogTag.TEMP, "Object -->>" + o);
                        }
                        StringUtil.toastStringShort("体检失败!");
                        break;
                    case Manager.Event.obdPhysicalCheckStart:

                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                            Log.v(LogTag.TEMP, "obdPhysicalCheckStart -->>");
                            Log.v(LogTag.TEMP, "o -->>" + o);
                        }
                        rela_physicaling.setVisibility(View.VISIBLE);
                        rela_no_physical.setVisibility(View.GONE);
                        line_last_result.setVisibility(View.GONE);
                        rela_result.setVisibility(View.GONE);

                        break;
                    case Manager.Event.obdPhysicalCheckResult:
                        PhysicalData physicalData = (PhysicalData) o;
                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                            Log.v(LogTag.TEMP, "obdPhysicalCheckResult -->>");
                            Log.v(LogTag.TEMP, "getId -->>" + physicalData.getId());
                            Log.v(LogTag.TEMP, "getStatus -->>" + physicalData.getStatus());
                            Log.v(LogTag.TEMP, "getProcessed -->>" + physicalData.getProcessed());
                            Log.v(LogTag.TEMP, "getName -->>" + physicalData.getName());
                        }
                        recyclerAdapter.setPhysicalData((PhysicalData) o);
                        recyclerAdapter.notifyDataSetChanged();
                        break;
                    case Manager.Event.obdPhysicalCheckEnd:
                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                            Log.v(LogTag.TEMP, "obdPhysicalCheckEnd -->>");
                            Log.v(LogTag.TEMP, "Object -->>" + o);
                        }
                        rela_physicaling.post(new Runnable() {
                            @Override
                            public void run() {
                                rela_physicaling.setVisibility(View.GONE);
                                rela_no_physical.setVisibility(View.GONE);
                                line_last_result.setVisibility(View.GONE);
                                rela_result.setVisibility(View.VISIBLE);
                                grid.setAdapter(checkupGridAdapter);
                                ArrayList<ReportHead> physicalReportByMonth = Physical
                                        .getInstance().getPhysicalReportByMonth(1970, 01);
                                if (physicalReportByMonth != null) {
                                    int score = physicalReportByMonth.get(0).getScore();
                                    tv_score.setText(String.valueOf(score));

                                    if (score >= 0 && score <= 50) {
                                        tv_level.setText("高危级别");
                                        tv_level.setTextColor(Color.RED);
                                    } else if (score > 50 && score <= 70) {
                                        tv_level.setText("亚健康级别");
                                        tv_level.setTextColor(Color.YELLOW);
                                    } else {
                                        tv_level.setText("健康级别");
                                        tv_level.setTextColor(Color.GREEN);
                                    }
                                    // 日志
                                    if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                                        Log.v(LogTag.TEMP, "score -->>" + score);
                                        Log.v(LogTag.TEMP, "size -->>" + physicalReportByMonth.size());
                                    }
                                }
                            }
                        });

                        break;
                    case PhysicalManager.EVENT_OBD_PHYSICAL_CHECK_PROGRESS:
                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                            Log.v(LogTag.TEMP, "EVENT_OBD_PHYSICAL_CHECK_PROGRESS -->>");
                            Log.v(LogTag.TEMP, "Object -->>" + o);
                        }
                        break;
                }
            }
        };
        OBDSDKListenerManager.getInstance().setSdkListener(sdkListener);
        btn_start_physical.setOnClickListener(this);
        btnLastCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_physical:
            case R.id.btn_last_check:
                Manager.getInstance().stopTrip(false);
                PhysicalManager.getInstance().startExam();
                // 日志
                if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                    Log.v(LogTag.TEMP, "btn_start_physical -->>");
                }
                break;
        }
    }
}
