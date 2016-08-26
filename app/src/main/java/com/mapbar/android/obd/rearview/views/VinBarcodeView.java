package com.mapbar.android.obd.rearview.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.common.QRUtils;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.obd.Application;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.android.obd.rearview.obd.util.Urls;
import com.mapbar.obd.UserCenter;

/**
 * Vin 二维码的 view
 * Created by zhangyunfei on 16/8/26.
 */
public class VinBarcodeView extends FrameLayout {
    private ViewGroup contentView;
    private ImageView iv_qr;
    private TextView tv_qr_info;

    public VinBarcodeView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public VinBarcodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public VinBarcodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        contentView = (ViewGroup) layoutInflater.inflate(R.layout.vin_barcode_view, this);
        iv_qr = (ImageView) findViewById(R.id.iv_qr);
        tv_qr_info = (TextView) findViewById(R.id.tv_qr_info);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        this.addView(contentView, lp);

        showQrBarcode();
    }

    public void showQrBarcode() {
        if (TextUtils.isEmpty(AixintuiConfigs.getPushToken()))
            return;
        String content = "请扫描填写车辆识别号来扩展此页的\r车辆状态和控制功能";
        StringBuilder sb = new StringBuilder();
        sb.append(Urls.URL_BIND_VIN);
        sb.append("?pushToken=" + Application.getInstance().getPushToken());
        sb.append("&userId=" + Application.getInstance().getUserID());
        LogUtil.d("TAG", "## 准备生成 绑定VIN的URL " + sb.toString());
        if (iv_qr.getDrawable() == null) {
            Bitmap bmQR = QRUtils.createQR(sb.toString());
            iv_qr.setImageBitmap(bmQR);
        }
        setText(content);
    }


    public void setText(String text) {
        this.tv_qr_info.setText(text);
    }

    public void setText(int source) {
        tv_qr_info.setText(source);
    }
}
