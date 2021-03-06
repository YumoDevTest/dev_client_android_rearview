package com.mapbar.android.obd.rearview.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.android.obd.rearview.modules.common.MainActivity;
import com.mapbar.android.obd.rearview.views.ProgressView;
import com.mapbar.obd.foundation.log.LogUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by liuyy on 2016/1/17.
 */
public class LayoutUtils {
    private static ProgressView pView;
    private static String url;
    private static TextView tv;

    public static Rect getScreenArea() {
        Context context = MyApplication.getInstance().getMainActivity();
        if (context == null) throw new NullPointerException();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return new Rect(0, 0, width, height);
    }

    public static float getDpi() {
        Context context = MyApplication.getInstance().getMainActivity();
        if (context == null) throw new NullPointerException();
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        // 日志
        if (Log.isLoggable(LogTag.FRAMEWORK, Log.VERBOSE)) {
            Log.v(LogTag.FRAMEWORK, "dpi -->> " + dm.densityDpi);
        }
        return dm.densityDpi;
    }

    public static float getDensity() {
        Context context = MyApplication.getInstance().getMainActivity();
        if (context == null) throw new NullPointerException();
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        // 日志
        if (Log.isLoggable(LogTag.FRAMEWORK, Log.VERBOSE)) {
            Log.v(LogTag.FRAMEWORK, "dpi -->> " + dm.densityDpi);
        }
        return dm.density;
    }

    public static int getDimenPx(int id) {
        return (int) MyApplication.getInstance().getResources().getDimension(id);
    }

    public static int dip2px(float dipValue) {
        final float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void showDialog(Activity activity, int resIdTitle, int resIdText, int icon) {
        Resources resources = MyApplication.getInstance().getResources();
        showDialog(activity, resources.getString(resIdTitle), resources.getString(resIdText), icon);
    }

    /**
     * 弹出系统的对话框
     *
     * @param title
     * @param msg
     * @param icon
     */
    public static void showDialog(Activity activity, String title, String msg, int icon) {
        AlertDialog.Builder tDialog = new AlertDialog.Builder(activity);
        tDialog.setIcon(icon);
        tDialog.setTitle(title);
        tDialog.setMessage(msg);
        tDialog.setPositiveButton(MyApplication.getInstance().getResources().getString(R.string.ok), null);
        tDialog.show();
    }

//    /**
//     * 弹出自定义的PopupWindow
//     *
//     * @param view    弹出的内容view
//     * @param gravity {@link Gravity#TOP 等等}
//     */
//    public static void showPopWindow(PageManager pageManager,View view, int gravity) {
//        PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        ArrayList<AppPage> pages = pageManager.getPages();
//        if (pages.size() > 0) {
//            popupWindow.showAtLocation(pages.get(pages.size() - 1).getContentView(), gravity, 0, 0);
//        }
//    }

    /**
     * 弹出对话框
     *  @param title           标题
     * @param msg             信息
     * @param confirmListener 确定事件
     */
    public static PopupWindow showPopWindow(MainActivity activity, String title, String msg, View.OnClickListener confirmListener) {
        return showPopWindow(activity, title, msg, null, null, TipsType.OK_CANCEL, confirmListener, null);
    }

    public static PopupWindow showPopWindow(MainActivity activity, String title, String msg, View.OnClickListener confirmListener, View containt) {
        return showPopWindow(activity, title, msg, null, null, TipsType.OK_CANCEL, confirmListener, containt);
    }

    /**
     * 弹出对话框
     *
     * @param title           标题
     * @param msg             信息
     * @param confirm         确定按钮文字
     * @param cancel          取消按钮文字
     * @param type            按钮类型
     * @param confirmListener 确定事件
     */
    public static PopupWindow showPopWindow(MainActivity activity, String title, String msg, String confirm, String cancel, TipsType type, View.OnClickListener confirmListener, View containt) {
//        MainActivity activity = MyApplication.getInstance().getMainActivity();
//        if (activity == null) throw new NullPointerException();
        View view = View.inflate(MyApplication.getInstance(), R.layout.layout_dialog, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_tips_title);
        TextView tv_msg = (TextView) view.findViewById(R.id.tv_tips_info);
        TextView tv_confirm = (TextView) view.findViewById(R.id.tv_tips_confirm);
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_tips_cancel);
        View v_mid = view.findViewById(R.id.v_tips_mid);
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        }

        if (!TextUtils.isEmpty(msg)) {
            tv_msg.setText(msg);
        }
        if (!TextUtils.isEmpty(confirm)) {
            tv_confirm.setText(confirm);
        }
        if (!TextUtils.isEmpty(cancel)) {
            tv_cancel.setText(cancel);
        }
        switch (type) {
            case ONLY_OK:
                tv_cancel.setVisibility(View.GONE);
                v_mid.setVisibility(View.GONE);
                break;
            case ONLY_CANCEL:
                tv_confirm.setVisibility(View.GONE);
                v_mid.setVisibility(View.GONE);
                break;
        }
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        int removeW = 0;
        if (getScreenArea().width() != activity.getContentView().getWidth()) {
            removeW = getScreenArea().width() / 2 - activity.getContentView().getWidth() / 2 - getViewLocationOnScreen(activity.getContentView())[0];
        }
        if (containt == null) {
            popupWindow.showAtLocation(activity.getContentView(), Gravity.CENTER, -removeW, 0);
        } else {
            popupWindow.showAtLocation(containt, Gravity.CENTER, -removeW, 0);
        }

//        }

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null)
                    popupWindow.dismiss();
            }
        };
        tv_cancel.setOnClickListener(listener);
        tv_confirm.setOnClickListener(listener);
        if (confirmListener != null) {
            tv_confirm.setOnClickListener(confirmListener);
        }
        return popupWindow;
    }

    public static PopupWindow showQrPop(MainActivity activity, String content, String info, View.OnClickListener onClickListener) {

        url = content;

        View view = View.inflate(MyApplication.getInstance(), R.layout.layout_qr_dialog, null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_qr);
        Button btn_state_pop_close = (Button) view.findViewById(R.id.btn_close_QRpop);
        btn_state_pop_close.setOnClickListener(onClickListener);
        Bitmap bmQR = QRUtils.createQR(content);
        iv.setImageBitmap(bmQR);
        tv = (TextView) view.findViewById(R.id.tv_qr_info);
        tv.setText(info);
        PopupWindow popupQR = new PopupWindow(view, activity.getContentView().getWidth(), activity.getContentView().getHeight(), true);
        popupQR.showAtLocation(activity.getContentView(), Gravity.LEFT, getViewLocationOnScreen(activity.getContentView())[0], 0);
        return popupQR;
    }

    public static int[] getViewLocationOnScreen(View view) {
        int[] ints = new int[2];
        //获取控件所在位置
        view.getLocationOnScreen(ints);
        return ints;
    }

    /**
     * 对齐顶部绘制文字时拿 top 加上此距离，得出便是绘制文字所需的 baseline 的 y 值
     *
     * @param p 文字画笔
     * @return baseline 和 top 之间的距离
     */
    public static int distanceOfBaselineAndTop(TextPaint p) {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return -fontMetrics.ascent;
    }

    /**
     * 垂直居中绘制文字时拿 centerY 加上此距离，得出便是绘制文字所需的 baseline 的 y 值
     *
     * @param p 文字画笔
     * @return baseline 和 centerY 之间的距离
     */
    public static int distanceOfBaselineAndCenterY(TextPaint p) {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
    }

    /**
     * 对齐顶部绘制文字时拿 bottom 减去此距离，得出便是绘制文字所需的 baseline 的 y 值
     *
     * @param p 文字画笔
     * @return baseline 和 bottom 之间的距离
     */
    public static int distanceOfBaselineAndBottom(TextPaint p) {
        Paint.FontMetricsInt fontMetrics = p.getFontMetricsInt();
        return fontMetrics.descent;
    }

    public static void showHud(Activity activity, String content) {
        pView = new ProgressView(activity, content);
        pView.setOnCancelListener(new ProgressView.OnCancelListener() {
            @Override
            public void onCancel(ProgressView progressView) {
                // 日志
                if (Log.isLoggable(LogTag.FRAMEWORK, Log.VERBOSE)) {
                    Log.v(LogTag.FRAMEWORK, "showHud -->> cancel HUD");
                }
            }
        });
        pView.show();
    }

    public static void disHud() {
        if (pView != null)
            pView.dismiss();
    }

    public static Rect reduceRect(Rect rect) {
        final int reduceSize = 50;
        return new Rect(rect.left + reduceSize, rect.top + reduceSize, rect.right - reduceSize, rect.bottom - reduceSize);
    }

    /**
     * ListView自适应宽高
     *
     * @param listView
     */
    public static void setListViewBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int maxWidth = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            int width = listItem.getMeasuredWidth();
            if (width > maxWidth) maxWidth = width;
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        params.width = maxWidth;
        listView.setLayoutParams(params);
    }

    /**
     * 截图
     *
     * @param scrollView
     * @return 截图并且保存sdcard成功返回true，否则返回false
     */
    public static boolean shotBitmap(ScrollView scrollView, String sharePath) {

        int h = 0;
        Bitmap bitmap = null;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundResource(R.drawable.add_device_bg);
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        // 测试输出
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(sharePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 截图
     *
     * @param view
     * @return 截图并且保存sdcard成功返回true，否则返回false
     */
    public static void shotBitmap(View view, String sharePath) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap != null) {
            try {
                FileOutputStream out = new FileOutputStream(sharePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LogUtil.d("LyoutUtils","bitmap is NULL !");


        }
    }

    public static void closeInput() {
        Activity context = MyApplication.getInstance().getMainActivity();
        if (context == null) throw new NullPointerException();
        try {
            //没有弹出输入法时，报空指针，临时解决方案 FIXME 以后再研究
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹出对话框的类型
     */
    public enum TipsType {
        ONLY_OK, ONLY_CANCEL, OK_CANCEL
    }
}
