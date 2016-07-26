package com.mapbar.android.obd.rearview.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.inject.ViewInjectTool;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;


/**
 * 标题栏 view
 * Created by zhangyf on 2016-07-25
 */
public class TitleBarView extends RelativeLayout {

    private View contentView;

    @ViewInject(R.id.textview_mid)
    private TextView textview_mid;
    @ViewInject(R.id.imageview_left)
    private ImageView imageview_left;
    @ViewInject(R.id.textview_right)
    private TextView textview_right;

    private View.OnClickListener leftListener;

    public TitleBarView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        contentView = (RelativeLayout) layoutInflater.inflate(R.layout.titlebar_view, null);
        ViewInjectTool.inject(this, contentView);

        setTitle(R.string.app_name);
        setEnableBackButton(false);
        setButtonRightVisibility(false);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addView(contentView, lp);
    }


    public void setButtonRightVisibility(boolean visibility) {
        textview_right.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }


    public void setTitle(String text) {
        textview_mid.setText(text);
    }

    public void setTitle(int id) {
        textview_mid.setText(id);
    }


    public void setButtonRightText(String text) {
        textview_right.setText(text);
    }

    public void setButtonRightImage(int id) {
        textview_right.setBackgroundResource(id);
    }

    public void setButtonLeftListener(View.OnClickListener listener) {
        imageview_left.setOnClickListener(listener);
    }

    public void setButtonRightListener(View.OnClickListener listener) {
        textview_right.setOnClickListener(listener);
    }

    public void setButtonRight(String text, View.OnClickListener listener) {
        textview_right.setVisibility(View.VISIBLE);
        textview_right.setText(text);
        textview_right.setOnClickListener(listener);
    }


    /**
     * 启用返回按钮
     */
    public void setEnableBackButton(boolean isEnable) {
        if (isEnable) {
            imageview_left.setVisibility(View.VISIBLE);
            imageview_left.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getContext() instanceof Activity) {
                        Activity act = (Activity) getContext();
                        act.onBackPressed();
                    }
                }
            });
        } else {
            imageview_left.setVisibility(View.GONE);
        }
    }
}
