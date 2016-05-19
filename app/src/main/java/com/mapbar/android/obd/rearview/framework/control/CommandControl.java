package com.mapbar.android.obd.rearview.framework.control;

import com.mapbar.android.obd.rearview.framework.manager.PhysicalManager;
import com.mapbar.obd.Manager;
import com.mapbar.obd.RealTimeData;

import java.util.ArrayList;

import static com.mapbar.android.obd.rearview.framework.control.VoiceManager.VoiceManagerHolder.voiceManager;

/**
 * 指令控制执行类
 */
public class CommandControl {
    private static CommandControl mCommandControl;
    private int[] commands = new int[]{201001, 201000, 202000, 202001, 207000, 207001};
    private String[] commadStrs = new String[]{"AT@STS020101", "AT@STS020102", "AT@STS010101", "AT@STS010102", "AT@STS010501", "AT@STS010502"};
    private String[] commadNames = new String[]{"开锁", "落锁", "降窗", "升窗", "开天窗", "关天窗"};

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
    public void executeCommand(final int command) {
        if (command >= 200000) {//控制类指令
            executeCommand2(command);
        } else {//非控制类指令
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
                    PhysicalManager.getInstance().startExam();
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
