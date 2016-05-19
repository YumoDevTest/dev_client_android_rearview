package com.mapbar.android.obd.rearview.framework.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.manager.CarStateManager;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.obd.CarStatusData;

/**
 * Created by liuyy on 2016/5/11.
 */
public class CarStateView {
    MainActivity context = MainActivity.getInstance();
    private View view;
    private CarStatusData data;
    private StateDrawable drawable;
    private Bitmap bmCar;

    public CarStateView(View parent, int id) {
        view = parent.findViewById(id);
        drawable = new StateDrawable();
        bmCar = BitmapFactory.decodeResource(context.getResources(), R.drawable.car_normal);
        view.setBackgroundDrawable(drawable);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarStateManager.getInstance().tryToGetData();
            }
        });
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
        Paint paint = new Paint();
        private Rect bounds;

        @Override
        public void draw(Canvas canvas) {
            bounds = getBounds();
            canvas.drawBitmap(bmCar, null, bounds, paint);
            if (data != null) {
                switch (data.lights) {
                    case 0:
                    case -1:
                        break;
                    case 1:
                    case 2:
                        Bitmap bmLights = BitmapFactory.decodeResource(context.getResources(), R.drawable.state_light);
                        canvas.drawBitmap(bmLights, null, bounds, paint);
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
                        Bitmap bmSunroof = BitmapFactory.decodeResource(context.getResources(), R.drawable.state_sunroof);
                        canvas.drawBitmap(bmSunroof, null, bounds, paint);
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
                        Bitmap bmWindow = BitmapFactory.decodeResource(context.getResources(), R.drawable.state_window);
                        canvas.drawBitmap(bmWindow, null, bounds, paint);
                        break;
                    case 3:
                        break;
                }
                switch (data.doors) {
                    case 0:
                    case -1:
                        break;
                    case 1:
                        Bitmap bmDoorLBDoorLF = BitmapFactory.decodeResource(context.getResources(), R.drawable.state_door_lf);
                        canvas.drawBitmap(bmDoorLBDoorLF, null, bounds, paint);
                        Bitmap bmDoorLBDoorLB = BitmapFactory.decodeResource(context.getResources(), R.drawable.state_door_lb);
                        canvas.drawBitmap(bmDoorLBDoorLB, null, bounds, paint);
                        Bitmap bmDoorLBDoorRF = BitmapFactory.decodeResource(context.getResources(), R.drawable.state_door_rf);
                        canvas.drawBitmap(bmDoorLBDoorRF, null, bounds, paint);
                        Bitmap bmDoorLBDoorRB = BitmapFactory.decodeResource(context.getResources(), R.drawable.state_door_rb);
                        canvas.drawBitmap(bmDoorLBDoorRB, null, bounds, paint);
                        break;
                    case 2:
                        break;
                }
                switch (data.lock) {
                    case 0:
                    case -1:
                        break;
                    case 1:
                        Bitmap bmLock = BitmapFactory.decodeResource(context.getResources(), R.drawable.state_locked);
                        canvas.drawBitmap(bmLock, null, bounds, paint);
                        break;
                    case 2:
                        Bitmap bmLockN = BitmapFactory.decodeResource(context.getResources(), R.drawable.state_nonlock);
                        canvas.drawBitmap(bmLockN, null, bounds, paint);
                        break;
                }
                switch (data.trunk) {
                    case 0:
                    case -1:
                        break;
                    case 1:
                        Bitmap bmTrunk = BitmapFactory.decodeResource(context.getResources(), R.drawable.state_trunk);
                        canvas.drawBitmap(bmTrunk, null, bounds, paint);
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
