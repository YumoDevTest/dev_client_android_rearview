package com.mapbar.android.obd.rearview.obd.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.control.CommandControl;
import com.mapbar.android.obd.rearview.framework.control.VoiceReceiver;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;

/**
 * Created by liuyy on 2016/5/15.
 */
public class ControlTestPage extends AppPage {

    @ViewInject(R.id.lv_test)
    private ListView lv;

    private int[] controlCmds;
    private String[] cmdStrs, cmdNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_control_test);
    }

    @Override
    public void initView() {
        controlCmds = CommandControl.getInstance().getCommands();
        cmdStrs = CommandControl.getInstance().getCommadStrs();
        cmdNames = CommandControl.getInstance().getCommadNames();
        lv.setAdapter(new TestAdapter());

        setListener();
    }

    public void setListener() {
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
