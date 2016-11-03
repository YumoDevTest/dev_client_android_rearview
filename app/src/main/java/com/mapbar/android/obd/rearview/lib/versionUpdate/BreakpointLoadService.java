package com.mapbar.android.obd.rearview.lib.versionUpdate;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.mapbar.obd.foundation.net.GsonHttpCallback;
import com.mapbar.obd.foundation.net.HttpResponse;
import com.mapbar.obd.foundation.net.HttpUtil;
import com.mapbar.obd.foundation.net.breakpoint.BreakpointSPUtil;
import com.mapbar.obd.foundation.net.breakpoint.DownloadTask;
import com.mapbar.obd.foundation.net.breakpoint.FileBreakpointLoader;
import com.mapbar.obd.foundation.utils.SafeHandler;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhangyh on 2016/11/1 0001.
 * 断点下载服务
 */
public class BreakpointLoadService extends Service {
    //下载状态
    private final static int DOWNLOAD_COMPLETE = 0;
    private final static int DOWNLOAD_FAIL = 1;
    private final static int DOWNLOAD_START = 2;
    private static final String TAG = "BreakpointLoadService";
    //文件存储
    private File updateDir = null;
    private File updateFile = null;
    private AppInfo info;
    private BreakpointLoadService.LoadHandler updateHandler = new BreakpointLoadService.LoadHandler(this);

    public BreakpointLoadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        info = (AppInfo) intent.getSerializableExtra("app_info");
        if (info == null)
            return Service.START_NOT_STICKY;
        //创建文件
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
            updateDir = new File(UpdateAPPConstants.UPDATE_FOLDER);
            updateFile = new File(UpdateAPPConstants.UPDATE_FILE);
        }
        //判断本地是否已经下载过最新版本,并且可用
        checkNativeApp(info);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "服务中断");
    }

    private Context getContext() {
        return this;
    }

    /**
     * 检测本地是否有已经下载过的版本,并对照MD5
     * 如果有直接进行安装,如果没有开启下载,如果有但是没有下载完进行断点下载
     *
     * @param info
     */
    private void checkNativeApp(final AppInfo info) {
        //请求详细信息获取MD5
        HttpUtil.post(UpdateAPPConstants.APPINFO_URL + info.getApp_id(), null, new GsonHttpCallback<AppDetailedInfo>(AppDetailedInfo.class) {
            @Override
            protected void onSuccess(AppDetailedInfo appDetailedInfo, HttpResponse httpResponse) {
                String netMD5 = appDetailedInfo.getMd5();
                Log.d(TAG, "$$ 网络请求的MD5::" + netMD5);
                //如果文件存在
                if (updateFile.exists()) {
                    //解析本地文件MD5
                    String fileMD5 = MD5Helper.fileToMD5(updateFile);
                    Log.d(TAG, "$$ 本地文件md5::" + fileMD5);
                    DownloadTask task = BreakpointSPUtil.getTask(getContext());
                    //如果md5相同直接安装
                    if (netMD5.equalsIgnoreCase(fileMD5)) {
                        Log.d(TAG, "$$ MD5相同,直接安装");
                        installApp();
                    }
                    //如果连接相同,但是任务未完成,继续下载
                    else if (task != null && task.getUrl().equals(info.getApk_path()) && !task.isCompelete()) {
                        //继续下载
                        Log.d(TAG, "$$ 继续下载");
                        startLoad(task);
                    }
                    //其他情况,重新下载
                    else {
                        //重新下载,创建一个新的task,并进行存储,下载
                        DownloadTask downloadTask = getNewTask(info.getApk_path(), updateFile.getAbsolutePath(), netMD5);
                        BreakpointSPUtil.setTask(getContext(), downloadTask);
                        startLoad(downloadTask);
                    }
                } else {
                    //如果文件不存在,创建文件,开启任务,下载
                    String url = info.getApk_path();
                    if (!updateDir.exists()) {
                        updateDir.mkdirs();
                    }
                    try {
                        updateFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    DownloadTask downloadTask = getNewTask(url, updateFile.getAbsolutePath(), netMD5);
                    BreakpointSPUtil.setTask(getContext(), downloadTask);
                    Log.d(TAG, "$$ 开启线程下载apk:");
                    startLoad(downloadTask);
                }
            }
        });
    }

    /**
     * 获取一个新的下载任务
     *
     * @param url
     * @param filePath
     * @param MD5
     * @return
     */
    private DownloadTask getNewTask(String url, String filePath, String MD5) {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.setCompelete(false);
        downloadTask.setBreakpoint(0L);
        downloadTask.setFilePath(filePath);
        downloadTask.setMD5(MD5);
        downloadTask.setUrl(url);
        return downloadTask;
    }

    /**
     * 开启下载
     *
     * @param downloadTask
     */
    private void startLoad(final DownloadTask downloadTask) {
        updateHandler.sendEmptyMessage(DOWNLOAD_START);
        final Message message = updateHandler.obtainMessage();
        message.what = DOWNLOAD_COMPLETE;
        FileBreakpointLoader.download(getContext(), downloadTask, new FileBreakpointLoader.FileDownloadCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onProgress(int percent) {
                Log.d(TAG, "$$ 下载的进度::" + percent);
            }

            @Override
            public void onError(Exception ex) {
                Log.d(TAG, "$$ " + ex.toString());
                message.what = DOWNLOAD_FAIL;
                //下载失败
                updateHandler.sendMessage(message);
            }

            @Override
            public void onFinish(File binFile) {
                updateHandler.sendMessage(message);
            }
        });
    }

    /**
     * 安装app的方法
     */
    private void installApp() {
        //发送一个广播让activity结束对网络状况的监听
        Intent reciverIntent = new Intent();
        reciverIntent.setAction("mapbar.obd.intent.action.CLOSE_NETLINSTENER");
        Log.d(TAG, "发送广播停止监听网络");
        sendBroadcast(reciverIntent);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(updateFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopSelf();
    }

    private static class LoadHandler extends SafeHandler<BreakpointLoadService> {

        public LoadHandler(BreakpointLoadService object) {
            super(object);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_START: {
                    Toast.makeText(getInnerObject(), "后台下载中,请稍后...", Toast.LENGTH_LONG).show();
                    break;
                }
                case DOWNLOAD_COMPLETE: {
                    Log.d(TAG, "$$ 下载完成::判断MD5是否相同");
                    DownloadTask task = BreakpointSPUtil.getTask(getInnerObject().getContext());
                    String fileMD5 = MD5Helper.fileToMD5(new File(task.getFilePath()));
                    if (fileMD5.equalsIgnoreCase(task.getMD5())) {
                        Log.d(TAG, "$$ MD5相同,开始安装");
                        getInnerObject().installApp();
                    } else {
                        Log.d(TAG, "$$ MD5不同,重新下载");
                        task.setBreakpoint(0L);
                        task.setCompelete(false);
                        getInnerObject().startLoad(task);
                    }
                    break;
                }
                case DOWNLOAD_FAIL: {
                    Toast.makeText(getInnerObject(), "$$ 新版本下载失败", Toast.LENGTH_LONG).show();
                    break;
                }
            }
            getInnerObject().stopSelf();
        }
    }

}
