package com.mapbar.android.obd.rearview.framework.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.mapbar.android.obd.rearview.R;

/**
 * Created by THINKPAD on 2016/5/11.
 */
public class CircleDrawable extends Drawable {
    public static final int STROKE = 0;
    public static final int FILL = 1;
    /**
     * 画笔对象的引用
     */
    private Paint paint;
    /**
     * 圆环的颜色
     */
    private int circleColor;
    /**
     * 圆环进度的颜色
     */
    private int circleProgressColor;
    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;
    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;
    /**
     * 圆环的宽度
     */
    private float circleWidth;
    /**
     * 最大进度
     */
    private int max;
    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;
    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    public CircleDrawable(Context context) {

        paint = new Paint();
        circleColor = Color.GRAY;
        circleProgressColor = context.getResources().getColor(R.color.bg_title);
        textColor = Color.BLACK;
        textSize = 15;
        circleWidth = 5;
        max = 100;
        textIsDisplayable = false;
        style = 0;
    }

    @Override
    public void draw(Canvas canvas) {
        /**
         * 画最外层的大圆环
         */
        int center = getBounds().centerX();//获取圆心的x坐标
        int radius = (int) (center - circleWidth / 2); //圆环的半径
        paint.setColor(circleColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(circleWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(center, center, radius, paint); //画出圆环

        /**
         * 画进度百分比
         */

        int percent = (int) (((float) progress / (float) max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        if (textIsDisplayable && percent != 0 && style == STROKE) {

            paint.setStrokeWidth(0);
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
            float textWidth = paint.measureText(percent + "%");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
            canvas.drawText(percent + "%", center - textWidth / 2, center + textSize / 2, paint); //画出进度百分比
        }

        /**
         * 画圆弧 ，画圆环的进度
         */

        //设置进度是实心还是空心
        paint.setStrokeWidth(circleWidth); //设置圆环的宽度
        paint.setColor(circleProgressColor);  //设置进度的颜色
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius);  //用于定义的圆弧的形状和大小的界限

        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 270, 360 * progress / max, false, paint);  //根据进度画圆弧
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, 270, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }

    }


    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            invalidateSelf();
        }

    }


    public int getCricleColor() {
        return circleColor;
    }

    /**
     * 设置圆环底色
     *
     * @param cricleColor
     */
    public void setCricleColor(int cricleColor) {
        this.circleColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return circleProgressColor;
    }

    /**
     * 设置圆环显示的颜色
     *
     * @param cricleProgressColor
     */
    public void setCricleProgressColor(int cricleProgressColor) {
        this.circleProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getCircleWidth() {
        return circleWidth;
    }


    /**
     * 设置圆环的宽度
     *
     * @param circleWidth
     */
    public void setCircleWidth(float circleWidth) {
        this.circleWidth = circleWidth;
        //        invalidateSelf();
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
