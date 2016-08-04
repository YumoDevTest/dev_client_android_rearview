package com.mapbar.android.obd.rearview.modules.permission;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.BuildConfig;
import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.common.QRUtils;
import com.mapbar.android.obd.rearview.framework.common.Utils;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.android.obd.rearview.obd.util.QrBarcodeUtils;
import com.mapbar.obd.UserCenter;

import java.security.InvalidParameterException;

/**
 * 权限提醒。两个状态： 1.试用中剩余多少天。2.试用过期
 * 包含一个二维码，剩余多少天，点击继续按钮
 * Created by zhangyunfei on 16/8/3.
 */
public class PermissionAlertView extends FrameLayout {
    private LayoutInflater layoutInflater;
    private boolean expired;
    private int numberOfDay;
    private TextView subtitle;
    private TextView title;
    private ImageView barcodeView;
    private Button btn_continue_try;

    public PermissionAlertView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PermissionAlertView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PermissionAlertView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        layoutInflater = LayoutInflater.from(context);
        View content = layoutInflater.inflate(R.layout.permission_alert_, null);
        this.addView(content, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        title = (TextView) findViewById(R.id.title);
        subtitle = (TextView) findViewById(R.id.subtitle);
        btn_continue_try = (Button) findViewById(R.id.btn_continue_try);
        barcodeView = (ImageView) findViewById(R.id.barcodeView);
        showQrBarcodeView();
        setClickable(true);
    }

    /**
     * 是否过期,是否 超出试用期
     *
     * @param expired
     */
    public void setExpired(boolean expired) {
        this.expired = expired;
        if (expired) {
            title.setVisibility(View.GONE);//大标题 不可见
            btn_continue_try.setVisibility(View.GONE);//继续试用按钮 不可见
            subtitle.setText(getExpiredText());
        }
    }

    /**
     * 剩余天数
     *
     * @param numberOfDay
     */
    public void setNumberOfDay(int numberOfDay) {
        if (expired) {
            subtitle.setText(getExpiredText());
            return;
        }
        if (numberOfDay < 0) {
            throw new InvalidParameterException("天数不能小于0");
        }
        this.numberOfDay = numberOfDay;
        subtitle.setText(Html.fromHtml("您还能免费试用OBD功能<font color='red'>" + numberOfDay + "天</font>"));
    }

    /**
     * 继续试用按钮的点击事件
     *
     * @param onClickListener
     */
    public void setOnContinueTryButtonClickListener(OnClickListener onClickListener) {
        btn_continue_try.setOnClickListener(onClickListener);
    }

    /**
     * 获得 过期 的提示语
     *
     * @return
     */
    private Spanned getExpiredText() {
        return Html.fromHtml("亲，很抱歉的通知您，OBD功能<font color='red'>使用权过期啦!</font>");
    }

    /**
     * 设置 二维码
     *
     * @param drawable
     */
    private void setQrBarcodeImage(Drawable drawable) {
//        barcodeView.setCompoundDrawables(null,top,null,null);
        barcodeView.setImageDrawable(drawable);
    }


    private void showQrBarcodeView() {
        new AsyncTask<Void, Integer, BitmapDrawable>() {
            @Override
            protected BitmapDrawable doInBackground(Void... voids) {
                String url = buildQrContentUrl();
                Bitmap bmQR = QrBarcodeUtils.createQrBitmap(url, 344, 344);
                return new BitmapDrawable(bmQR);
            }

            @Override
            protected void onPostExecute(BitmapDrawable bitmapDrawable) {
                setQrBarcodeImage(bitmapDrawable);
            }
        }.execute();
    }

    private static String buildQrContentUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(Configs.URL_REG_INFO).append("imei=").append(Utils.getImei(MainActivity.getInstance())).append("&");
        sb.append("pushToken=").append(AixintuiConfigs.push_token);
        return sb.toString();
    }
}
