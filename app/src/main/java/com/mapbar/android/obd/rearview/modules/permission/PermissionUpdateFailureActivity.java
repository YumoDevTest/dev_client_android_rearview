package com.mapbar.android.obd.rearview.modules.permission;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.R;

/**
 * 权限失败dialog
 * Created by zhangyunfei on 16/8/4.
 */
public class PermissionUpdateFailureActivity extends Activity {

    private View contentView;
    private Button btn_retry;
    private Button btn_skip;
    private PermissonCheckerOnStart permissonCheckerOnStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(this);
        contentView = inflater.inflate(R.layout.permission_failure, null);
        setContentView(contentView);

        btn_retry = (Button) contentView.findViewById(R.id.btn_retry);
        btn_skip = (Button) contentView.findViewById(R.id.btn_skip);

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickButtonRetry();
            }

        });
        btn_skip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //下载失败时，跳过而不再下载，进入下一流程，检查本地权限摘要
                permissonCheckerOnStart.checkLocalPermissionSummary(getActivity());
                finish();
            }
        });

        permissonCheckerOnStart = new PermissonCheckerOnStart();
        //如果本地权限为空，则隐藏跳过按钮
        if (permissonCheckerOnStart.isLocalPermissionEmpty(getActivity())) {
            btn_skip.setVisibility(View.GONE);
        }

        if (getIntent() != null) {
            String error = getIntent().getStringExtra("error");
            if (!TextUtils.isEmpty(error)) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 重试按钮点击事件
     */
    private void onClickButtonRetry() {
        permissonCheckerOnStart.downloadPermision(getActivity());
        finish();
    }

    public Activity getActivity() {
        return this;
    }

}
