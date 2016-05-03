package com.mapbar.android.obd.rearview.framework.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by THINKPAD on 2016/2/18.
 */
public class ImageLoaderUtil {

    public static Bitmap displayImageFromAssetsFile(Context context, String fileName, ImageView imageView) {
        Bitmap image = null;

        String imageUri = "assets://" + fileName; // from assets

        ImageLoader.getInstance().displayImage(imageUri, imageView);

        return image;
    }
}
