package com.mapbar.android.obd.rearview.framework.widget.hud;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;


/**
 * 转圈圈
 *
 * @author 刘焰宇
 * @date 2014-3-7
 */
public class ProgressView extends RelativeLayout {
    private static final int BAR_SIZE_DIP = Build.VERSION.SDK_INT < Build.VERSION_CODES.BASE ? 45 : 60;
    private static final int BAR_CENTER_Y_OFFSET_DIP = 22;
    private static final int TEXT_CENTER_Y_OFFSET_DIP = -35;
    private static final int TEXT_HEIGHT_DIP = 25;

    private Activity activity;
    private FrameLayout layout;
    private Resources resources;
    private OnCancelListener listener;

    private View alert;
    private int alertWidth;
    private int alertHeight;

    private ProgressBar bar;
    private int barWidth;
    private int barHeight;
    private int barOffset;

    private TextView textView;
    private String text = "";
    private int textWidth;
    private int textHeight;
    private int textOffset;

    public ProgressView(Activity activity) {
        super(activity);
        this.activity = activity;
        this.resources = getResources();
        init();
    }

    public ProgressView(Activity activity, String text) {
        super(activity);
        this.activity = activity;
        this.resources = getResources();
        this.text = text;
        init();
    }

    public ProgressView(Activity activity, int textRes) {
        super(activity);
        this.activity = activity;
        this.resources = getResources();
        this.text = resources.getText(textRes).toString();
        init();
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 初始化方法
     */
    public void init() {
        // 设置背景及初始化
        setBackgroundColor(Color.TRANSPARENT);
        ViewGroup view = (ViewGroup) activity.getWindow().getDecorView();
        layout = (FrameLayout) view.findViewById(android.R.id.content);
        setClickable(true);

        // 设置半透明alert
        alert = new View(activity);
        Drawable drawable = resources.getDrawable(R.drawable.alert);
        alertWidth = drawable.getIntrinsicWidth();
        alertHeight = drawable.getIntrinsicHeight();
        alert.setBackgroundResource(R.drawable.alert);
        addView(alert);

        // 设置转圈圈
        bar = new ProgressBar(activity, null, android.R.attr.progressBarStyleLargeInverse);
        barWidth = barHeight = ScreenParam.convertDipToPx(activity, BAR_SIZE_DIP);
        barOffset = ScreenParam.convertDipToPx(activity, BAR_CENTER_Y_OFFSET_DIP);
        addView(bar);

        // 设置文本
        textWidth = alertWidth;
        textHeight = ScreenParam.convertDipToPx(activity, TEXT_HEIGHT_DIP);
        textView = new TextView(activity);
        textView.setTextSize(18);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setText(text);
        textOffset = ScreenParam.convertDipToPx(activity, TEXT_CENTER_Y_OFFSET_DIP);
        addView(textView, new LayoutParams(textWidth, textHeight));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 设置透明alert布局
        int width = r - l;
        int height = b - t;
        int alertLeft = (width - alertWidth) / 2;
        int alertRight = alertLeft + alertWidth;
        int alertTop = (height - alertHeight) / 2;
        int alertBottom = alertTop + alertHeight;
        alert.layout(alertLeft, alertTop, alertRight, alertBottom);

        // 设置转圈圈布局
        int barLeft = (width - barWidth) / 2;
        int barRight = barLeft + barWidth;
        int barTop = (height - barHeight) / 2 - barOffset;
        int barBottom = barTop + barHeight;
        bar.layout(barLeft, barTop, barRight, barBottom);

        // 设置文字布局
        int textLeft = (width - textWidth) / 2;
        int textRight = textLeft + textWidth;
        int textTop = (height - textHeight) / 2 - textOffset;
        int textBottom = textTop + textHeight;
        textView.layout(textLeft, textTop, textRight, textBottom);
    }

    public String getText() {
        return text;
    }

    public ProgressView setText(int textRes) {
        String text = resources.getText(textRes).toString();
        if (this.text != text) {
            this.text = text;
            textView.setText(text);
            invalidate();
        }
        return this;
    }

    public ProgressView setText(String text) {
        if (this.text != text) {
            this.text = text;
            textView.setText(text);
            invalidate();
        }
        return this;
    }

    public OnCancelListener getOnCancelListener() {
        return listener;
    }

    public ProgressView setOnCancelListener(OnCancelListener listener) {
        this.listener = listener;
        return this;
    }

    public void show() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocusFromTouch();
        layout.addView(this);
    }

    public void dismiss() {
        setFocusable(false);
        layout.removeView(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
            if (listener != null) {
                listener.onCancel(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 取消监听
     */
    public interface OnCancelListener {
        void onCancel(ProgressView progressView);
    }

}
