package com.mapbar.android.obd.rearview.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Patterns;

import com.mapbar.android.log.Log;
import com.mapbar.android.log.LogTag;
import com.mapbar.android.obd.rearview.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by THINKPAD on 2016/5/11.
 */
public class UtilTools {

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param startColor
     * @param endColor
     * @param per        变化百分比（0-100）
     * @return 当前颜色值
     */
    public static int getColor(int startColor, int endColor, int per) {

        int r_start = (startColor >> 16) & 0xff;
        int g_start = (startColor >> 8) & 0xff;
        int b_start = (startColor) & 0xff;
        int r_end = (endColor >> 16) & 0xff;
        int g_end = (endColor >> 8) & 0xff;
        int b_end = (endColor) & 0xff;

        int r_step = (r_end - r_start) * per / 100;
        int g_step = (g_end - g_start) * per / 100;
        int b_step = (b_end - b_start) * per / 100;

        int r_current = r_start + r_step;
        int g_current = g_start + g_step;
        int b_current = b_start + b_step;
        return Color.rgb(r_current, g_current, b_current);
    }

    public static boolean isBluetooth() {
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mAdapter == null) {
            return false;
        }
        return mAdapter.isEnabled();
    }

    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return image;
    }

    public static boolean isMobileNum(String str) {
        Pattern p = Pattern.compile("[1][0-9]\\d{9}");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isEmail(String str) {
        Pattern p = Patterns.EMAIL_ADDRESS;
        Matcher m = p.matcher(str);
        return m.matches();
    }


    public static void saveParaToBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getParaFromBoolean(Context context, String key) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        boolean value = sp.getBoolean(key, false);
        return value;
    }


    /**
     * 获取assets复制的静态网页路径
     *
     * @param context
     * @return
     */
    public static String getAssetsHtmlUrl(Context context) {
        try {
            File file = new File(context.getFilesDir().getAbsolutePath() + "/OTADefault");
            String otaPath = file.listFiles()[0].getName() + "/index.html";
            String asshtmlPath = file.getAbsolutePath() + "/" + otaPath;
            if (!new File(asshtmlPath).exists()) {
                //TODO
            }
            return asshtmlPath;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取下载html路径
     *
     * @param context
     * @return
     */
    public static String getLocalHtmlUrl(Context context) {
        try {
            File file = new File(context.getFilesDir().getAbsolutePath() + "/OTA");
            String otaPath = file.listFiles()[0].getName() + "/carSettingPage/index.html";
            String htmlPath = file.getAbsolutePath() + "/" + otaPath;
            if (!new File(htmlPath).exists()) {
                //TODO
            }
            return htmlPath;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 判断文件是否存在
     *
     * @return
     */
    public static boolean fileIsExists(String path) {
        // 日志
        if (Log.isLoggable(LogTag.OTA, Log.DEBUG)) {
            Log.d(LogTag.OTA, " path-->> " + path);
        }
        File f = new File(path);
        return f.exists();
    }

    /**
     * 跳转activity
     *
     * @param intent
     * @param context
     */
    public static void startNextActivity(Intent intent, Activity context) {
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    /**
     * 退出Activity
     *
     * @param activity Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
        }
    }


}
