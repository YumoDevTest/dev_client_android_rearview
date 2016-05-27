package com.mapbar.android.obd.rearview.framework.common;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

/**
 * Created by liuyy on 2016/5/26.
 */
public class QRUtils {

    public static Bitmap createQR(String content, int size) {
        int width = size; // 图像宽度
        int height = size; // 图像高度
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = null;// 生成矩阵
        try {
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (bitMatrix != null) {
            width = bitMatrix.width;
            height = bitMatrix.height;
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
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.setPixels(pixels, 0, width, 0, 0, width, height);
            return bmp;
        } else {
            return null;
        }
    }


}
