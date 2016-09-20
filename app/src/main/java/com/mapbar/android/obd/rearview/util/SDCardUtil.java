package com.mapbar.android.obd.rearview.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class SDCardUtil {

    /**
     * 判断两个路径是否相同时使用，/mnt+sd1与sd2相同或者/mnt+sd2与sd1相同时，认为sd1与sd2 相同
     */
    private static final String mVirtualHeader = "/mnt";
    /**
     * 内置存储卡存储目录 sd1
     */
    private static String sdCardPath;
    /**
     * 外置存储卡存储目录 sd2
     */
    private static String extSdCardPath;
    /**
     * 可能存在的外置存储卡的路径集合
     */
    private static List<String> mSdcard2Paths = new LinkedList<String>();

    /**
     * 初始化内置SD卡的路径
     *
     * @param context
     */
    protected static void initSDCardPath(Context context) {
        if (TextUtils.isEmpty(sdCardPath)) {
            if (Build.VERSION.SDK_INT < 19) {
                sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else {
                try {
                    File[] files = ContextCompat.getExternalFilesDirs(context, null);
                    if (files != null) {
                        if (files.length > 0 && files[0] != null) {
                            sdCardPath = resetPath(context, files[0].getAbsolutePath());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                }
            }
        }
    }

    /**
     * 返回可读写的内置存储卡的位置，
     *
     * @param context
     * @param path
     * @return
     */
    private static final String resetPath(Context context, String path) {
        // ContextCompat.getExternalFilesDirs方法返回文件集合：内置sd卡路径/Android/data/+包名+files，
        String tempPath = path.replace("/Android/data/" + context.getPackageName() + "/files", "");
        File file = new File(tempPath, System.currentTimeMillis() + ".txt");
        try {
            if (file.createNewFile()) {
                file.delete();
                return tempPath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 初始化外置存储卡的路径
     *
     * @param context
     */
    @SuppressLint("SdCardPath")
    protected static void initExtSdCardPath(Context context) {
        // 3.2及以上SDK识别路径
        mSdcard2Paths = getSdcard2Paths(context);
        mSdcard2Paths.add("/mnt/emmc");
        mSdcard2Paths.add("/mnt/extsdcard");
        mSdcard2Paths.add("/mnt/ext_sdcard");
        mSdcard2Paths.add("/sdcard-ext");
        mSdcard2Paths.add("/mnt/sdcard-ext");
        mSdcard2Paths.add("/sdcard2");
        mSdcard2Paths.add("/sdcard");
        mSdcard2Paths.add("/mnt/sdcard2");
        mSdcard2Paths.add("/mnt/sdcard");
        mSdcard2Paths.add("/sdcard/sd");
        mSdcard2Paths.add("/sdcard/external");
        mSdcard2Paths.add("/flash");
        mSdcard2Paths.add("/mnt/flash");
        mSdcard2Paths.add("/mnt/sdcard/external_sd");

        mSdcard2Paths.add("/mnt/external1");
        mSdcard2Paths.add("/mnt/sdcard/extra_sd");
        mSdcard2Paths.add("/mnt/sdcard/_ExternalSD");
        mSdcard2Paths.add("/mnt/extrasd_bin");
        // 4.1SDK 识别路径
        mSdcard2Paths.add("/storage/extSdCard");
        mSdcard2Paths.add("/storage/sdcard0");
        mSdcard2Paths.add("/storage/sdcard1");
        initSdcard2(context);
    }

    @SuppressLint("InlinedApi")
    private static List<String> getSdcard2Paths(Context context) {
        List<String> paths = new LinkedList<String>();
        if (Build.VERSION.SDK_INT < 13) {
            return paths;
        }

        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<? extends StorageManager> clazz = sm.getClass();
            Method mlist = clazz.getMethod("getVolumeList", (Class[]) null);
            Class<?> cstrvol = Class.forName("android.os.storage.StorageVolume");
            Method mvol = cstrvol.getMethod("getPath", (Class[]) null);
            Object[] objects = (Object[]) mlist.invoke(sm);
            if (objects != null && objects.length > 0) {
                for (Object obj : objects) {
                    paths.add((String) mvol.invoke(obj));
                }
            }
        } catch (Exception e) {
        }
        return paths;
    }

    /**
     * 判断不同于内置存储卡的，路径中哪个存在，找到第一个存在的非内置卡的路径作为外卡路径
     */
    private static void initSdcard2(Context context) {
        int count = mSdcard2Paths.size();
        String dataPath = "/Android/data/" + context.getPackageName() + "/files";
        for (int index = 0; index < count; index++) {
            boolean isSame = isSamePath(sdCardPath, mSdcard2Paths.get(index));
            if (isSame) {
                continue;
            }
            boolean isExsits = isExsitsPath(mSdcard2Paths.get(index));
            if (isExsits && !isSameSdcard(sdCardPath, mSdcard2Paths.get(index))) {
                extSdCardPath = mSdcard2Paths.get(index);
                break;
            }
        }
        if (!TextUtils.isEmpty(extSdCardPath)) {
            File file = new File(extSdCardPath, System.currentTimeMillis() + ".txt");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!file.exists()) {
                file = new File(extSdCardPath + dataPath, System.currentTimeMillis() + ".txt");
                try {
                    if (file.createNewFile()) {
                        file.delete();
                        extSdCardPath += dataPath;
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else {
                file.delete();
            }
        }
    }

    /**
     * 判断该路径的目录是否存在，并且可写
     *
     * @param path
     * @return
     */
    private static boolean isExsitsPath(String path) {
        File f = new File(path);
        if (f.exists() && f.canWrite()) {
            return true;
        }
        return false;
    }

    /**
     * 判断两个SD卡路径是否相同
     *
     * @param path
     * @param path2
     * @return
     */
    private static boolean isSamePath(String path, String path2) {
        // 名称有空则认为一样
        if (isNullOrEmptyOrSpace(path) || isNullOrEmptyOrSpace(path2)) {
            return true;
        }
        // 一样
        if (path.trim().toLowerCase().equals(path2.trim().toLowerCase())) {
            return true;
        }
        // 添加/mnt
        if (path2.trim().toLowerCase().equals((mVirtualHeader + path).trim().toLowerCase())) {
            return true;
        }
        // 添加/mnt
        if (path.trim().toLowerCase().equals((mVirtualHeader + path2).trim().toLowerCase())) {
            return true;
        }

        return false;
    }

    /**
     * 判断是否为NULL或者空或者空格
     *
     * @param str 判断字符串
     * @return 是则为NULL或者空或者空格
     */
    private static boolean isNullOrEmptyOrSpace(String str) {
        if (null == str || "".equals(str.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 判断两个SD卡路径所对应的卡是否是同一个SD卡，可能一个SD卡存在多个有效路径，比如:/mnt/sdcard和sdcard
     *
     * @param sdcard1
     * @param sdcard2
     * @return
     */
    private static boolean isSameSdcard(String sdcard1, String sdcard2) {
        long sdcard1Size = getSdcardSize(sdcard1);
        long sdcard2Size = getSdcardSize(sdcard2);
        // 存储空间不一样，则认为是不同目录
        if (sdcard1Size != sdcard2Size) {
            return false;
        }
        sdcard1Size = getSdcardAvailableSize(sdcard1);
        sdcard2Size = getSdcardAvailableSize(sdcard2);
        // 可用存储空间不一样，则认为是不同的目录
        if (sdcard1Size != sdcard2Size) {
            return false;
        }

        File f1 = new File(sdcard1);
        File f2 = new File(sdcard2);

        String[] fileList1 = f1.list();
        String[] fileList2 = f2.list();
        // 都是空，则认为是同一个目录
        if (fileList1 == null && fileList2 == null) {
            return true;
        }

        // 有一个为空，则认为是不同目录
        if (fileList1 == null || fileList2 == null) {
            return false;
        }

        // 不一样多的文件，则认为不同目录
        if (fileList1.length != fileList2.length) {
            return false;
        }

        // 判断文件是否完全一样
        // int count = fileList1.length;
        // for(int index=0;index<count;index++)
        // {
        // boolean isHave = false;
        // for(int j=0;j<count;j++)
        // {
        // String name1= PathOperator.getFileName(fileList1[index]);
        // String name2 = PathOperator.getFileName(fileList2[j]);
        // if(name1.toLowerCase().equals(name2.toLowerCase()))
        // {
        // isHave = true;
        // break;
        // }
        // }
        // if(!isHave)
        // {
        // return false;
        // }
        // }
        return true;
    }

    /**
     * 获得一个SD卡的存储空间
     *
     * @param sdcardPath
     * @return
     */
    private static long getSdcardSize(String sdcardPath) {
        long size = 0;
        try {
            StatFs statFs = new StatFs(sdcardPath);
            int blockSize = statFs.getBlockSize();
            int totalBlocks = statFs.getBlockCount();
            size = (long) ((long) totalBlocks * (long) blockSize);
        } catch (Exception e) {
        }
        return size;
    }

    /**
     * 获得一个SD卡的剩余存储空间
     *
     * @param sdcardPath
     * @return
     */
    private static long getSdcardAvailableSize(String sdcardPath) {
        long size = 0;
        StatFs statFs = new StatFs(sdcardPath);
        int blockSize = statFs.getBlockSize();
        int totalBlocks = statFs.getAvailableBlocks();
        size = (long) ((long) totalBlocks * (long) blockSize);
        return size;
    }

    public static String getSdCardPath(Context context) {
        if (TextUtils.isEmpty(sdCardPath)) {
            initSDCardPath(context);
        }
        return sdCardPath;
    }

    public static String getExtSdCardPath(Context context) {
        if (TextUtils.isEmpty(extSdCardPath)) {
            initExtSdCardPath(context);
        }
        return extSdCardPath;
    }

}
