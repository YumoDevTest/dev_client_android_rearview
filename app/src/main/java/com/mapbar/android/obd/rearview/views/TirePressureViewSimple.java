package com.mapbar.android.obd.rearview.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.mapbar.android.obd.rearview.R;

/**
 * 4胎压指示器 ,显示胎压和胎温
 * Created by zhangyunfei on 16/8/3.
 */
public class TirePressureViewSimple extends FrameLayout {
    private LayoutInflater layoutInflater;
    private RadioButton image_sign;//指向标记
    private RadioButton indicator_text1;
    private RadioButton indicator_text2;
    private Boolean isWarning = false;//警告状态显示红色文字，否则绿色文字

    public TirePressureViewSimple(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TirePressureViewSimple(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TirePressureViewSimple(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TirePressureViewSimple);
        boolean isReverseSign = a.getBoolean(R.styleable.TirePressureViewSimple_reverse_sign,
                false);
        a.recycle();

        layoutInflater = LayoutInflater.from(context);
        View content = layoutInflater.inflate(R.layout.tire_pressure_simple_view, null);
        this.addView(content, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        image_sign = (RadioButton) content.findViewById(R.id.image_sign);
        indicator_text1 = (RadioButton) content.findViewById(R.id.indicator_text1);
        indicator_text2 = (RadioButton) content.findViewById(R.id.indicator_text2);

        if (isReverseSign) {
            ViewGroup viewGroup = (ViewGroup) image_sign.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(image_sign);
                viewGroup.addView(image_sign);
            }
            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.ic_tirepressure_sign_right);
            image_sign.setCompoundDrawables(drawable, null, null, null);
            image_sign.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
        setWarning(false);//设置 非警告状态
    }

    /**
     * 设置是否处于警告状态
     * 警告状态显示红色文字，否则绿色文字
     *
     * @param warning
     */
    public void setWarning(boolean warning) {
        this.isWarning = warning;
        image_sign.setChecked(isWarning);
        indicator_text1.setChecked(isWarning);
        indicator_text2.setChecked(isWarning);
        if (warning)
            indicator_text2.setText("异常");
        else
            indicator_text2.setText("正常");
    }

    /**
     * 是否处于警告状态
     *
     * @return
     */
    public Boolean isWarning() {
        return isWarning;
    }


}
