package com.mapbar.android.obd.rearview.obd;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.lib.ota.OTAManager;
import com.mapbar.obd.Firmware;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wanghw on 2016/7/27.
 */
public class FirmwareDialogHandler {

    private static final int TYPE_FORCE = 0, TYPE_NORMAL = 1;
    private static int countdown_number = 5;
    private View popupView;
    private View prompt_content;
    private View succ_content;
    private View fail_content;
    private View progress_content;
    private View force_content;
    private ArrayList<View> views;
    private TextView tv_download_progress;
    private TextView tv_firmware_force_content;
    private TextView tv_firmware_pop_content;
    private TextView tv_firmware_force_update;
    private ProgressBar progressbar;
    private TextView tv_firmware_pop_update;
    private TextView tv_firmware_pop_cancle;
    private String firmware_force_update_tip;
    private ViewOnClickListener listener;
    private PopupWindow firmwarePopu;
    private FlashListener flashListener;
    private TimerHandler mHandler = null;
    private Timer mTimer = null;


    public void showAtLocation(View parent, int gravity, int x, int y) {

        showAtLocation(parent, gravity, x, y, null);
    }

    public void showAtLocation(View parent, int gravity, int x, int y, FlashListener flashListener) {

        this.flashListener = flashListener;
        initView();

        firmwarePopu = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置点击PopupWindow以外的区域取消PopupWindow的显示
//        firmwarePopu.setOutsideTouchable(false);//TODO 针对是否强制升级进行处理
        firmwarePopu.setBackgroundDrawable(new BitmapDrawable());
        firmwarePopu.showAtLocation(parent, gravity, x, y);
    }

    private void showCurrentView(View showView) {
        if (!views.contains(showView))
            return;
        for (int i = 0; i < views.size(); i++) {
            views.get(i).setVisibility(View.GONE);
        }
        showView.setVisibility(View.VISIBLE);
    }

    private void initView() {
        if (views == null) {
            firmware_force_update_tip = MainActivity.getInstance().getString(R.string.firmware_force_update_tip) + countdown_number + "s";
            views = new ArrayList<View>();
            popupView = View.inflate(MainActivity.getInstance(), R.layout.layout_firmware_pop, null);

            prompt_content = popupView.findViewById(R.id.firmware_update_prompt_content);
            force_content = popupView.findViewById(R.id.firmware_update_force_content);

            succ_content = popupView.findViewById(R.id.firmware_update_succ_content);
            fail_content = popupView.findViewById(R.id.firmware_update_fail_content);
            progress_content = popupView.findViewById(R.id.firmware_update_progress_content);

            views.add(prompt_content);
            views.add(succ_content);
            views.add(fail_content);
            views.add(progress_content);
            views.add(force_content);

            tv_firmware_pop_content = (TextView) prompt_content.findViewById(R.id.tv_firmware_pop_content);
            tv_download_progress = (TextView) progress_content.findViewById(R.id.tv_download_progress);


            tv_firmware_force_content = (TextView) force_content.findViewById(R.id.tv_firmware_force_content);
            tv_firmware_force_content.setText(firmware_force_update_tip);

            tv_firmware_force_update = (TextView) force_content.findViewById(R.id.tv_firmware_force_update);
            progressbar = (ProgressBar) progress_content.findViewById(R.id.progressbar);

            View firmware_update_succ_confirm = succ_content.findViewById(R.id.firmware_update_succ_confirm);

            View firmware_update_fail_confirm = fail_content.findViewById(R.id.firmware_update_fail_confirm);
            View tv_firmware_pop_fail_cancle = fail_content.findViewById(R.id.tv_firmware_pop_fail_cancle);

            tv_firmware_pop_update = (TextView) popupView.findViewById(R.id.tv_firmware_pop_update);
            tv_firmware_pop_cancle = (TextView) popupView.findViewById(R.id.tv_firmware_pop_cancle);

            listener = new ViewOnClickListener();
            tv_firmware_pop_update.setOnClickListener(listener);//正常的升级按钮
            tv_firmware_pop_cancle.setOnClickListener(listener);//正常的取消按钮
            tv_firmware_force_update.setOnClickListener(listener);//强制升级的升级按钮
            firmware_update_succ_confirm.setOnClickListener(listener);//成功后的重启应用
            firmware_update_fail_confirm.setOnClickListener(listener);//失败后的重试
            tv_firmware_pop_fail_cancle.setOnClickListener(listener);//失败后的取消

            if (Firmware.getInstance(MainActivity.getInstance()).isForceUpdate()) {
                showCurrentView(force_content);
                startTimer(TYPE_FORCE);
            } else {
                showCurrentView(prompt_content);
                startTimer(TYPE_NORMAL);
            }

        } else {
            startTimer(TYPE_NORMAL);
        }
    }

    private void startTimer(final int typeFirmware) {
        switch (typeFirmware) {
            case TYPE_NORMAL://等待10秒 消失dialog 考虑当前用户的操作
                //判断用户有没有点击升级操作,可通过两个按钮的是否显示来判断
                countdown_number = 10;
                break;
            case TYPE_FORCE://等待5秒 开始刷 考虑当前用户的操作
                countdown_number = 5;
                break;
        }
        tv_firmware_pop_content.setText(MainActivity.getInstance().getString(R.string.tv_firmware_pop_content_tip) + countdown_number + "s");
        if (mHandler == null)
            mHandler = new TimerHandler();

        if (mTimer == null)
            mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                countdown_number--;
                Message msg = Message.obtain();
                Log.e(LogTag.OBD, "whw --> typeFirmware ==" + typeFirmware);
                msg.what = typeFirmware;
                mHandler.sendMessage(msg);
            }
        }, 1000, 1000);

    }

    private void stopTimer() {
        if (mTimer != null) {
            countdown_number = 10;
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void updateFirmware() {
        showCurrentView(progress_content);
        OTAManager.getInstance().upgrade(MainActivity.getInstance(), new Firmware.UpgradeCallback() {

            @Override
            public void onDownResult(int statusCode, File file) {
                //TODO 下载失败
            }

            @Override
            public void onDownProgress(int bytesWritten, int totalSize) {

            }

            @SuppressLint("NewApi")
            @Override
            public void onFlashResult(final int statusCode, File file) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progress_content.setVisibility(View.GONE);
                        switch (statusCode) {
                            case Firmware.UpgradeCallback.STATUSCODE_FLASH_OK:
//                        Log.d(LogTag.OTA, "whw -->> 升级成功 == ");
                                //TODO 显示升级成功的ui 点击完成 重启客户端
                                if (flashListener != null) {
                                    flashListener.onFlashSucc();
                                }
                                showCurrentView(succ_content);
//                        tv_state.setText("当前车型支持宝马车辆控制功能");//TODO 通过接口回调返回刷固件结果对应改变文案
                                break;
                            case Firmware.UpgradeCallback.STATUSCODE_FLASH_FAILED:
                                //TODO 显示升级失败的ui 点击重试重新升级

                                showCurrentView(fail_content);

                                break;
                            default:
                                break;
                        }
                    }
                });


            }

            @Override
            public void onFlashProgress(final int progress, final int totalSize) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //TODO 显示刷固件进度
                        showCurrentView(progress_content);
//                        int pb = (int) (Float.intBitsToFloat(bytesWritten) / Float.intBitsToFloat(totalSize) * 100f);
                        progressbar.setProgress(progress);
                        tv_download_progress.setText(progress + "%");
                    }
                });
            }
        });
    }

    public interface FlashListener {
        public void onFlashSucc();
    }


    private class TimerHandler extends Handler {

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        @Override
        public void handleMessage(Message msg) {

            Log.e(LogTag.OBD, "whw --> msg.what ==" + msg.what);
            if (countdown_number == 0) {
                switch (msg.what) {
                    case TYPE_NORMAL://等待10秒 消失dialog 考虑当前用户的操作
                        //判断用户有没有点击升级操作,可通过两个按钮的是否显示来判断
//                        countdown_number = 10;
                        if (prompt_content.getVisibility() == View.VISIBLE) {
                            tv_firmware_pop_cancle.callOnClick();
                        }
                        break;
                    case TYPE_FORCE://等待5秒 开始刷 考虑当前用户的操作
//                        countdown_number = 5;
                        if (force_content.getVisibility() == View.VISIBLE) {
                            updateFirmware();
                        }
                        break;
                }
                stopTimer();
            } else {
                switch (msg.what) {
                    case TYPE_NORMAL:
                        tv_firmware_pop_content.setText(MainActivity.getInstance().getString(R.string.tv_firmware_pop_content_tip) + countdown_number + "s");
                        break;
                    case TYPE_FORCE:
                        tv_firmware_force_content.setText(MainActivity.getInstance().getString(R.string.firmware_force_update_tip) + countdown_number + "s");
                        break;
                }
            }
        }
    }

    /**
     * 原有逻辑:普通的：点击升级，刷固件,取消,关popu
     */
    private class ViewOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.tv_firmware_pop_update://点击通用升级
                    updateFirmware();
                    break;

                case R.id.tv_firmware_pop_cancle:
                case R.id.tv_firmware_pop_fail_cancle:
                    stopTimer();
                    firmwarePopu.dismiss();
                    break;

                case R.id.tv_firmware_force_update://点击强制升级
                    updateFirmware();
                    break;

                case R.id.firmware_update_succ_confirm://升级成功,点击完成按钮,重启客户端
                    stopTimer();
                    firmwarePopu.dismiss();
                    MainActivity.getInstance().restartApp();
                    break;

                case R.id.firmware_update_fail_confirm://点击重试
                    updateFirmware();
                    break;
            }
        }
    }
}
