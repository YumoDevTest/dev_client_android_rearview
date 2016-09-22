package com.mapbar.android.obd.rearview.modules.controltest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.manager.CommandControl;
import com.mapbar.android.obd.rearview.framework.manager.VoiceReceiver;
import com.mapbar.android.obd.rearview.lib.base.AppPage2;

/**
 * Created by liuyy on 2016/5/15.
 */
public class ControlTestPage extends AppPage2 {

    private ListView lv;
    private int[] controlCmds;
    private String[] cmdStrs, cmdNames;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentView() == null) {
            createContenttView(R.layout.page_control_test);
            initView();
        }
        return getContentView();
    }

    public void initView() {
        lv = (ListView) getContentView().findViewById(R.id.lv_test);
        controlCmds = CommandControl.getInstance().getCommands();
        cmdStrs = CommandControl.getInstance().getCommadStrs();
        cmdNames = CommandControl.getInstance().getCommadNames();
        lv.setAdapter(new TestAdapter());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CommandControl.create().executeCommand(controlCmds[position]);
                Toast.makeText(getActivity(), "点击" + position + "  " + controlCmds[position], Toast.LENGTH_SHORT).show();
                final Intent intent = new Intent();
                intent.setAction(VoiceReceiver.VOICE_ACTION);
                intent.putExtra("command", controlCmds[position]);
                getActivity().sendBroadcast(intent);
            }
        });
    }


    class TestAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return controlCmds.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_test, null);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.item_test);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(cmdNames[position]);
            return convertView;
        }

        class ViewHolder {
            TextView tv;
        }
    }
}
