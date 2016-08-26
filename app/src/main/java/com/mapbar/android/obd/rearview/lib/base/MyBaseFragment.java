package com.mapbar.android.obd.rearview.lib.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by zhangyunfei on 16/7/26.
 */
public class MyBaseFragment extends Fragment {
    private MyBaseFragmentActivity myBaseActivity;

    public void alert(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void alert(int sourceID) {
        Toast.makeText(getActivity(), sourceID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MyBaseFragmentActivity) {
            myBaseActivity = (MyBaseFragmentActivity) activity;
        }
    }

    public MyBaseFragmentActivity getParentActivity() {
        return myBaseActivity;
    }
}
