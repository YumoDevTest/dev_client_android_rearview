package com.mapbar.android.obd.rearview.obd.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.obd.Constants;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 支持自动倒计时关闭的弹出对话框控件
 *
 * @author anjy
 */
public class TimerDialog extends Dialog implements OnClickListener {

    // 默认的倒计时是5秒，创建定时器的时候设置了定时器1秒之后生效
    public static final int DEFAULT_COUNTDOWN_NUMBER = 5;
    private static final String TAG = "[TimerDialog]";
    private TimerDialog.Listener mListener = null;
    private boolean mAutoClose = false;
    private int mCountdown = DEFAULT_COUNTDOWN_NUMBER;
    private Timer mTimer = null;
    private MyHandler mHandler = null;
    private Window mWindow = null;
    // UI
    private TextView mCountdownTextView = null;
    private TextView mContentTextView = null;
    private ImageView mIconImageView = null;
    private ViewGroup rela_close = null;

    public TimerDialog(Context context, TimerDialog.Listener listener,
                       boolean autoClose, int countdown) {
        super(context, R.style.Dialog);
        mListener = listener;
        mAutoClose = autoClose;
        mCountdown = countdown;
        mHandler = new MyHandler(this);
    }

    /**
     * 显示指定字符串
     *
     * @param text 需要显示的字符串
     */
    public void show(String text) {
        show(text, 0);
    }

    /**
     * 弹窗 显示 提醒消息
     *
     * @param text
     */
    public void showAlerm(String text) {
        if (getContentTextView() != null) {
            getContentTextView().setTextColor(getContext().getResources().getColor(R.color.dashboard_red_text));
        }
        setCountdown(TimerDialog.DEFAULT_COUNTDOWN_NUMBER);
        show(text);
    }

    /**
     * 显示指定的文字和图标
     *
     * @param text           需要显示的文字
     * @param resourceIconId 需要显示的图标的资源ID
     */
    public void show(String text, int resourceIconId) {

        try {
            super.show();
            if (text != null) {
                mContentTextView.setText(text);
            } else {
                if (Constants.DEBUG) {
                    Log.w(TAG, "Text is null!");
                }
                mContentTextView.setText("");
            }
            if (resourceIconId != 0) {
                mIconImageView.setVisibility(View.VISIBLE);
                mIconImageView.setBackgroundResource(resourceIconId);
            } else {
                mIconImageView.setVisibility(View.GONE);
            }
            if (mAutoClose) {
                mCountdownTextView.setText("(" + mCountdown + "s)");
                if (mTimer == null) {
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            mCountdown--;
                            mHandler.sendEmptyMessage(0);
                        }
                    }, 1000, 1000);
                }
            } else {
                // 如果不需要自动关闭，则不显示倒计时
                mCountdownTextView.setVisibility(View.GONE);
            }
        } catch (Exception e) {//BadTokenException
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_timer_dialog);
        rela_close = (ViewGroup) findViewById(R.id.rela_close);
        rela_close.setOnClickListener(this);
        this.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (mListener != null) {
                    mListener.onCancel(dialog);
                } else {
                    if (Constants.DEBUG) {
                        Log.w(TAG, "TimerDialog.Listener is null!");
                    }
                }
            }
        });

        rela_close = (ViewGroup) findViewById(R.id.rela_close);
        mCountdownTextView = (TextView) findViewById(R.id.tv_countdown);
        mContentTextView = (TextView) findViewById(R.id.tv_td_content);
        mIconImageView = (ImageView) findViewById(R.id.iv_td_icon);
        mIconImageView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onClick(view);
        } else {
            if (Constants.DEBUG) {
                Log.d(TAG, "### TimerDialog.Listener ### is null!");
            }
        }
    }

    /**
     * 获取当前页面的倒计时，单位：秒，缺省值：5
     *
     * @return 当前的倒计时秒数
     */
    public int getCountdown() {
        return mCountdown;
    }

    /**
     * 设置显示倒计时，仅在自动关闭的模式下且在{@link TimerDialog#show(String)}方法之前调用才能生效
     *
     * @param countdown 需要设置的倒计时，单位：秒，缺省值：5
     */
    public void setCountdown(int countdown) {
        if (mAutoClose) {
            mCountdown = countdown;
        } else {
            if (Constants.DEBUG) {
                Log.e(TAG, "The TimerDialog don't close itself automatically!");
            }
        }
    }

    /**
     * 当前弹出框是否自动关闭
     *
     * @return true表示自动关闭，false表示手动关闭
     */
    public boolean getAutoClose() {
        return mAutoClose;
    }

    /**
     * 设置当前弹出框是否自动关闭，此方法只能在{@link TimerDialog#show(String)}方法之前调用有效
     *
     * @param autoClose true表示自动关闭，false表示手动关闭
     */
    public void setAutoClose(boolean autoClose) {
        mAutoClose = autoClose;
    }

    @Override
    public void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        super.cancel();
    }

    /**
     * 设置窗口显示方式，添加动画
     */
    public void windowDeploy() {
        mWindow = getWindow();
        // 设置弹出动画
        mWindow.setWindowAnimations(R.style.DialogAnimation);
    }

    public TextView getContentTextView() {
        return mContentTextView;
    }

    /**
     * 收到点击消息时触发此回调
     *
     * @author anjy
     */
    public interface Listener {
        /**
         * 点击对话框时会收到此回调
         *
         * @param view 点击的对话框实例
         */
        public void onClick(View view);

        /**
         * 对话框被取消时收到此回调
         *
         * @param view 点击的对话框实例
         */
        public void onCancel(DialogInterface dialog);
    }

    private static class MyHandler extends Handler {

        private WeakReference<TimerDialog> mTimerDialogWeakRef = null;

        public MyHandler(TimerDialog timerDialog) {
            mTimerDialogWeakRef = new WeakReference<TimerDialog>(timerDialog);
        }

        @Override
        public void handleMessage(Message msg) {
            TimerDialog td = mTimerDialogWeakRef.get();
            if (td != null) {
                td.mCountdownTextView.setText("(" + td.mCountdown + "s)");
                if (td.mCountdown <= 0) {
                    td.mCountdown = 0;
                    if (td.isShowing())
                        td.cancel();
                    if (td.mTimer != null) {
                        td.mTimer.cancel();
                        td.mTimer = null;
                    }
                    return;
                }
            } else {
                if (Constants.DEBUG) {
                    Log.w(TAG, "WeakReference<TimerDialog> is null");
                }
            }
        }
    }

}
