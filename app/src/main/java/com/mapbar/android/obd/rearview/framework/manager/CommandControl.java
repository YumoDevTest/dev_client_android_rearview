package com.mapbar.android.obd.rearview.framework.manager;

import android.content.Context;

import com.mapbar.android.obd.rearview.util.DecFormatUtil;
import com.mapbar.android.obd.rearview.util.TimeUtils;
import com.mapbar.obd.foundation.tts.VoiceManager;
import com.mapbar.obd.foundation.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.lib.umeng.UmengConfigs;
import com.mapbar.android.obd.rearview.lib.config.Constants;
import com.mapbar.mapdal.DateTime;
import com.mapbar.obd.MaintenanceInfo;
import com.mapbar.obd.MaintenanceResult;
import com.mapbar.obd.MaintenanceState;
import com.mapbar.obd.Manager;
import com.mapbar.obd.RealTimeData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 指令控制执行类
 */
public class CommandControl {
    private static CommandControl mCommandControl;
    public StringBuffer maintenanceVoice = new StringBuffer();
    private int[] commands = new int[]{201001, 201000, 202000, 202001, 207001, 207000};
    private String[] commadStrs = new String[]{"AT@STS020101", "AT@STS020102", "AT@STS010101", "AT@STS010102", "AT@STS010501", "AT@STS010502"};
    private String[] commadNames = new String[]{"开锁", "落锁", "降窗", "升窗", "开天窗", "关天窗"};
    private Context context;
    private short year;
    private short month;
    private short day;
    private long nextDay;
    private long nextUpkeepDate;
    private VoiceManager voiceManager;

    private CommandControl() {
        voiceManager = VoiceManager.getInstance();
    }

    public static CommandControl getInstance() {
        if (mCommandControl == null) {
            mCommandControl = new CommandControl();
        }
        return mCommandControl;
    }

    /**
     * 执行指令
     *
     * @param command 指令
     */
    public void executeCommand(Context context, final int command) {
        this.context = context;
        if (command >= 200000) {//控制类指令
            executeCommand2(command);
        } else {//非控制类指令
            MobclickAgentEx.onEvent(context, UmengConfigs.VOICE_REALTIMEDATA);
            final RealTimeData realTimeData = Manager.getInstance().getRealTimeData();
            switch (command) {
                case 102000://车速

                    voiceManager.sendBroadcastTTS(context,realTimeData.speed + "千米每小时");
                    break;
                case 102001://转速
                    voiceManager.sendBroadcastTTS(context,realTimeData.rpm + "转每分钟");
                    break;
                case 102002://电压
                    voiceManager.sendBroadcastTTS(context, DecFormatUtil.format2dot1(realTimeData.voltage) + "伏");
                    break;
                case 102003://水温
                    voiceManager.sendBroadcastTTS(context,realTimeData.engineCoolantTemperature + "摄氏度");
                    break;
                case 102004://瞬时油耗
                    String gasConsum = realTimeData.speed < 10 ? DecFormatUtil.format2dot1(realTimeData.gasConsumInLPerHour) + "升每小时" : DecFormatUtil.format2dot1(realTimeData.gasConsum) + "升每百公里";
                    voiceManager.sendBroadcastTTS(context,gasConsum);
                    break;
                case 102005://平均油耗
                    voiceManager.sendBroadcastTTS(context,DecFormatUtil.format2dot1(realTimeData.averageGasConsum) + "升每百公里");
                    break;
                case 102006://本次时间
                    voiceManager.sendBroadcastTTS(context,TimeUtils.parseTime(realTimeData.tripTime) + "小时");
                    break;
                case 102007://本次行程
                    voiceManager.sendBroadcastTTS(context,DecFormatUtil.format2dot1(realTimeData.tripLength / 1000) + "公里");
                    break;
                case 102008://本次花费
                    voiceManager.sendBroadcastTTS(context,DecFormatUtil.format2dot1(realTimeData.driveCost) + "元");
                    break;
//                case 103000://开始体检
//                    Constants.ISRECEIVER = 0;
//                    VoiceManager.getInstance().sendBroadcastTTS("体检开始请稍等");
//                    ReportHead head = PhysicalManager.getInstance().getReportHead();
//                    if (head != null) {
//                        StringBuffer checkupVoiceResut = new StringBuffer();
//                        int score = head.getScore();
//                        checkupVoiceResut.append("体检结果");
//                        checkupVoiceResut.append("分数" + String.valueOf(score));
//                        if (score >= 0 && score <= 50) {
//                            checkupVoiceResut.append("高危级别");
//                        } else if (score > 50 && score <= 70) {
//                            checkupVoiceResut.append("亚健康级别");
//                        } else {
//                            checkupVoiceResut.append("健康级别");
//                        }
//                        VoiceManager.getInstance().sendBroadcastTTS("体检开始请稍等");
//                    } else {
//                    Log.e("VoiceReceiver", "执行指令:::::");
//                        Intent startIntent = new Intent();
//                        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//必须加上
//                        ComponentName cName = new ComponentName("com.mapbar.android.obd.rearview", "com.mapbar.android.obd.rearview.obd.MainActivity");
//                        startIntent.setComponent(cName);
//                        context.startActivity(startIntent);

//                    }
//                    break;
                case 104000://播报保养
                    getLocalSchemeCache();
                    break;

            }
        }
    }

    /**
     * 获取本地保养信息
     */
    private void getLocalSchemeCache() {
        // 获取本地缓存保养数据
        MaintenanceInfo localSchemeCache = Manager.getInstance().queryMaintenanceInfoByLocalSchemeCache();
        switch (localSchemeCache.errCode) {
            case MaintenanceResult.noSchemeFound:
                // FIXME: 2016/7/17   本地没有保养信息,网络获取保养信息

                break;
            case MaintenanceResult.ok:
                if (maintenanceVoice.length() > 0) {
                    maintenanceVoice.delete(0, maintenanceVoice.length() - 1);
                }
                DateTime mDate = localSchemeCache.state.getNextMaintenanceDate();
                year = mDate.year;
                month = mDate.month;
                day = mDate.day;
                maintenanceVoice.append("距离下次保养里程还有");
                String data2 = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d = null;
                try {
                    d = sdf.parse(data2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                nextUpkeepDate = c.getTimeInMillis();
                Date nowDate = new Date();
                long time = nowDate.getTime();
                if (time < nextUpkeepDate) {
                    nextDay = (nextUpkeepDate - time) / 86400000;
                }
                int tag = localSchemeCache.state.getTag();
                switch (tag) {
                    case MaintenanceState.Tag.invalid:
                        break;
                    case MaintenanceState.Tag.nextMaintenanceDateEstimatedByMileage:
                        // 表示未过保，并且下次保养日期是用里程估算得到的
                        maintenanceVoice.append(String.valueOf(localSchemeCache.state.getMileageToMaintenance() / 1000));
                        maintenanceVoice.append("公里,");
                    case MaintenanceState.Tag.nextMaintenanceDateEstimatedByTime:
                        maintenanceVoice.append("距离下次保养时间还有").append(nextDay).append("天");
                        VoiceManager.getInstance().sendBroadcastTTS(context, maintenanceVoice.toString());
                        break;
                    default:
                        break;
                }
                break;
            case MaintenanceResult.carGenerationNotSpecified:
                VoiceManager.getInstance().sendBroadcastTTS(context, "尚未指定车型");
                break;

            default:
                VoiceManager.getInstance().sendBroadcastTTS(context, "网络请求失败");
                break;
        }
    }

    /**
     * 执行控制指令
     * 应用场景：执行开关窗，开关车门，开关后备箱等。
     *
     * @param command
     */
    private void executeCommand2(final int command) {
        uMeng(command);
        ArrayList<Integer> commandList = new ArrayList<>();
        for (int command1 : commands) {
            commandList.add(command1);
        }
        int index = commandList.indexOf(command);//对应的可执行指令数组索引
        if (index != -1) {
            String commandStr = commadStrs[index];
            boolean obdOtaSpecialSurpport = Manager.getInstance().getObdOtaSpecialSurpport(commandStr);
            if (obdOtaSpecialSurpport) {
                int exuResult = Manager.getInstance().exuSpecialCarAction(commandStr);
                boolean flag = exuResult == 1;
                String speekStr = "";
                if (command == 201001) {
                    speekStr = flag ? "已解锁" : "解锁失败";
                } else if (command == 201000) {
                    speekStr = flag ? "已落锁" : "落锁失败";
                } else if (command == 202000) {
                    speekStr = flag ? "已开窗" : "开窗失败";
                } else if (command == 202001) {
                    speekStr = flag ? "已关窗" : "关窗失败";
                } else if (command == 207001) {
                    speekStr = flag ? "天窗已开启" : "开启天窗失败";
                } else if (command == 207000) {
                    speekStr = flag ? "天窗已关闭" : "关闭天窗失败";
                }
                Manager.getInstance().speak(speekStr);
            } else {
                Manager.getInstance().speak("您的爱车暂不支持该功能");
            }
        }
    }

    /**
     * 友盟统计
     *
     * @param command
     */
    private void uMeng(int command) {
        switch (command) {
            case 201001:
                MobclickAgentEx.onEvent(context, UmengConfigs.VOICE_LOCK1);
                break;
            case 201000:
                MobclickAgentEx.onEvent(context, UmengConfigs.VOICE_LOCK0);
                break;
            case 202000:
                MobclickAgentEx.onEvent(context, UmengConfigs.VOICE_WINDOW0);
                break;
            case 202001:
                MobclickAgentEx.onEvent(context, UmengConfigs.VOICE_WINDOW1);
                break;
            case 207001:
                MobclickAgentEx.onEvent(context, UmengConfigs.VOICE_SKY_LIGHT1);
                break;
            case 207000:
                MobclickAgentEx.onEvent(context, UmengConfigs.VOICE_SKY_LIGHT0);
                break;


        }
    }

    public int[] getCommands() {
        return commands;
    }

    public String[] getCommadStrs() {
        return commadStrs;
    }

    public String[] getCommadNames() {
        return commadNames;
    }
}
