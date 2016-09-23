package com.mapbar.demo.serial;

import android.app.Activity;
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
import java.io.FileFilter;

/**
 * Created by zhangyunfei on 16/9/23.
 */
public class OtaChooseBinFileActivity extends Activity {

    private ListView listView;
    private QuickAdapter<String> adapter;
    private File binRootDir;

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
                File f = new File(binRootDir, str);
                Intent intent = new Intent(getActivity(), OtaFlushActivity.class);
                intent.putExtras(getIntent().getExtras());
                intent.putExtra("FILE", f.getPath());
                startActivity(intent);
            }
        });
        binRootDir = Environment.getExternalStorageDirectory();

        loadBinFiles();
    }

    private void loadBinFiles() {
        adapter.clear();
        File[] files = binRootDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".bin");
            }
        });
        if (files != null)
            for (int i = 0; i < files.length; i++) {
                adapter.add(files[i].getName());
                adapter.notifyDataSetChanged();
            }
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
