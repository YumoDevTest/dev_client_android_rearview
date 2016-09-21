package com.mapbar.android.obd.rearview.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.mapbar.obd.foundation.log.LogUtil;

/**
 * 布局工具类
 */
public class LayoutUtils_ui {

    private static final int CENTER = 0;
    private static final int CENTER_VERTICAL = 2;
    private static final int CENTER_HORIZONTAL = 1;

    private static final int WIDTH = 0;
    private static final int HEIGHT = 1;

    /**
     * 横屏补充用 FLAG
     */
    private static final int ITEM_TYPE_LANDSCAPE_FLAG = 1;

    /**
     * 屏幕宽度所对应的dp个数
     */
    private static final float WIDTH_DP_COUNT = 360;

    /**
     * 宽高
     */
    private static int[] wh = null;

    /**
     * 调整后的 dp 与 px 的比值
     */
    private static float adjustedDensity = 0;

    /**
     * 通过本方法使得所有分辨率下均以屏幕宽度除以 360 个 dp 的方式来确定每个 dp 对应多少 px <br>
     * 也就是说 36dp 将含义将变为屏幕宽度的三百六十分之三十六，也就是十分之一<br>
     * UI 线程限定
     */
    public static void proportional(Activity activity) {
        if (activity == null)
            throw new NullPointerException();
        float currentEnvironmentDensity = activity.getResources().getDisplayMetrics().density;// 当前环境密度
        LogUtil.d("proportional", activity.getResources().getDisplayMetrics().toString());
        if (adjustedDensity == currentEnvironmentDensity) {// 如果已经正确
            return;
        }

        int width = getScreenWH(activity)[WIDTH];

        width = correctWidth(width, currentEnvironmentDensity);// 纠正宽度（未来觉得不合适时再考虑使用）

        changeDensity(activity, width / WIDTH_DP_COUNT);
    }

    /**
     * 纠正宽度
     *
     * @param width
     * @param density
     * @return
     */
    private static int correctWidth(int width, float density) {
        int statusBarHeight = (int) (density * 48);// 状态栏高度（一般48dp）
        int possibleWidth = width + statusBarHeight;// 可能的高度
        switch (possibleWidth) {
            case 320:
            case 480:
            case 600:
            case 640:
            case 720:
            case 1080:
            case 1440:
                return possibleWidth;
        }
        return width;
    }

    /**
     * 更改 dp 与 px 的转换比值<br>
     * UI 线程限定
     *
     * @param density 目标密度
     */
    private static void changeDensity(Activity activity, float density) {

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();

        if (density == displayMetrics.density) {// 如果已经正确
            adjustedDensity = density;
            return;
        }

        float ratio = density / displayMetrics.density;// 计算变化比例
        displayMetrics.scaledDensity *= ratio;// 按变化比例修改
        displayMetrics.densityDpi *= ratio;// 按变化比例修改

        displayMetrics.density = density;// 直接修改

        adjustedDensity = density;// 记录调整后的 dp 与 px 的比值
    }

    /**
     * 转化为含横竖屏信息的 item type
     *
     * @param sourceItemType 原 item type
     * @param isLandscape    是否横屏
     * @return 含横竖屏信息的 item type
     */
    private static int landPortItemType(int sourceItemType, boolean isLandscape) {
        int landPortType = sourceItemType << 1;
        if (isLandscape) {
            landPortType |= ITEM_TYPE_LANDSCAPE_FLAG;
        } else {
            landPortType &= ~ITEM_TYPE_LANDSCAPE_FLAG;
        }
        return landPortType;
    }

    /**
     * 还原为原 item type
     *
     * @param landPortItemType 含横竖屏信息的 item type
     * @return 原 item type
     */
    private static int sourceItemType(int landPortItemType) {
        int sourceType = landPortItemType >> 1;
        return sourceType;
    }

    private static int dp2px(Activity activity, float dipValue) {
        // INFO 工具/dip转px
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private static int px2dp(Activity activity, float pxValue) {
        // INFO 工具/px转dip
        final float scale = activity.getResources().getDisplayMetrics().density;

        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    private static int px2sp(Activity activity, float pxValue) {
        final float fontScale = activity.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    private static int sp2px(Activity activity, float spValue) {
        final float fontScale = activity.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 注意使用常量取值！
     *
     * @return 宽总是短的，高总是长的
     */
    private static synchronized int[] getScreenWH(Activity context) {
        if (context == null) throw new NullPointerException();
        if (null != wh) {
            return wh;
        }

        wh = new int[2];

        WindowManager windowManager = context.getWindowManager();

        Display display = windowManager.getDefaultDisplay();
        mergeWH(display.getWidth(), display.getHeight());

        // since SDK_INT = 1;
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mergeWH(metrics.widthPixels, metrics.heightPixels);

        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                // includes window decorations (statusbar bar/menu bar)
                int a = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                int b = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
                mergeWH(a, b);
            } catch (Exception e) {
            }
        }

        if (Build.VERSION.SDK_INT >= 17) {
            // includes window decorations (statusbar bar/menu bar)
            Point realSize = new Point();
            display.getRealSize(realSize);
            mergeWH(realSize.x, realSize.y);
        }

        return wh;
    }

    /**
     * 1、判断哪个是宽哪个是高<br>
     * 2、宽高分别只留下最大值
     *
     * @param a
     * @param b
     */
    private static void mergeWH(int a, int b) {
        if (a < b) {
            wh[WIDTH] = Math.max(wh[WIDTH], a);
            wh[HEIGHT] = Math.max(wh[HEIGHT], b);
        } else {
            wh[WIDTH] = Math.max(wh[WIDTH], b);
            wh[HEIGHT] = Math.max(wh[HEIGHT], a);
        }
    }

    /**
     * 给定父矩形和子矩形的宽高，自动将子矩形修改为居中区域
     *
     * @param centerType 1:水平居中 2:垂直居中 0:全部居中
     */
    private static Rect getCenter(Rect outer, Rect inner, int centerType) {
        int w = inner.width();
        int h = inner.height();
        switch (centerType) {
            case CENTER:
                inner.top = (outer.height() - h) / 2 + outer.top;
                inner.bottom = inner.top + h;
            case CENTER_HORIZONTAL:
                inner.left = (outer.width() - w) / 2 + outer.left;
                inner.right = inner.left + w;
                break;
            case CENTER_VERTICAL:
                inner.top = (outer.height() - h) / 2 + outer.top;
                inner.bottom = inner.top + h;
                break;
        }
        return inner;
    }

    /**
     * 获取定义在 dimens 的尺寸
     *
     * @param id
     * @return 像素值
     */
    private static int getPxByDimens(Activity activity, int id) {
        return activity.getResources().getDimensionPixelSize(id);
    }

    /**
     * 获取定义在 dimens 的尺寸
     *
     * @param res
     * @param id
     * @return 像素值
     */
    private static int getPxByDimens(Resources res, int id) {
        return res.getDimensionPixelSize(id);
    }

    /**
     * 获取定义在 color 的值
     *
     * @param id
     * @return 颜色值
     */
    private static int getColorById(Activity activity, int id) {
        return activity.getResources().getColor(id);
    }

    /**
     * 对齐顶部绘制文字时拿 top 加上此距离，得出便是绘制文字所需的 baseline 的 y 值
     *
     * @param p 文字画笔
     * @return baseline 和 top 之间的距离
     */
    private static int distanceOfBaselineAndTop(TextPaint p) {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return -fontMetrics.ascent;
    }

    /**
     * 垂直居中绘制文字时拿 centerY 加上此距离，得出便是绘制文字所需的 baseline 的 y 值
     *
     * @param p 文字画笔
     * @return baseline 和 centerY 之间的距离
     */
    private static int distanceOfBaselineAndCenterY(TextPaint p) {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
    }

    /**
     * 对齐顶部绘制文字时拿 bottom 减去此距离，得出便是绘制文字所需的 baseline 的 y 值
     *
     * @param p 文字画笔
     * @return baseline 和 bottom 之间的距离
     */
    private static int distanceOfBaselineAndBottom(TextPaint p) {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return fontMetrics.descent;
    }

    /**
     * @param p 文字画笔
     * @return 文字高度
     */
    private static int textHeight(TextPaint p) {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return fontMetrics.descent - fontMetrics.ascent;
    }

    /**
     * 是否横屏
     *
     * @return
     */
    private static boolean isLandscape(Activity activity) {
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 获取横屏状态下，气泡面板的宽度
     *
     * @param context
     * @return
     */
    private static int getLandscapeAnnotationWdith(Context context) {
        return (int) (context.getResources().getDisplayMetrics().widthPixels * 0.46);
    }

    /**
     * 设置是否全屏显示
     *
     * @param isFull
     */
    private static void setFullScreen(Activity activity, boolean isFull) {
        Window window = activity.getWindow();
        if (isFull) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = window.getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(attr);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
}
