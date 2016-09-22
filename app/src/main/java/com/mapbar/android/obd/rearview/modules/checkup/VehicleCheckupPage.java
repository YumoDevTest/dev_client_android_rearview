package com.mapbar.android.obd.rearview.modules.checkup;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.base.AppPage2;
import com.mapbar.android.obd.rearview.util.StringUtil;
import com.mapbar.android.obd.rearview.util.TimeUtils;
import com.mapbar.obd.foundation.tts.VoiceManager;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.framework.manager.CarDataManager;
import com.mapbar.android.obd.rearview.framework.manager.PhysicalManager;
import com.mapbar.android.obd.rearview.framework.preferences.PreferencesConfig;
import com.mapbar.android.obd.rearview.views.CircleDrawable;
import com.mapbar.android.obd.rearview.modules.checkup.contract.IVehicleCheckupView;
import com.mapbar.android.obd.rearview.modules.permission.PermissionAlertViewAdapter;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAdatper;
import com.mapbar.android.obd.rearview.lib.services.OBDSDKListenerManager;
import com.mapbar.obd.foundation.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.lib.umeng.UmengConfigs;
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
public class VehicleCheckupPage extends AppPage2 implements View.OnClickListener, IVehicleCheckupView {

    private ListView rl_view;
    private GridView grid;
    private TextView tv_score;
    private TextView tv_level;
    private TextView tv_progress;
    private ImageView view_upkeep_time;
    //体检中
    private RelativeLayout rela_physicaling;
    //未体检过
    private RelativeLayout rela_no_physical;
    //上次体检结果
    private LinearLayout line_last_result;
    private TextView tvLastScore;
    private TextView tvLastLevel;
    private Button btnLastCheck;
    private TextView tvLastTime;
    //本次体检结果
    private RelativeLayout rela_result;
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
    private OBDSDKListenerManager.SDKListener sdkListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentView() == null) {
            createContenttView(R.layout.layout_physical);
            initView();
        }
        return getContentView();
    }

    public void initView() {
        rl_view = (ListView)getContentView().findViewById(R.id.test_rl);
        grid = (GridView)getContentView().findViewById(R.id.gridv);
        tv_score = (TextView) getContentView().findViewById(R.id.tv_score);
        tv_level = (TextView) getContentView().findViewById(R.id.tv_level);
        tv_progress = (TextView) getContentView().findViewById(R.id.tv_progress);
        view_upkeep_time = (ImageView) getContentView().findViewById(R.id.view_upkeep_time);
        rela_physicaling = (RelativeLayout) getContentView().findViewById(R.id.rela_physicaling);
        rela_no_physical = (RelativeLayout) getContentView().findViewById(R.id.rela_no_physical);
        line_last_result = (LinearLayout) getContentView().findViewById(R.id.line_last_result);
        tvLastScore = (TextView) getContentView().findViewById(R.id.tv_last_check_score);
        tvLastLevel = (TextView) getContentView().findViewById(R.id.tv_last_check_level);
        tvLastTime = (TextView) getContentView().findViewById(R.id.tv_last_check_time);
        btnLastCheck = (Button) getContentView().findViewById(R.id.btn_last_check);
        btn_start_physical = (Button) getContentView().findViewById(R.id.btn_start_physical);
        rela_result = (RelativeLayout) getContentView().findViewById(R.id.rela_result);

        titlebarview1 = (TitleBarView) getContentView().findViewById(R.id.titlebarview1);
        titlebarview1.setTitle(R.string.page_title_aiche_tijian);

        checkupVoiceResut = new StringBuffer();
        checkupVoiceResut.append("体检结果");
        physicalList = PhysicalManager.getInstance().getPhysicalSystem();
        recyclerAdapter = new VehicleCheckupAdapter1(getActivity(), physicalList);
        rl_view.setAdapter(recyclerAdapter);
        checkupGridAdapter = new CheckupGridAdapter(getActivity(), physicalList);
        initPage();
        circleDrawable = new CircleDrawable(getActivity());
        circleDrawable.setCricleProgressColor(getActivity().getResources().getColor(R.color.upkeep_progress));
        circleDrawable.setCircleWidth(9);
        view_upkeep_time.setImageDrawable(circleDrawable);

        presenter = new VehicleCheckupPresenter(this);

        setListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) presenter.checkPermission();
        if (isCheckupFinish) {
            initPage();
            isCheckupFinish = false;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
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
            tvLastTime.setText(PreferencesConfig.PHYSICAL_CHECKEND_DATE.get());
            rela_physicaling.setVisibility(View.GONE);
            rela_no_physical.setVisibility(View.GONE);
            line_last_result.setVisibility(View.VISIBLE);
            rela_result.setVisibility(View.GONE);
            int score = head.getScore();
            tvLastScore.setText("" + score);
            if (score >= 0 && score <= 50) {
                tvLastLevel.setText("高危");
                tvLastLevel.setTextColor(Color.RED);
                tvLastScore.setTextColor(Color.RED);
            } else if (score > 50 && score <= 70) {
                tvLastLevel.setText("亚健康");
                tvLastLevel.setTextColor(Color.YELLOW);
                tvLastScore.setTextColor(Color.YELLOW);
            } else {
                tvLastLevel.setText("健康");
                tvLastLevel.setTextColor(Color.GREEN);
                tvLastScore.setTextColor(Color.GREEN);
            }
        } else {
            rela_physicaling.setVisibility(View.GONE);
            rela_no_physical.setVisibility(View.VISIBLE);
            line_last_result.setVisibility(View.GONE);
            rela_result.setVisibility(View.GONE);
        }
    }

    public void setListener() {
        sdkListener = new OBDSDKListenerManager.SDKListener() {
            @Override
            public void onEvent(int event, Object o) {
                super.onEvent(event, o);

                switch (event) {
                    case Manager.Event.obdPhysicalConditionFailed:
                        MobclickAgentEx.onEvent(getActivity(), UmengConfigs.CHECKFAILED);
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
                        MobclickAgentEx.onEvent(getActivity(), UmengConfigs.CHECKSUCC);
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
                                    PreferencesConfig.PHYSICAL_CHECKEND_DATE.set(TimeUtils.getmDateYYYYMMDD2(getContext(), System.currentTimeMillis()));
                                    int score = reportHead.getScore();
                                    if (checkupVoiceResut.length() > 4) {
                                        checkupVoiceResut.delete(4, checkupVoiceResut.length() - 1);
                                    }
                                    checkupVoiceResut.append("分数").append(String.valueOf(score));
                                    if (score >= 0 && score <= 50) {
                                        tv_score.setTextColor(Color.RED);
                                        checkupVoiceResut.append("高危级别");
                                        tv_level.setText("高危");
                                        tv_level.setTextColor(Color.RED);
                                    } else if (score > 50 && score <= 70) {
                                        tv_level.setText("亚健康");
                                        tv_score.setTextColor(Color.YELLOW);
                                        checkupVoiceResut.append("亚健康级别");
                                        tv_level.setTextColor(Color.YELLOW);
                                    } else {
                                        tv_level.setText("健康");
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
                                VoiceManager.getInstance().sendBroadcastTTS(getActivity(), checkupVoiceResut.toString());
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
        OBDSDKListenerManager.getInstance().addSdkListener(sdkListener);
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
                MobclickAgentEx.onEvent(getActivity(), UmengConfigs.STARTEXAM);
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
