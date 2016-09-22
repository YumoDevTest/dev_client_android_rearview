package com.mapbar.obd.foundation.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.foundation.umeng.MobclickAgentEx;

/**
 * 接触类
 * Created by zhangyunfei on 16/7/26.
 */
public class BaseFragment extends Fragment {
    private static final String TAG = BaseFragmentActivity.class.getSimpleName();
    private String ThisClassName;

    public void alert(final String msg) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void alert(final int sourceID) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), sourceID, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected String getThisClassName() {
        if (ThisClassName == null) {
            ThisClassName = getClass().getSimpleName();
        }
        return ThisClassName;
    }


    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d(TAG, String.format("## %s onStop", getThisClassName()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, String.format("## %s onCreate", getThisClassName()));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, String.format("## %s onCreateView", getThisClassName()));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, String.format("## %s onDestroy", getThisClassName()));
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.d(TAG, String.format("## %s onStart", getThisClassName()));
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgentEx.onPageStart(getThisClassName());
        LogUtil.d(TAG, String.format("## %s onPause", getThisClassName()));
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgentEx.onPageEnd(getThisClassName());
        LogUtil.d(TAG, String.format("## %s onResume", getThisClassName()));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogUtil.d(TAG, String.format("## %s onAttach", getThisClassName()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.d(TAG, String.format("## %s onDetach", getThisClassName()));
    }
}
