package com.mapbar.android.obd.rearview.modules.permission;

import android.os.Bundle;
import android.view.View;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.obd.foundation.base.MyBaseActivity;

/**
 * 购买成功
 * Created by zhangyunfei on 16/8/25.
 */
public class PermissionBuySuccess extends MyBaseActivity {
    View btn_start;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_buy_success);

        btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }


}
