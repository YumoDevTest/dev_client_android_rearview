package com.mapbar.android.obd.rearview.lib.base;

import android.app.Activity;

import com.mapbar.obd.foundation.base.MyBaseFragment;
import com.mapbar.obd.foundation.log.LogUtil;

import java.lang.ref.WeakReference;

/**
 * Created by zhangyunfei on 16/9/20.
 */
public class TitlebarFragment extends MyBaseFragment {
    private static final String TAG = TitlebarFragment.class.getSimpleName();
    private WeakReference<TitlebarActivity> activityWeakReference;

    public TitlebarActivity getParentActivity() {
        return activityWeakReference == null ? null : activityWeakReference.get();
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof TitlebarActivity) {
            activityWeakReference = new WeakReference<>((TitlebarActivity) activity);
        }
        LogUtil.d(TAG, String.format("## %s onAttach", getThisClassName()));
        super.onAttach(activity);
    }
}
