package com.mapbar.android.obd.rearview.modules.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.obd.foundation.base.BaseFragment;

/**
 * 更改手机号 - 扫码完成后 - 扫码成功，等待填写
 * Created by zhangyunfei on 16/7/26.
 */
public class ChangePhoneWaitFragment extends BaseFragment {
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.setting_chang_phone_wait, null);
        }
        return rootView;
    }
}
