package com.mapbar.android.obd.rearview.framework.control;

import android.content.res.Resources;

import com.mapbar.android.obd.rearview.MainActivity;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.obd.Manager;
import com.mapbar.obd.RealTimeData;

import java.util.ArrayList;

import static com.mapbar.android.obd.rearview.framework.control.VoiceManager.VoiceManagerHolder.voiceManager;

/**
 * Created by tianff on 2016/5/7.
 */
public class CommandControl {
    private static CommandControl mCommandControl;

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
     * @param command
     */
    public void executeCommand(final int command) {
        if (command != 000000) {//有效指令
            if (command >= 2000000) {//控制类指令
                executeCommand2(command);
            } else {//非控制类指令
                RealTimeData realTimeData = Manager.getInstance().getRealTimeData();
                switch (command) {
                    case 102000://车速
                        voiceManager.sendBroadcastTTS(String.valueOf(realTimeData.speed));
                        break;
                    case 102001://转速
                        voiceManager.sendBroadcastTTS(String.valueOf(realTimeData.averageSpeed));
                        break;
                }
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
        Resources res = MainActivity.getInstance().getResources();
        int[] commands = res.getIntArray(R.array.command);
        ArrayList<Integer> commandList = new ArrayList<>();
        for (int command1 : commands) {
            commandList.add(command1);
        }
        int index = commandList.indexOf(command);//对应的可执行指令数组索引
        String[] commadStrs = res.getStringArray(R.array.command_string);
        if (index != -1) {
            String commandStr = commadStrs[index];
            Manager.getInstance().exuSpecialCarAction(commandStr);
        }
    }


}
