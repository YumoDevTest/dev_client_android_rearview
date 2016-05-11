package com.mapbar.android.obd.rearview.framework.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.mapbar.android.obd.rearview.MainActivity;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.obd.CarStatusData;

/**
 * Created by liuyy on 2016/5/11.
 */
public class CarStateView {
    private View view;
    private CarStatusData data;
    private StateDrawable drawable;
    private Drawable drawableCar;

    public CarStateView(int id) {
        view = View.inflate(MainActivity.getInstance(), id, null);
        drawable = new StateDrawable();
        view.setBackgroundDrawable(drawable);
        drawableCar = MainActivity.getInstance().getResources().getDrawable(R.drawable.car_normal);
    }

    private class StateDrawable extends Drawable {


        @Override
        public void draw(Canvas canvas) {
            Rect rect = getBounds();
            drawableCar.draw(canvas);
            if (data != null) {
//                data
            }
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }
    }
}
