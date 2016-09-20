package com.mapbar.android.obd.rearview.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhangyunfei on 16/8/9.
 */
public class MyFileUtil {


    public static String readAll(InputStream inputStream) {
        if (inputStream == null)
            return null;
        //得到数据的大小
        int length = 0;
        try {
            length = inputStream.available();
            byte[] buffer = new byte[length];
            //读取数据
            inputStream.read(buffer);
            //依test.txt的编码类型选择合适的编码，如果不调整会乱码
            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
