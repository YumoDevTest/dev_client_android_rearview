package com.mapbar.android.obd.rearview.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.mapbar.android.obd.rearview.R;

/**
 * 4胎压指示器 ,显示胎压和胎温
 * Created by zhangyunfei on 16/8/3.
 */
public class TirePressureViewDigital extends FrameLayout {
    private LayoutInflater layoutInflater;
    private RadioButton tire_pressure;//胎压
    private RadioButton tire_temperature;//胎温
    private RadioButton tire_pressure_unit;//胎压 单位 bar
    private RadioButton tire_temperature_unit;//胎温 单位 °C
    private Boolean isWarning = false;//警告状态显示红色文字，否则绿色文字
    private View tire_pressure_divider;//分割线

    public TirePressureViewDigital(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TirePressureViewDigital(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TirePressureViewDigital(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        layoutInflater = LayoutInflater.from(context);
        View content = layoutInflater.inflate(R.layout.tire_pressure_digital_view, null);
        this.addView(content, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        tire_pressure = (RadioButton) content.findViewById(R.id.tire_pressure);
        tire_pressure_unit = (RadioButton) content.findViewById(R.id.tire_pressure_unit);
        tire_temperature = (RadioButton) content.findViewById(R.id.tire_temperature);
        tire_temperature_unit = (RadioButton) content.findViewById(R.id.tire_temperature_unit);
        tire_pressure_divider = content.findViewById(R.id.tire_pressure_divider);
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
        tire_pressure.setChecked(isWarning);
        tire_pressure_unit.setChecked(isWarning);
        tire_temperature.setChecked(isWarning);
        tire_temperature_unit.setChecked(isWarning);
        if (warning)
            tire_pressure_divider.setBackgroundColor(getResources().getColor(R.color.tire_pressture_text_warning));
        else
            tire_pressure_divider.setBackgroundColor(getResources().getColor(R.color.tire_pressture_text_normal));
    }

    /**
     * 是否处于警告状态
     *
     * @return
     */
    public Boolean isWarning() {
        return isWarning;
    }

    /**
     * 设置胎压
     *
     * @param tirePressure
     */
    public void setTirePressure(String tirePressure) {
        tire_pressure.setText(tirePressure);
    }

    /**
     * 设置胎温
     *
     * @param tireTemperature
     */
    public void setTireTemperature(String tireTemperature) {
        tire_temperature.setText(tireTemperature);
    }
}
