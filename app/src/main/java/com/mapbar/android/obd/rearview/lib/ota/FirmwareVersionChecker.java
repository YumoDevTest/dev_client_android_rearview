package com.mapbar.android.obd.rearview.lib.ota;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.lib.net.FileDownloader;
import com.mapbar.android.obd.rearview.lib.net.GsonHttpCallback;
import com.mapbar.android.obd.rearview.lib.net.HttpParameter;
import com.mapbar.android.obd.rearview.lib.net.HttpResponse;
import com.mapbar.android.obd.rearview.lib.net.HttpUtil;
import com.mapbar.android.obd.rearview.lib.vin.VinManager;
import com.mapbar.android.obd.rearview.obd.util.LogUtil;
import com.mapbar.android.obd.rearview.obd.util.Urls;
import com.mapbar.obd.CarDetail;
import com.mapbar.obd.FileUtils;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCar;

import java.io.File;
import java.security.InvalidParameterException;

/**
 * 固件 版本检查
 * Created by zhangyunfei on 16/8/26.
 */
public class FirmwareVersionChecker {
    private static final String TAG = "OTA";
    private static final String BT_TYPE = "7";
    private static final String SELECT_TAG = "9";
    private static final String VIN_CAR = "1";
    private static final String USER_CAR = "2";

    /**
     * 检查 版本
     */
    public void checkVersion(final VersionCheckCallback versionCheckCallback) {
        LocalUserCarResult result = Manager.getInstance().queryLocalUserCar();
        if (result == null)
            return;
        if (result.userCars == null || result.userCars.length == 0)
            return;

        String vin = new VinManager().getVin();
        UserCar car = result.userCars[0];
        CarDetail cur = Manager.getInstance().getCarDetailByCarInfo(car);
        String firstBrand = cur.firstBrand.trim();//firstCar
        String carModel = cur.carModel.trim();//secondCar
        String generation = cur.generation.trim();//thirdCar

        String localVersion = getLocalVersion();//

        HttpParameter para = HttpParameter.create()
                .add("vin", vin)
                .add("f_brand", firstBrand)
                .add("t_brand", carModel)
                .add("m_model", "9".equals(generation) ? "" : generation)
                .add("version", localVersion)
                .add("select_tag", "9".equals(generation) ? SELECT_TAG : ((TextUtils.isEmpty(generation) ? VIN_CAR : USER_CAR)))
                .add("btType", BT_TYPE);

        HttpUtil.post(Urls.OTA_QUERYUPGRADEFILE, para, new GsonHttpCallback<CheckVersionBean>(CheckVersionBean.class) {

            @Override
            protected void onSuccess(CheckVersionBean versionBean, HttpResponse httpResponse) {
                String binUrl = versionBean.down_url;
                boolean bin_must_update = versionBean.bin_must_update == 1;
                checkLocalOrDownloadBinFile(binUrl, versionBean, versionCheckCallback);
            }

            @Override
            protected boolean onFailure(int code, HttpResponse httpResponse) {
                if (code == 206) {//没有版本更新
                    LogUtil.d(TAG, "没有版本更新");
                    if (versionCheckCallback != null)
                        versionCheckCallback.noNothing();
                }
                return true;
            }
        }, null);

    }

    /**
     * 检查本地文件
     * 尝试 下载bin文件
     *
     * @param binUrl
     */
    private void checkLocalOrDownloadBinFile(String binUrl, final CheckVersionBean versionBean, final VersionCheckCallback versionCheckCallback) {
        File targetFile = buildBinFilePath(binUrl);
        if (targetFile.exists()) {
            if (versionCheckCallback != null)
                versionCheckCallback.onFoundNewVersion(targetFile, versionBean);
        }
        FileDownloader.download(binUrl, targetFile, new FileDownloader.FileDownloadCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int percent) {

            }

            @Override
            public void onError(Exception ex) {

            }

            @Override
            public void onFinish(File file1) {
                // 解压至ota目录下
//                FileUtils.upZipFile(file, getOTAPath(), true);// 解压成功
                if (versionCheckCallback != null)
                    versionCheckCallback.onFoundNewVersion(file1, versionBean);
            }
        });
    }

    private File buildBinFilePath(@NonNull String binUrl) {
        File dir = getOTAFilesDir();
        String tmp = binUrl;
        if (tmp.endsWith("/"))
            tmp = tmp.substring(0, binUrl.length() - 1);
        int index = binUrl.lastIndexOf("/");
        if (index < 0 || (index + 1) == tmp.length()) {
            throw new InvalidParameterException("binUrl 不合法");
        }
        tmp = tmp.substring(index + 1);
        return new File(dir, tmp);

    }

    @NonNull
    private static File getOTAFilesDir() {
        return Environment.getDownloadCacheDirectory();//Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getLocalVersion() {
        String version = Manager.getInstance().getOBDVersion();
        String newVersion = version;
        if (version.equals("MAPBAR4")) {
            newVersion += " ";
        }
        if (version.contains("MAPBAR627 v1.6.1036")) {//ELM327 v1.5MAPBAR627 v1.6.1036
            newVersion = newVersion.replace("MAPBAR627 v1.6.1036", "MAPBAR627H v1.6.1036");
        }
        return newVersion;
    }

    public interface VersionCheckCallback {
        /**
         * 当需要更新版本
         */
        void onFoundNewVersion(File binFile, CheckVersionBean checkVersionBean);

        /**
         * 当没有新版本
         */
        void noNothing();
    }
}
