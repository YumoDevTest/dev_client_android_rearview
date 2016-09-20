package com.mapbar.android.obd.rearview.lib.ota;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.mapbar.android.obd.rearview.lib.base.MyEnviroment;
import com.mapbar.android.obd.rearview.lib.config.Urls;
import com.mapbar.android.obd.rearview.modules.vin.VinManager;
import com.mapbar.obd.foundation.net.FileDownloader;
import com.mapbar.obd.foundation.net.GsonHttpCallback;
import com.mapbar.obd.foundation.net.HttpParameter;
import com.mapbar.obd.foundation.net.HttpResponse;
import com.mapbar.obd.foundation.net.HttpUtil;
import com.mapbar.obd.foundation.log.LogUtil;
import com.mapbar.obd.CarDetail;
import com.mapbar.obd.FileUtils;
import com.mapbar.obd.LocalUserCarResult;
import com.mapbar.obd.Manager;
import com.mapbar.obd.UserCar;
import com.mapbar.obd.serial.utils.OutputStringUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.List;

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
    private MyOTAVersionCheckByVin myAsyncTask;

    /**
     * 是否是专车固件
     *
     * @return
     */
    public boolean isV3SpecialOta() {
        boolean isV3SpecialOta = Manager.getInstance().isV3SpecialOta();
        return isV3SpecialOta;
    }

    /**
     * 检查 版本
     */
    public void checkVersion(final VersionCheckCallback versionCheckCallback) {
        boolean isV3SpecialOta = isV3SpecialOta();
        VinManager vinManager = new VinManager();
        String vin = vinManager.getVin();
        if (isV3SpecialOta) {//如果是专车专用固件
            //如果 vin为空 否则开始启动检测version
            if (!TextUtils.isEmpty(vin)) {
                LogUtil.d(TAG, "## 专车专用固件，有vin，开始检查固件版本");
                checkVersionByVin(versionCheckCallback);
            } else {
                //do nothing
                LogUtil.d(TAG, "## 专车专用固件，没有vin，停止检查固件版本");
            }
        } else {// 普通固件，直接检查，不管有没有vin
            LogUtil.d(TAG, "## 普通固件，开始检查固件版本,vin=" + vin);
            checkVersionByVin(versionCheckCallback);
        }
    }

    /**
     * 启动检查固件版本
     *
     * @param versionCheckCallback
     */
    private void checkVersionByVin(final VersionCheckCallback versionCheckCallback) {
        if (myAsyncTask != null)
            return;
        myAsyncTask = new MyOTAVersionCheckByVin(versionCheckCallback);
        myAsyncTask.execute();
    }

    /**
     * 检查固件版本。允许vin为空
     */
    private class MyOTAVersionCheckByVin extends AsyncTask<Void, Void, Boolean> {
        private Exception exception;
        private VersionCheckCallback versionCheckCallback;

        public MyOTAVersionCheckByVin(VersionCheckCallback versionCheckCallback) {
            this.versionCheckCallback = versionCheckCallback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                LocalUserCarResult result = Manager.getInstance().queryLocalUserCar();
                if (result == null)
                    throw new Exception("获得本地车辆信息失败");
                if (result.userCars == null || result.userCars.length == 0)
                    throw new Exception("获得本地车辆信息失败");

                String vin = new VinManager().getVin();//vin 可能为空
                UserCar car = result.userCars[0];
                CarDetail cur = Manager.getInstance().getCarDetailByCarInfo(car);
                String firstBrand = cur.firstBrand.trim();//firstCar
                String carModel = cur.carModel.trim();//secondCar
                String generation = cur.generation.trim();//thirdCar

                String localVersion = getLocalVersion();//
//                TODO  测试用，记得删除此行代码
//                localVersion = "ELM327 v1.5\rMAPBAR627H v1.6.1020\rBMW.5.E60.0000.0001";

                LogUtil.d(TAG, "## 本地固件版本是：" + OutputStringUtil.transferForPrint(localVersion));
                if (TextUtils.isEmpty(localVersion))
                    throw new Exception("无法获得硬件固件版本");
                if (!localVersion.contains("BLV") && !localVersion.contains("MAPBAR627")) {
                    //非 v3硬件，不支持检查升级
                    return false;
                }

                HttpParameter para = HttpParameter.create()
                        .add("vin", vin)
                        .add("f_brand", firstBrand)
                        .add("t_brand", carModel)
                        .add("m_model", "9".equals(generation) ? "" : generation)
                        .add("version", localVersion)
                        .add("select_tag", "9".equals(generation) ? SELECT_TAG : ((TextUtils.isEmpty(generation) ? VIN_CAR : USER_CAR)))
                        .add("btType", BT_TYPE);

                Type type = new TypeToken<List<CheckVersionBean>>() {
                }.getType();
                HttpUtil.post(Urls.OTA_QUERYUPGRADEFILE, para, new GsonHttpCallback<List<CheckVersionBean>>(type) {

                    @Override
                    protected void onSuccess(List<CheckVersionBean> versionBeanList, HttpResponse httpResponse) {
                        if (versionBeanList == null || versionBeanList.size() == 0) {
                            if (versionCheckCallback != null)
                                versionCheckCallback.onError(new Exception("远程服务返回了意外的数据"));
                            return;
                        }
                        CheckVersionBean versionBean = versionBeanList.get(0);
                        String binUrl = versionBean.down_url;
//                        boolean bin_must_update = versionBean.bin_must_update == 1;
                        checkLocalOrDownloadBinFile(binUrl, versionBean, versionCheckCallback);
                    }

                    @Override
                    protected boolean onFailure(int code, HttpResponse httpResponse) {
                        if (code == 206) {//没有版本更新
                            LogUtil.d(TAG, "## 没有可更新的版本");
                            if (versionCheckCallback != null)
                                versionCheckCallback.noNothing();
                        }
                        return true;
                    }
                }, null);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                this.exception = e;
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean isok) {
            if (!isok) {
                if (this.exception != null)
                    if (versionCheckCallback != null)
                        versionCheckCallback.onError(this.exception);
            }
            myAsyncTask = null;
        }
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
            LogUtil.d(TAG, "## 检查文件缓存存在，无需再次下载,文件路径=" + targetFile.getPath());
            raiseOnFoundNewVersion(targetFile, versionBean, versionCheckCallback);
            return;
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
                if (versionCheckCallback != null)
                    versionCheckCallback.onError(ex);
            }

            @Override
            public void onFinish(File file1) {
                LogUtil.d(TAG, "## bin文件下载成功,len=" + file1.length() + ", 存放路径=" + file1.getPath());

                raiseOnFoundNewVersion(file1, versionBean, versionCheckCallback);
            }
        });
    }

    private static void raiseOnFoundNewVersion(File binZipFile1, final CheckVersionBean versionBean, final VersionCheckCallback versionCheckCallback) {
        try {
            // 解压至ota目录下
            String zipFileName = binZipFile1.getName();
            zipFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
            File binUnzipDir = new File(MyEnviroment.getOTAFilesDir(), zipFileName);
            if (!binUnzipDir.exists()) {
                binUnzipDir.mkdirs();
            } else {//如果已经存在，则尝试查找
                File[] foundFiles = findBinFile(binUnzipDir);
                if (foundFiles.length != 0) {
                    LogUtil.d(TAG, "## 以前解压过bin文件，直接使用");
                    if (versionCheckCallback != null)
                        versionCheckCallback.onFoundNewVersion(foundFiles[0], versionBean);
                    return;
                }
            }
            try {
                FileUtils.upZipFile(binZipFile1, MyEnviroment.getOTAFilesDir().getPath(), true);// 解压成功
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception("解压缩失败,下载到的文件无法被解压");
            }
            LogUtil.d(TAG, "## bin文件解压成功");
            File[] foundFiles = findBinFile(binUnzipDir);

            if (foundFiles.length == 0)
                throw new Exception("下载到的zip中不包含bin文件");
            if (versionCheckCallback != null)
                versionCheckCallback.onFoundNewVersion(foundFiles[0], versionBean);
        } catch (Exception e) {
            e.printStackTrace();
            if (versionCheckCallback != null)
                versionCheckCallback.onError(e);
        }
    }

    private static File[] findBinFile(File binDir) {
        //找到解压后的 bin文件
        return binDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith(".bin"))
                    return true;
                return false;
            }
        });
    }

    private File buildBinFilePath(@NonNull String binUrl) {
        File dir = MyEnviroment.getOTAFilesDir();
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

        void onError(Exception e);
    }
}
