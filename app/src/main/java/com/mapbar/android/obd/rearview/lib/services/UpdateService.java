package com.mapbar.android.obd.rearview.lib.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.model.AppInfo;
import com.mapbar.android.obd.rearview.modules.common.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wanghw on 2016/7/8.
 */
public class UpdateService extends Service {
    //下载状态
    private final static int DOWNLOAD_COMPLETE = 0;
    private final static int DOWNLOAD_FAIL = 1;
    private final Intent installIntent;
    private final PendingIntent updatePendingIntent;
    //标题
    private int titleId = 0;
    //文件存储
    private File updateDir = null;
    private File updateFile = null;
    //通知栏
    private NotificationManager updateNotificationManager = null;
    //通知栏跳转Intent
    private AppInfo info;

    public UpdateService() {

        //点击安装PendingIntent
        Uri uri = Uri.fromFile(updateFile);
        installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(uri, "application/vnd.android.package-archive");

        updatePendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installIntent, 0);
    }


    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_COMPLETE: {

                    Notification notification = new NotificationCompat.Builder(getContext()).
                            setDefaults(Notification.DEFAULT_SOUND).
                            setContentTitle(getString(R.string.app_name)).
                            setContentText("下载完成,点击安装。").
                            setContentIntent(updatePendingIntent).
                            build();

                    updateNotificationManager.notify(0, notification);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(updateFile), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    //停止服务
//                    stopSelf();
                }
                break;
                case DOWNLOAD_FAIL: {
                    //下载失败
//                    updateNotification.setLatestEventInfo(UpdateService.this, getString(R.string.app_name), "下载完成,点击安装。", updatePendingIntent);

                    PendingIntent updatePendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installIntent, 0);
                    Notification notification = new NotificationCompat.Builder(getContext()).
                            setDefaults(Notification.DEFAULT_SOUND).
                            setContentTitle(getString(R.string.app_name)).
                            setContentText("下载完成,点击安装。").
                            setContentIntent(updatePendingIntent).
                            build();

                    updateNotificationManager.notify(0, notification);
                    break;
                }
                default:
            }
            stopSelf();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(LogTag.OBD, "whw UpdateService onStartCommand");
        info = (AppInfo) intent.getSerializableExtra("app_info");
        if (info == null)
            return Service.START_NOT_STICKY;
//        titleId = intent.getIntExtra("titleId",0);

        //创建文件
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory(), "app/download/");
            updateFile = new File(updateDir.getPath(), info.getName());
        }

        this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //设置下载过程中，点击通知栏，回到主界面
        Intent updateIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
        //设置通知栏显示内容
        Notification notification = new NotificationCompat.Builder(getContext()).
                setContentTitle(getString(R.string.app_name)).
                setContentText("0%").
                setContentIntent(pendingIntent).
                setSmallIcon(R.drawable.logo).
                setTicker("开始下载").
                build();

        //发出通知
        updateNotificationManager.notify(0, notification);

        //开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
        new Thread(new updateRunnable()).start();//这个是下载的重点，是下载的过程

        return Service.START_NOT_STICKY;
    }

    public long downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;

        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
            if (currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
            }
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);
            byte buffer[] = new byte[4096];
            int readsize = 0;


            PendingIntent updatePendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installIntent, 0);

            while ((readsize = is.read(buffer)) > 0) {
                fos.write(buffer, 0, readsize);
                totalSize += readsize;
                //百分比增加10才通知一次
                if ((downloadCount == 0) || (int) (totalSize * 100 / updateTotalSize) - 10 > downloadCount) {
                    downloadCount += 10;
//                    Log.e(LogTag.OBD, "whw UpdateService downloadUpdateFile downloadCount==" + downloadCount);
                    String progressText = (int) totalSize * 100 / updateTotalSize + "%";
                    Notification notification = new NotificationCompat.Builder(getContext()).
                            setDefaults(Notification.DEFAULT_SOUND).
                            setContentTitle("正在下载").
                            setContentText(progressText).
                            setContentIntent(updatePendingIntent).
                            build();
                    updateNotificationManager.notify(0, notification);
                    //TODO 下载到100弹窗安装
                    if (downloadCount > 7) {
                        Log.e(LogTag.OBD, "whw UpdateService downloadUpdateFile totalSize==" + totalSize);
                        Log.e(LogTag.OBD, "whw UpdateService downloadUpdateFile updateTotalSize==" + updateTotalSize);
                    }
                }
            }
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return totalSize;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    class updateRunnable implements Runnable {
        Message message = updateHandler.obtainMessage();

        public void run() {
            message.what = DOWNLOAD_COMPLETE;
            try {
                //增加权限;
                if (!updateDir.exists()) {
                    updateDir.mkdirs();
                }
                if (!updateFile.exists()) {
                    updateFile.createNewFile();
                }
                //下载函数，以QQ为例子
                //增加权限;
                long downloadSize = downloadUpdateFile(info.getApk_path(), updateFile);
                if (downloadSize > 0) {
                    //下载成功
                    updateHandler.sendMessage(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                message.what = DOWNLOAD_FAIL;
                //下载失败
                updateHandler.sendMessage(message);
            }
        }
    }


    private Context getContext() {
        return this;
    }

    //    private void cheanUpdateFile() {
//        File updateFile = new File(updateFiler,getResources().getString(R.string.app_name)+".apk");
//        if(updateFile.exists()){
//            //当不需要的时候，清除之前的下载文件，避免浪费用户空间
//            updateFile.delete();
//        }
//    }
}
