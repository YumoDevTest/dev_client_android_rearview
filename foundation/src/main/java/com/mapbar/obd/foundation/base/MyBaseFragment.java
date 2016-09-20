package com.mapbar.obd.foundation.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.foundation.umeng.MobclickAgentEx;

/**
 * Created by zhangyunfei on 16/7/26.
 */
public class MyBaseFragment extends Fragment {
    private static final String TAG = MyBaseActivity.class.getSimpleName();
    private String ThisClassName;

    public void alert(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void alert(final int sourceID) {
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
