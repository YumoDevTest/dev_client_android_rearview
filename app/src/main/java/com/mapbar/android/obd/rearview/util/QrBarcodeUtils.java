package com.mapbar.android.obd.rearview.util;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;

import java.util.Hashtable;

/**
 * Created by zhangyf on 2016-08-03
 */
public class QrBarcodeUtils {

    /**
     *  构建二维码图像，通过字符串
     * @param content url
     * @param width  图像宽度
     * @param height 图像高度
     * @return
     */
    public static Bitmap createQrBitmap(String content,int width,int height) {
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
}
