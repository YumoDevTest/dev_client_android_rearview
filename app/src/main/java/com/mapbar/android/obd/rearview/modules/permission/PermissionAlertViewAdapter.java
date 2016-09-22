package com.mapbar.android.obd.rearview.modules.permission;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mapbar.android.obd.rearview.lib.base.AppPage2;
import com.mapbar.android.obd.rearview.modules.permission.contract.IPermissionAlertViewAdatper;

import java.lang.ref.WeakReference;

/**
 * 试用到期页面会在多个页面试用，为了重用，我们抽离出代理类，代理类具体做操作
 * Created by zhangyunfei on 16/8/4.
 */
public class PermissionAlertViewAdapter implements IPermissionAlertViewAdatper {
    private PermissionAlertView permissionAlertView;
    private WeakReference<AppPage2> fragmentWeakReference;

    public AppPage2 getFragment() {
        if (fragmentWeakReference == null || fragmentWeakReference.get() == null)
            return null;
        return fragmentWeakReference.get();
    }

    public PermissionAlertViewAdapter(AppPage2 fragment) {
        this.fragmentWeakReference = new WeakReference<>(fragment);
    }

    /**
     * 根节点是个framentView,可以放入 授权提醒的视图作为浮层
     *
     * @param numberOfDay
     */
    public void showPermissionAlertView_FreeTrial(boolean isExpired, int numberOfDay) {
        if (getFragment() == null)
            throw new RuntimeException("has cleared");
        if (getFragment().getActivity() == null || getFragment().getActivity().isFinishing())
            return;

        if (permissionAlertView == null) {
            permissionAlertView = new PermissionAlertView(getFragment().getActivity());
            if(getFragment().getContentView() instanceof FrameLayout) {
                FrameLayout frameLayout = (FrameLayout) (getFragment().getContentView());
                if (frameLayout != null)
                    frameLayout.addView(permissionAlertView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        }
        permissionAlertView.setExpired(isExpired);//是否过期
        permissionAlertView.setNumberOfDay(numberOfDay);//剩余天数
        permissionAlertView.setOnContinueTryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidePermissionAlertView_FreeTrial();
            }
        });
    }

    public void hidePermissionAlertView_FreeTrial() {
        if (getFragment() == null)
            throw new RuntimeException("has cleared");
        if (getFragment().getActivity() == null || getFragment().getActivity().isFinishing())
            return;

        if (permissionAlertView != null && permissionAlertView.getParent() != null) {
            if (permissionAlertView.getParent() instanceof ViewGroup) {
                ((ViewGroup) permissionAlertView.getParent()).removeView(permissionAlertView);
            }
        }
    }

    public void clear() {
        if (fragmentWeakReference != null)
            fragmentWeakReference.clear();
        permissionAlertView = null;
    }
}
