package com.mapbar.android.obd.rearview.framework.control;

import android.content.res.Resources;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.obd.Manager;
import com.mapbar.obd.RealTimeData;

import java.util.ArrayList;

import static com.mapbar.android.obd.rearview.framework.control.VoiceManager.VoiceManagerHolder.voiceManager;

/**
 * Created by tianff on 2016/5/7.
 */
public class CommandControl {
    private static CommandControl mCommandControl;
    private int[] commands;
    private String[] commadStrs;
    private String[] commadNames;

    private CommandControl() {
        Resources res = Global.getAppContext().getResources();
        commands = res.getIntArray(R.array.control_command);
        commadStrs = res.getStringArray(R.array.command_string);
        commadNames = res.getStringArray(R.array.command_name);
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
     * @param command
     */
    public void executeCommand(final int command) {
        if (command >= 2000000) {//控制类指令
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
    public void executeCommand2(final int command) {
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
