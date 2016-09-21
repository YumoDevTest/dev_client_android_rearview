package com.mapbar.android.obd.rearview.modules.permission;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.base.SimpleActivity;
import com.mapbar.android.obd.rearview.modules.push.events.PermissionBuyEvent;
import com.mapbar.box.protobuf.bean.ObdRightBean;
import com.mapbar.obd.foundation.log.LogUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 权限失败dialog
 * Created by zhangyunfei on 16/8/4.
 */
public class PermissionUpdateFailureActivity extends SimpleActivity {
    public static final String TAG = PermissionUpdateFailureActivity.class.getSimpleName();

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
                LogUtil.d(TAG, "## 点击跳过按钮");
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
                alert(error);
            }
        }
    }

    /**
     * 重试按钮点击事件
     */
    private void onClickButtonRetry() {
        permissonCheckerOnStart.downloadPermision(getActivity(), new PermissionManager.DownloadPermissionCallback() {

            @Override
            public void onSuccess(List<ObdRightBean.ObdRight> permissionList) {
                //表示更新权限成功。
                LogUtil.d(TAG, "## 重试成功");
                permissonCheckerOnStart.checkLocalPermissionSummary(getActivity());
                finish();
            }

            @Override
            public void onFailure(final Exception ex) {
                //提示用户，更新权限失败
                if (getActivity() != null && !getActivity().isFinishing())
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alert("" + ex.getMessage());
                        }
                    });
            }
        });
    }

    public Activity getActivity() {
        return this;
    }


    /**
     * 收到推送事件：购买功能成功或失败
     *
     * @param permissionBuyResult
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PermissionBuyEvent permissionBuyResult) {
        //购买成功则关闭自身,MainActivity也会收到推送，它会下载最新的权限
        if (permissionBuyResult.isBuySuccess()) {
            finish();
        }
    }
}
