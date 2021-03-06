package com.mapbar.android.obd.rearview.modules.setting;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.base.AppPage2;
import com.mapbar.obd.Manager;

/**
 * 关于 页
 * Created by zhangyunfei on 16/7/25.
 */
public class AboutFragment extends AppPage2 {
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.setting_about_fragment, null);

            TextView txt_app_version = (TextView) rootView.findViewById(R.id.txt_app_version);
            TextView txt_hard_version = (TextView) rootView.findViewById(R.id.txt_hard_version);

            txt_app_version.setText(getAppVersionName(getActivity()));

            String hardVersion = Manager.getInstance().getOBDVersion();
            txt_hard_version.setText(hardVersion);
        }
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (getParentActivity().getTitlebarview() != null) {
            getParentActivity().getTitlebarview().setTitle(R.string.page_title_about_us);
            getParentActivity().getTitlebarview().setEnableBackButton(true);
        }
    }


    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
//            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

}
