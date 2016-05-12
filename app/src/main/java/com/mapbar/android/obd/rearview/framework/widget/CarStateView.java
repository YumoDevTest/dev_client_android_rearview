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
    MainActivity context = MainActivity.getInstance();
    private View view;
    private CarStatusData data;
    private StateDrawable drawable;
    private Drawable drawableCar;

    public CarStateView(View parent, int id) {
        view = parent.findViewById(id);
        drawable = new StateDrawable();
        view.setBackgroundDrawable(drawable);
        drawableCar = context.getResources().getDrawable(R.drawable.car_normal);
    }

    public void setData(CarStatusData data) {
        this.data = data;
        invalidate();
    }

    public StateDrawable getDrawable() {
        return drawable;
    }

    public void invalidate() {
        if (drawable != null) {
            drawable.invalidateSelf();
        }
    }

    private class StateDrawable extends Drawable {

        @Override
        public void draw(Canvas canvas) {
            Rect rect = getBounds();
            drawableCar.draw(canvas);
            if (data != null) {
                switch (data.lights) {
                    case 0:
                    case -1:
                        break;
                    case 1:
                    case 2:
                        Drawable drawableLights = context.getResources().getDrawable(R.drawable.state_light);
                        drawableLights.draw(canvas);
                        break;
                    case 3:
                        break;
                }
                switch (data.sunroof) {
                    case 0:
                    case -1:
                        break;
                    case 1:
                    case 2:
                        Drawable drawableSunroof = context.getResources().getDrawable(R.drawable.state_sunroof);
                        drawableSunroof.draw(canvas);
                        break;
                    case 3:
                        break;
                }
                switch (data.windows) {
                    case 0:
                    case -1:
                        break;
                    case 1:
                    case 2:
                        Drawable drawableWindow = context.getResources().getDrawable(R.drawable.state_window);
                        drawableWindow.draw(canvas);
                        break;
                    case 3:
                        break;
                }
                switch (data.doors) {
                    case 0:
                    case -1:
                        break;
                    case 1:
                        Drawable drawableDoorLF = context.getResources().getDrawable(R.drawable.state_door_lf);
                        drawableDoorLF.draw(canvas);
                        Drawable drawableDoorLB = context.getResources().getDrawable(R.drawable.state_door_lb);
                        drawableDoorLB.draw(canvas);
                        Drawable drawableDoorRF = context.getResources().getDrawable(R.drawable.state_door_rf);
                        drawableDoorRF.draw(canvas);
                        Drawable drawableDoorRB = context.getResources().getDrawable(R.drawable.state_door_rb);
                        drawableDoorRB.draw(canvas);
                        break;
                    case 2:
                        break;
                }
                switch (data.lock) {
                    case 0:
                    case -1:
                        break;
                    case 1:
                        Drawable drawableLock = context.getResources().getDrawable(R.drawable.state_locked);
                        drawableLock.draw(canvas);
                        break;
                    case 2:
                        Drawable drawableLockN = context.getResources().getDrawable(R.drawable.state_nonlock);
                        drawableLockN.draw(canvas);
                        break;
                }
                switch (data.trunk) {
                    case 0:
                    case -1:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
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
