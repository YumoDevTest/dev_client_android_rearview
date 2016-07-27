package com.mapbar.android.obd.rearview.framework.widget;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.activity.AppPage;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.inject.ViewInjectTool;
import com.mapbar.android.obd.rearview.framework.inject.annotation.ViewInject;


/**
 * Created by liuyy on 2016/1/29.
 */
public class TitleBar {

    private View contentView;
    @ViewInject(R.id.tv_title_mid)
    private View vMid;

    @ViewInject(R.id.tv_title_right)
    private TextView tvRight;
    @ViewInject(R.id.iv_title_right)
    private ImageView ivRight;

    private View.OnClickListener leftListener;

    public TitleBar(AppPage parentPage, int id) {
        contentView = parentPage.getContentView().findViewById(id);
        ViewInjectTool.inject(this, contentView);
        intiView();
    }

    private void intiView() {
    }

    public void setVisibility(int visibility, TitleArea area) {
        switch (area) {
            case RIGHT:
                if (tvRight != null) {
                    tvRight.setVisibility(visibility);
                }
                break;
            default:
                break;
        }
    }

    public void setText(String text, TitleArea area) {
        switch (area) {
            case RIGHT:
                tvRight.setVisibility(View.VISIBLE);
                tvRight.setText(text);
                break;
            case MID:
                if (vMid != null) {
                    ((TextView) vMid).setText(text);
                }
                break;
            default:
                break;
        }
    }

    public void hideRight() {
        tvRight.setVisibility(View.GONE);
        ivRight.setVisibility(View.GONE);
    }

    public void setImage(int id, TitleArea area) {
        switch (area) {
            case RIGHT:
                ivRight.setVisibility(View.VISIBLE);
                ivRight.setImageResource(id);
                break;
            case MID:
                break;
            default:
                break;
        }
    }

    public void setText(int id, TitleArea area) {
        String text = Global.getAppContext().getResources().getString(id);
        setText(text, area);
    }

    public void setListener(View.OnClickListener listener, TitleArea area) {
        switch (area) {

            case RIGHT:
                tvRight.setOnClickListener(listener);
                ivRight.setOnClickListener(listener);
                break;
            case MID:
                break;
            default:
                break;
        }
    }

    public void setBackground(int resId) {
        contentView.setBackgroundDrawable(Global.getAppContext().getResources().getDrawable(resId));
    }

    public View getContentView() {
        return contentView;
    }

    /**
     * 控制自身的可见性
     * @param visibilitySelf
     */
    public void setVisibilitySelf(boolean visibilitySelf) {
        contentView.setVisibility(visibilitySelf ? View.VISIBLE : View.GONE);
    }

    /**
     * title区域枚举类型，左中右三个值
     */
    public enum TitleArea {
        MID, RIGHT
    }
}
