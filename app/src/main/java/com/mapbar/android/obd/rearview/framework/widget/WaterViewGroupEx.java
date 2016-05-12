package com.mapbar.android.obd.rearview.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.common.UtilTools;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by THINKPAD on 2016/5/11.
 */
public class WaterViewGroupEx extends ViewGroup {

    /**
     * 增长
     */
    public static final int MODE_INCREASE = 0;
    /**
     * 降低
     */
    public static final int MODE_DECREASE = 1;
    /**
     * 起始位置 (对圆有效)
     */
    private int mStartAngle = 0;
    /**
     * 范围(0-100)
     */
    private int mRange = 0;
    /**
     * 范围(0-100)
     */
    private int mOldRange = 0;
    /**
     * 当前位置(百分比)
     */
    private int mCurrentPosition = 0;
    /**
     * 每次绘制跨度(百分比)
     */
    private int mSpanPercent = 1;
    private int span = 0;
    /**
     * 绘制时间间隔
     */
    private int mFrameTime = 10;
    private MyView mView;
    private Drawable drawable;
    private float mWidth;
    private float mHeight;
    /**
     * 底图绘制方式
     */
    private int type = 0;
    private int TYPE_DRAW_ARC = 0;
    private int TYPE_DRAW_RECT = 1;
    /**
     * 是否正在绘制
     */
    private boolean isDraw = false;
    /***/
    private int mMode = 0;
    /**
     * 线性颜色百分比界限
     */
    private int[] mRectLimitNum;
    /**
     * 线性颜色百分比对应颜色
     */
    private int[] mRectLimitColor;
    /**
     * 背景色
     */
    private int mBackgroundColor;

    private float mStrokeWidth = 30;

    private int childOffX = 0;

    private int childOffY = 0;

    public WaterViewGroupEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public WaterViewGroupEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.WaterViewGroup);
        drawable = tArray.getDrawable(R.styleable.WaterViewGroup_src);
        type = tArray.getInt(R.styleable.WaterViewGroup_type_draw, TYPE_DRAW_ARC);
        mFrameTime = tArray.getInt(R.styleable.WaterViewGroup_time_frame, 20);
        mSpanPercent = tArray.getInt(R.styleable.WaterViewGroup_span_percent, 1);
        mStartAngle = tArray.getInt(R.styleable.WaterViewGroup_angle_start, 0);
        mBackgroundColor = tArray.getInt(R.styleable.WaterViewGroup_background_color, 0xff000000);
        // mStrokeWidth = tArray.getInt(R.styleable.WaterViewGroup_stroke_width,
        // 30);

        mStrokeWidth = UtilTools.dip2px(context, tArray.getInt(R.styleable.WaterViewGroup_stroke_width, 20));
//		System.out.println("mStrokeWidth" + mStrokeWidth);
        childOffX = UtilTools.dip2px(context, tArray.getInt(R.styleable.WaterViewGroup_child_off_x, 0));
        childOffY = UtilTools.dip2px(context, tArray.getInt(R.styleable.WaterViewGroup_child_off_y, 0));

        mView = new MyView(context, attrs);
        addView(mView);
        mMode = MODE_INCREASE;
        span = mSpanPercent;
    }

    public WaterViewGroupEx(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = this.getChildAt(i);
            if (child instanceof MyView) {
                int w = getWidth();
                int h = getHeight();
                mWidth = w;
                mHeight = h;
                child.layout(0, 0, w, h);
            } else {// 居中显示
                int w = child.getMeasuredWidth();
                int h = child.getMeasuredHeight();
                int l1 = ((getWidth() - w) >> 1) + childOffX;
                int t1 = ((getHeight() - h) >> 1) + childOffY;
                int r1 = ((getWidth() - w) >> 1) + w + childOffX;
                int b1 = ((getHeight() - h) >> 1) + h + childOffY;
                child.layout(l1, t1, r1, b1);
            }
        }
    }

    /**
     * 设置线性填充界限（只对rect有效）
     *
     * @param limitNum   线性颜色百分比界限
     * @param limitColor 线性颜色百分比对应颜色
     */
    public void setRectLimit(int[] limitNum, int[] limitColor) {
        mRectLimitColor = limitColor;
        mRectLimitNum = limitNum;
    }

    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    /**
     * 设置扇形渐变色
     *
     * @param arcColors
     */
    public void setArcColor(int[] arcColors) {
        mView.setArcColors(arcColors);
    }

    public void init() {
        if (type == TYPE_DRAW_ARC) {
            mCurrentPosition = 0;
            mRange = 0;
        } else if (type == TYPE_DRAW_RECT) {
            mCurrentPosition = 100;
            mRange = 100;
        }

    }

    /**
     * 开始动画
     *
     * @param range
     */
    public void startAnimation(int range, int mode) {
        if (isDraw || mRange == range)
            return;
        mMode = mode;
        if (mMode == MODE_INCREASE) {
            span = mSpanPercent;
        } else if (mMode == MODE_DECREASE) {
            span = -mSpanPercent;
        }
        mRange = range;
        mView.start();
    }

    /**
     * 开始动画
     *
     * @param range
     */
    public void startAnimation(int range) {
        if (isDraw || mRange == range)
            return;
        if (mRange < range) {
            mMode = MODE_INCREASE;
            span = mSpanPercent;
        } else if (mRange > range) {
            mMode = MODE_DECREASE;
            span = -mSpanPercent;
        }
        mRange = range;
        mView.start();
    }

    class MyView extends View {

        private Paint mPaint = null;

        private Timer mTask;

        private Shader mShader;

        private RectF rect;

        private Paint p = null;

        private Paint cPaint = null;
        private RectF topClipRect, bottomClipRect;
        private RectF crect = new RectF(0, 0, 0, 0);

        public MyView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
            init(context);
        }

        public MyView(Context context, AttributeSet attrs) {
            super(context, attrs);
            // TODO Auto-generated constructor stub
            init(context);
        }

        public MyView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            init(context);
        }

        private void init(Context context) {
            mPaint = new Paint();
            cPaint = new Paint();
            mPaint.setAntiAlias(true);
            cPaint.setAntiAlias(true);
            if (type == TYPE_DRAW_ARC) {
                cPaint.setColor(0xccffffff);
                cPaint.setStyle(Paint.Style.FILL);

                mPaint.setColor(0xccffffff);
                mCurrentPosition = 0;
                mRange = 0;
                mPaint.setStrokeWidth(UtilTools.px2dip(context, mStrokeWidth));
                mPaint.setStyle(Paint.Style.STROKE);

            } else if (type == TYPE_DRAW_RECT) {
                mRectLimitNum = new int[]{100, 79, 30};
                mRectLimitColor = new int[]{0xff35bdb2, 0xfff7b32e, 0xffff5777};
                mCurrentPosition = 100;
                mRange = 100;
            }
            p = new Paint();
            p.setColor(0x19000000);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(UtilTools.px2dip(context, mStrokeWidth));
            p.setAntiAlias(true);
        }

        private void setArcColors(int[] colors) {
            mShader = new SweepGradient(mWidth / 2, mWidth / 2, colors, null);
            mPaint.setShader(mShader);
            cPaint.setShader(mShader);
        }

        public void start() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new Timer();
            try {
                mTask.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        mCurrentPosition += span;
                        isDraw = true;
                        if (mMode == MODE_INCREASE) {
                            if (mRange < mOldRange) {
                                if (mCurrentPosition >= 100) {
                                    mCurrentPosition -= 100;
                                    mOldRange = mRange;
                                }
                            } else {
                                if (mCurrentPosition >= mRange) {
                                    mOldRange = mRange;
                                    if (mTask != null) {
                                        mTask.cancel();
                                        mTask = null;
                                    }
                                    isDraw = false;
                                }
                            }
                        } else if (mMode == MODE_DECREASE) {
                            if (mCurrentPosition <= 0)
                                mCurrentPosition = 0;
                            if (mCurrentPosition <= mRange) {
                                mOldRange = mRange;
                                if (mTask != null) {
                                    mTask.cancel();
                                    mTask = null;
                                }
                                isDraw = false;
                            }
                        }
                        postInvalidate();
                    }
                }, mFrameTime, mFrameTime);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(mBackgroundColor);
            if (type == TYPE_DRAW_ARC) {
                if (rect == null) {
                    rect = new RectF(0 + (mStrokeWidth / 2), 0 + (mStrokeWidth / 2), mWidth - (mStrokeWidth / 2),
                            mHeight - (mStrokeWidth / 2));
                }
                canvas.drawArc(rect, 0, 360, false, p);

                if (mStartAngle == 0) {
                    if (topClipRect == null) {
                        mPaint.setStrokeJoin(Paint.Join.ROUND);
                        mPaint.setStrokeCap(Paint.Cap.ROUND);
                        topClipRect = new RectF(0, 0, mWidth, mWidth / 2);
                        bottomClipRect = new RectF(0, mWidth / 2, mWidth, mWidth);
                    }
                    float sweepAngle = 360f * mCurrentPosition / 100;

                    canvas.save();
                    canvas.clipRect(bottomClipRect);
                    canvas.drawArc(rect, 0, sweepAngle, false, mPaint);
                    canvas.restore();

                    if (sweepAngle - 180 >= 0) {
                        float angle = sweepAngle - 180 == 0 ? 0.1f : sweepAngle - 180;
                        canvas.save();
                        canvas.clipRect(topClipRect);
                        canvas.drawArc(rect, 180, angle, false, mPaint);
                        canvas.restore();
                    }
                } else {
                    float x = (float) Math.cos(Math.toRadians(360 * mCurrentPosition / 100))
                            * ((mWidth / 2) - (mStrokeWidth / 2)) + (mWidth / 2);
                    float y = (float) Math.sin(Math.toRadians(360 * mCurrentPosition / 100))
                            * ((mWidth / 2) - (mStrokeWidth / 2)) + (mWidth / 2);
                    crect.left = x - (mStrokeWidth / 4);
                    crect.top = y - (mStrokeWidth / 4);
                    crect.right = x + (mStrokeWidth / 4);
                    crect.bottom = y + (mStrokeWidth / 4);
                    // 小圆
                    canvas.drawArc(crect, 360f * mCurrentPosition / 100, 180, true, cPaint);
                    // 圆弧
                    canvas.drawArc(rect, mStartAngle, 360f * mCurrentPosition / 100, false, mPaint);
                }
            } else if (type == TYPE_DRAW_RECT) {
                for (int i = 0; i < mRectLimitNum.length; i++) {
                    if (mCurrentPosition <= mRectLimitNum[i]) {
                        mPaint.setColor(mRectLimitColor[i]);
                    }
                }
                canvas.drawRect(0, (100 - mCurrentPosition) * mHeight / 100, mWidth, mHeight, mPaint);
            }

            if (drawable != null)
                canvas.drawBitmap(((BitmapDrawable) drawable).getBitmap(), 0, 0, mPaint);
        }
    }
}
