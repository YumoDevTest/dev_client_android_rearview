package com.mapbar.android.obd.rearview.framework.control;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.mapbar.android.obd.rearview.framework.manager.PhysicalManager;
import com.mapbar.android.obd.rearview.umeng.MobclickAgentEx;
import com.mapbar.android.obd.rearview.umeng.UmengConfigs;
import com.mapbar.obd.Manager;
import com.mapbar.obd.RealTimeData;
import com.mapbar.obd.ReportHead;

import java.util.ArrayList;

import static com.mapbar.android.obd.rearview.framework.control.VoiceManager.VoiceManagerHolder.voiceManager;

/**
 * 指令控制执行类
 */
public class CommandControl {
    private static CommandControl mCommandControl;
    private int[] commands = new int[]{201001, 201000, 202000, 202001, 207001, 207000};
    private String[] commadStrs = new String[]{"AT@STS020101", "AT@STS020102", "AT@STS010101", "AT@STS010102", "AT@STS010501", "AT@STS010502"};
    private String[] commadNames = new String[]{"开锁", "落锁", "降窗", "升窗", "开天窗", "关天窗"};
    private Context context;

    private CommandControl() {
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
            MobclickAgentEx.onEvent(UmengConfigs.VOICE_REALTIMEDATA);
            final RealTimeData realTimeData = Manager.getInstance().getRealTimeData();
            switch (command) {
                case 102000://车速
                    voiceManager.sendBroadcastTTS(String.valueOf(realTimeData.speed));
                    break;
                case 102001://转速
                    voiceManager.sendBroadcastTTS(String.valueOf(realTimeData.rpm));
                    break;
                case 102002://电压
                    voiceManager.sendBroadcastTTS(String.valueOf(realTimeData.voltage));
                    break;
                case 102003://水温
                    voiceManager.sendBroadcastTTS(String.valueOf(realTimeData.engineCoolantTemperature));
                    break;
                case 102004://瞬时油耗
                    voiceManager.sendBroadcastTTS(String.valueOf(realTimeData.gasConsum));
                    break;
                case 102005://平均油耗
                    voiceManager.sendBroadcastTTS(String.valueOf(realTimeData.averageGasConsum));
                    break;
                case 102006://本次时间
                    voiceManager.sendBroadcastTTS(String.valueOf(realTimeData.tripTime));
                    break;
                case 102007://本次行程
                    voiceManager.sendBroadcastTTS(String.valueOf(realTimeData.tripLength));
                    break;
                case 102008://本次花费
                    voiceManager.sendBroadcastTTS(String.valueOf(realTimeData.driveCost));
                    break;
                case 103000://开始体检
                    VoiceManager.getInstance().sendBroadcastTTS("体检开始请稍等");
                    ReportHead head = PhysicalManager.getInstance().getReportHead();
                    if (head != null) {
                        StringBuffer checkupVoiceResut = new StringBuffer();
                        int score = head.getScore();
                        checkupVoiceResut.append("体检结果");
                        checkupVoiceResut.append("分数" + String.valueOf(score));
                        if (score >= 0 && score <= 50) {
                            checkupVoiceResut.append("高危级别");
                        } else if (score > 50 && score <= 70) {
                            checkupVoiceResut.append("亚健康级别");
                        } else {
                            checkupVoiceResut.append("健康级别");
                        }
                        VoiceManager.getInstance().sendBroadcastTTS("体检开始请稍等");
                    } else {
                        Intent startIntent = new Intent();
                        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//必须加上
                        ComponentName cName = new ComponentName("com.mapbar.android.obd.rearview", "com.mapbar.android.obd.rearview.obd.MainActivity");
                        startIntent.setComponent(cName);
                        context.startActivity(startIntent);
                        PhysicalManager.getInstance().startExam();
                    }
                    break;
                case 104000://播报保养
                    break;

            }
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
            Manager.getInstance().exuSpecialCarAction(commandStr);
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
                MobclickAgentEx.onEvent(UmengConfigs.VOICE_LOCK1);
                break;
            case 201000:
                MobclickAgentEx.onEvent(UmengConfigs.VOICE_LOCK0);
                break;
            case 202000:
                MobclickAgentEx.onEvent(UmengConfigs.VOICE_WINDOW0);
                break;
            case 202001:
                MobclickAgentEx.onEvent(UmengConfigs.VOICE_WINDOW1);
                break;
            case 207001:
                MobclickAgentEx.onEvent(UmengConfigs.VOICE_SKY_LIGHT1);
                break;
            case 207000:
                MobclickAgentEx.onEvent(UmengConfigs.VOICE_SKY_LIGHT0);
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
