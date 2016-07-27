package com.mapbar.android.obd.rearview.modules.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.base.MyBaseFragment;

/**
 * 更改手机号 - 扫码完成后 - 填写成功
 * Created by zhangyunfei on 16/7/26.
 */
public class ChangePhoneFinishFragment extends MyBaseFragment {
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.setting_chang_phone_finish, null);
        }
        return rootView;
    }
}