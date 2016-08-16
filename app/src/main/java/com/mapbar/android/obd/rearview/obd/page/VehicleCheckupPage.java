package com.mapbar.android.obd.rearview.obd.page;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.common.StringUtil;
import com.mapbar.android.obd.rearview.framework.control.VoiceManager;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.CarDataManager;
import com.mapbar.android.obd.rearview.framework.manager.PhysicalManager;
import com.mapbar.android.obd.rearview.framework.widget.CircleDrawable;
import com.mapbar.android.obd.rearview.modules.checkup.VehicleCheckupPresenter;
import com.mapbar.android.obd.rearview.modules.checkup.contract.IVehicleCheckupView;
import com.mapbar.android.obd.rearview.modules.permission.PermissionAlertViewAdapter;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAdatper;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.OBDSDKListenerManager;
import com.mapbar.android.obd.rearview.obd.adapter.CheckupGridAdapter;
import com.mapbar.android.obd.rearview.obd.adapter.VehicleCheckupAdapter1;
import com.mapbar.android.obd.rearview.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.umeng.UmengConfigs;
import com.mapbar.android.obd.rearview.views.TitleBarView;
import com.mapbar.obd.Manager;
import com.mapbar.obd.PhysicalData;
import com.mapbar.obd.ReportHead;

import java.util.ArrayList;
import java.util.List;

/**
 * 体检
 * Created by THINKPAD on 2016/5/9.
 */
public class VehicleCheckupPage extends AppPage implements View.OnClickListener, IVehicleCheckupView {

    @ViewInject(R.id.test_rl)
    private ListView rl_view;

    @ViewInject(R.id.gridv)
    private GridView grid;

    @ViewInject(R.id.tv_score)
    private TextView tv_score;

    @ViewInject(R.id.tv_level)
    private TextView tv_level;

    @ViewInject(R.id.tv_progress)
    private TextView tv_progress;

    @ViewInject(R.id.view_upkeep_time)
    private ImageView view_upkeep_time;

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

    private VehicleCheckupAdapter1 recyclerAdapter;

    private CheckupGridAdapter checkupGridAdapter;
    private CircleDrawable circleDrawable;
    private VehicleCheckupPresenter presenter;
    private IPermissionAlertViewAdatper permissionAlertAbleAdapter;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            circleDrawable.setProgress(msg.what);
            tv_progress.setText("" + msg.what);
        }
    };
    /**
     * 所要播报的语音内容
     */
    private StringBuffer checkupVoiceResut;
    private boolean isCheckupFinish;
    private TitleBarView titlebarview1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_physical);
    }

    @Override
    public void initView() {
        titlebarview1 = (TitleBarView) getContentView().findViewById(R.id.titlebarview1);
        titlebarview1.setTitle(R.string.page_title_aiche_tijian);

        checkupVoiceResut = new StringBuffer();
        checkupVoiceResut.append("体检结果");
        physicalList = PhysicalManager.getInstance().getPhysicalSystem();
        recyclerAdapter = new VehicleCheckupAdapter1(MainActivity.getInstance(), physicalList);
        rl_view.setAdapter(recyclerAdapter);
        checkupGridAdapter = new CheckupGridAdapter(getContext(), physicalList);
        initPage();
        circleDrawable = new CircleDrawable(getContext());
        circleDrawable.setCricleProgressColor(getContext().getResources().getColor(R.color.upkeep_progress));
        circleDrawable.setCircleWidth(9);
        view_upkeep_time.setImageDrawable(circleDrawable);

        presenter = new VehicleCheckupPresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) presenter.checkPermission();
        if (isUmenngWorking) {
            MobclickAgentEx.onPageStart("VehicleCheckupPage");
        }
        if (isCheckupFinish) {
            initPage();
            isCheckupFinish = false;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (isUmenngWorking) {
            MobclickAgentEx.onPageEnd("VehicleCheckupPage");
        }
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.clear();
            presenter = null;
        }
        super.onDestroy();
    }

    private void initPage() {
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
                        MobclickAgentEx.onEvent(UmengConfigs.CHECKFAILED);
                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                            Log.v(LogTag.TEMP, "obdPhysicalConditionFailed -->>");
                            Log.v(LogTag.TEMP, "Object -->>" + o);
                        }
                        StringUtil.toastStringShort("请在怠速下进行车辆检测");
                        CarDataManager.getInstance().restartTrip();
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
                            Log.v(LogTag.TEMP, "getCount -->>" + physicalData.getCount());
                        }
                        recyclerAdapter.setPhysicalData((PhysicalData) o);
                        recyclerAdapter.notifyDataSetChanged();

                        break;
                    case Manager.Event.obdPhysicalCheckEnd:
                        MobclickAgentEx.onEvent(UmengConfigs.CHECKSUCC);
                        isCheckupFinish = true;
                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                            Log.v(LogTag.TEMP, "obdPhysicalCheckEnd1 -->>");
                            Log.v(LogTag.TEMP, "Object -->>" + o);
                        }
                        android.util.Log.e("wwwwwwww", "结果---->" + PhysicalManager.getInstance().getStatuses().toString());
                        rela_physicaling.post(new Runnable() {
                            @Override
                            public void run() {
                                rela_physicaling.setVisibility(View.GONE);
                                rela_no_physical.setVisibility(View.GONE);
                                line_last_result.setVisibility(View.GONE);
                                rela_result.setVisibility(View.VISIBLE);
                                grid.setAdapter(checkupGridAdapter);
                                ReportHead reportHead = PhysicalManager.getInstance().getReportHead();
                                if (reportHead != null) {
                                    int score = reportHead.getScore();

                                    checkupVoiceResut.append("分数").append(String.valueOf(score));
                                    if (score >= 0 && score <= 50) {
                                        tv_score.setTextColor(Color.RED);
                                        checkupVoiceResut.append("高危级别");
                                        tv_level.setText("高危级别");
                                        tv_level.setTextColor(Color.RED);
                                    } else if (score > 50 && score <= 70) {
                                        tv_level.setText("亚健康级别");
                                        tv_score.setTextColor(Color.YELLOW);
                                        checkupVoiceResut.append("亚健康级别");
                                        tv_level.setTextColor(Color.YELLOW);
                                    } else {
                                        tv_level.setText("健康级别");
                                        checkupVoiceResut.append("健康级别");
                                        tv_score.setTextColor(Color.GREEN);
                                        tv_level.setTextColor(Color.GREEN);
                                    }
                                    tv_score.setText(String.valueOf(score));
                                    // 日志
                                    if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                                        Log.v(LogTag.TEMP, "score -->>" + score);
                                    }
                                }
                                CarDataManager.getInstance().restartTrip();
                                //语音播报
                                VoiceManager.getInstance().sendBroadcastTTS(checkupVoiceResut.toString());
                            }
                        });

                        break;
                    case PhysicalManager.EVENT_OBD_PHYSICAL_CHECK_PROGRESS:
                        // 日志
                        if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                            Log.v(LogTag.TEMP, "EVENT_OBD_PHYSICAL_CHECK_PROGRESS -->>");
                            Log.v(LogTag.TEMP, "Object -->>" + o);
                        }

                        handler.sendEmptyMessage((int) o);
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
                if (recyclerAdapter != null) {
                    recyclerAdapter.setPhysicalData(null);
                }

                PhysicalManager.getInstance().startExam();
                MobclickAgentEx.onEvent(UmengConfigs.STARTEXAM);
                // 日志
                if (Log.isLoggable(LogTag.TEMP, Log.VERBOSE)) {
                    Log.v(LogTag.TEMP, "btn_start_physical -->>");
                }
                break;
        }
    }

    /**
     * 根节点是个framentView,可以放入 授权提醒的视图作为浮层
     *
     * @param numberOfDay
     */
    public void showPermissionAlertView_FreeTrial(boolean isExpired, int numberOfDay) {
        if (permissionAlertAbleAdapter == null)
            permissionAlertAbleAdapter = new PermissionAlertViewAdapter(this);
        permissionAlertAbleAdapter.showPermissionAlertView_FreeTrial(isExpired, numberOfDay);
    }

    public void hidePermissionAlertView_FreeTrial() {
        if (permissionAlertAbleAdapter != null)
            permissionAlertAbleAdapter.hidePermissionAlertView_FreeTrial();
    }
}
