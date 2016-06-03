package com.mapbar.android.obd.rearview.framework.common;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mapbar.android.obd.rearview.framework.Configs;
import com.mapbar.android.obd.rearview.framework.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.obd.UserCenter;

import java.util.Hashtable;

/**
 * Created by liuyy on 2016/5/26.
 */
public class QRUtils {

    public static Bitmap createQR(String content) {
        int width = 256; // 图像宽度
        int height = 256; // 图像高度
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0);  //二维码边框宽度，这里文档说设置0-4，但是设置后没有效果，不知原因，
        BitMatrix bitMatrix = null;// 生成矩阵
        try {
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int margin = 0;  //自定义白边边框宽度

        bitMatrix = updateBit(bitMatrix, margin);  //生成新的bitMatrix
        if (bitMatrix != null) {
            width = bitMatrix.getWidth();
            // 日志
            if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
                Log.d(LogTag.OBD, "width -->> " + width);
            }
            height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < width; ++y) {
                for (int x = 0; x < height; ++x) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000; // black pixel
                    } else {
                        pixels[y * width + x] = 0xffffffff; // white pixel
                    }
                }
            }
            Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            bmp.setPixels(pixels, 0, width, 0, 0, width, width);
            return bmp;
        } else {
            return null;
        }
    }

    private static BitMatrix updateBit(BitMatrix matrix, int margin) {

        int tempM = margin * 2;

        int[] rec = matrix.getEnclosingRectangle();   //获取二维码图案的属性

        int resWidth = rec[2] + tempM;

        int resHeight = rec[3] + tempM;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix

        resMatrix.clear();

        for (int i = margin; i < resWidth - margin; i++) {   //循环，将二维码图案绘制到新的bitMatrix中

            for (int j = margin; j < resHeight - margin; j++) {

                if (matrix.get(i - margin + rec[0], j - margin + rec[1])) {

                    resMatrix.set(i, j);

                }

            }

        }
        return resMatrix;
    }


    /**
     * 弹出二维码
     */
    public static void showRegQr(String info) {
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, " -->> 弹出二维码");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Configs.URL_REG_INFO).append("imei=").append(Utils.getImei()).append("&");
        sb.append("pushToken=").append(AixintuiConfigs.push_token).append("&");
        sb.append("token=").append(UserCenter.getInstance().getCurrentUserToken());
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, "url_info -->> " + sb.toString());
        }
//        String url = Configs.URL_REG1 + "&redirect_uri=" + Uri.encode(sb.toString()) + Configs.URL_REG2;
        String url = sb.toString();
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, "url -->> " + url);
        }
        LayoutUtils.showQrPop(url, info);
    }
}
