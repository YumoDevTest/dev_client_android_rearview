package com.mapbar.android.obd.rearview.modules.setting;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.manager.UserCenterManager;
import com.mapbar.android.obd.rearview.lib.base.MyBaseFragment;
import com.mapbar.android.obd.rearview.lib.eventbus.EventBusManager;
import com.mapbar.android.obd.rearview.modules.vin.VinManager;
import com.mapbar.android.obd.rearview.modules.vin.events.VinChangeFailureEvent;
import com.mapbar.android.obd.rearview.modules.vin.events.VinChangeSucccessEvent;
import com.mapbar.android.obd.rearview.modules.vin.events.VinScanEvent;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.android.obd.rearview.views.VinBarcodeView;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 更改VIN - 扫码页
 * Created by zhangyh on 2016/9/8.
 */
public class ChangeVinBarcodeFragment extends MyBaseFragment {
    VinBarcodeView vinBarcodeView;
    private static final int TIMEOUT = 300000;
    private Handler handler;
    private Timer timer;
    private static final String TAG = ChangePhoneActivity.class.getSimpleName();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new Handler();
        if (vinBarcodeView == null) {
            vinBarcodeView = new VinBarcodeView(getParentActivity());
            vinBarcodeView.showQrBarcode();
            vinBarcodeView.setText("请扫描修改VIN");
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //设置超时标志
                    UserCenterManager.getInstance().setOutTime(true);
                    onWaitUserOperationTimeout();
                }
            }, TIMEOUT, TIMEOUT);
        }
        return vinBarcodeView;
    }

    /**
     * 当用户更改VIN超时
     */
    private void onWaitUserOperationTimeout() {
        LogUtil.d(TAG, "##等待用户操作Timerout");
        UserCenterManager.getInstance().sdkListener.setActive(true);
        UserCenter.getInstance().DeviceLoginlogin(Utils.getImei(getActivity()));
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        });
    }

    /**
     * 当接收到 扫码成功时
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VinScanEvent event) {
        LogUtil.d(TAG, "##展示等待用户填写页面");
        vinBarcodeView.setText("扫码成功,请等待...");
    }

    /**
     * 当接收到 修改成功时
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VinChangeSucccessEvent event) {
        vinBarcodeView.setText("VIN修改成功");
        Toast.makeText(getActivity(), "VIN修改成功", Toast.LENGTH_SHORT).show();
        VinManager vinManager = new VinManager();
        LogUtil.d("TAG", "## 手动输入的VIN::: " + vinManager.getVinFromManual());
        getActivity().finish();
    }

    /**
     * 当接收到 修改失败时
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VinChangeFailureEvent event) {
        LogUtil.d(TAG, "##Vin修改失败");
        vinBarcodeView.setText("VIN修改失败");
        onWaitUserOperationTimeout();
    }

    @Override
    public void onStart() {
        EventBusManager.register(this);
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (timer != null)
            timer.cancel();
        EventBusManager.unregister(this);
        super.onDestroy();
    }
}
