package com.mapbar.demo.serial;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.mapbar.demo.R;

import java.io.File;

/**
 * 选串口
 * Created by zhangyunfei on 16/9/23.
 */
public class ChooseSerialPortActivity extends Activity {

    private ListView listView;
    private QuickAdapter<String> adapter;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ota_choose_activity);
        listView = (ListView) findViewById(R.id.listView);
        adapter = createAdapter();
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.empty));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = adapter.getItem(position);
                Intent arg = new Intent();
                arg.putExtra("SERIAL_PORT", str);
                try {
                    pendingIntent.send(getActivity(), 0, arg);
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                setResult(RESULT_OK, arg);
                finish();
            }
        });

        loadData();

        if (getIntent() != null) {
            pendingIntent = getIntent().getParcelableExtra("TARGET");
        }
    }

    private void loadData() {
        adapter.clear();
        adapter.add("/dev/ttyMT0");
        adapter.add("/dev/ttyMT1");
        adapter.add("/dev/ttyMT2");
        adapter.add("/dev/ttyMT3");
        adapter.add("/dev/ttyMT4");
        adapter.add("/dev/ttyS0");
        adapter.add("/dev/ttyS1");
        adapter.add("/dev/ttyS2");
        adapter.add("/dev/ttyS3");
        adapter.add("/dev/ttyS4");
    }

    private QuickAdapter<String> createAdapter() {
        return new QuickAdapter<String>(getActivity(), android.R.layout.simple_list_item_1) {

            @Override
            protected void convert(BaseAdapterHelper helper, String item) {
                helper.setText(android.R.id.text1, item);
            }
        };
    }

    public Context getActivity() {
        return this;
    }
}
