package com.mapbar.android.obd.rearview.lib.versionUpdate;

import android.os.Build;
import android.os.Environment;

/**
 * Created by Administrator on 2016/10/27 0027.
 */

public class UpdateAPPConstants {
    //    public static final String APP_URL_TEST="http://downgame.shuowan.com/downloads/package/tianlongbabu3d/tianlongbabu3d_20073947-tlbb3d-46-14.apk";
    public static final String APP_URL_TEST = "http://wdcdn.mapbar.com/appstoreapi/apk/5976f28507864d9992116ac4f57bad19.apk";
    public static final String APP_FOLDER = Environment.getExternalStorageDirectory() + "/app/download/";
    public static final String APP_FILE = Environment.getExternalStorageDirectory() + "/app/download/HAHA.apk";
    public static final String APPCHECK_URL = "http://wdservice.mapbar.com/appstorewsapi/checkexistlist/" + Build.VERSION.SDK_INT;
    //后面需要app_id, 例如:http://wdservice.mapbar.com/appstorewsapi/appdetail/23/da780621efc94902a5c053d0c540e760
    public static final String APPINFO_URL = "http://wdservice.mapbar.com/appstorewsapi/appdetail/" + Build.VERSION.SDK_INT + "/";
    public static final String UPDATE_FOLDER = Environment.getExternalStorageDirectory() + "/app/download/";
    public static final String UPDATE_FILE = UPDATE_FOLDER + "图吧汽车卫士.apk";
}
