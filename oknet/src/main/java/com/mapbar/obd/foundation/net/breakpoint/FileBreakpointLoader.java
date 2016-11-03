package com.mapbar.obd.foundation.net.breakpoint;

import android.content.Context;
import android.util.Log;

import com.mapbar.obd.foundation.net.MyHttpContext;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by zhangyh on 2016/11/1 0001.
 * 断点续传下载类
 */

public class FileBreakpointLoader {
    private static final String TAG = "FileBreakpointLoader";
    private static DownloadTask downloadTask;
    private static FileDownloadCallback callback;
    private static OkHttpClient client;

    /**
     * 下载文件的方法
     *
     * @param context
     * @param task
     * @param callback
     */
    public static void download(Context context, DownloadTask task, final FileBreakpointLoader.FileDownloadCallback callback) {
        //获取请求对象
        client = MyHttpContext.getOkHttpClient();
        downloadTask = task;
        FileBreakpointLoader.callback = callback;
        if (task == null) {
            throw new NullPointerException();
        }
        if (callback != null)
            callback.onStart();
        //创建一个新的请求并执行
        newCall(task).enqueue(new MyCallback(context, task, callback));
    }

    /**
     * 每次下载需要新建新的Call对象
     */
    private static Call newCall(DownloadTask task) {
        Request request = new Request.Builder()
                .url(task.getUrl())
                .header("RANGE", "bytes=" + task.getBreakpoint() + "-")//断点续传要用到的，指示下载的区间
                .build();
        return client.newCall(request);
    }

    public interface FileDownloadCallback {
        void onStart();

        void onProgress(int percent);

        void onError(Exception ex);

        void onFinish(File binFile);
    }

    /**
     * 自定义回调
     */
    private static class MyCallback implements Callback {
        private DownloadTask task;
        private FileBreakpointLoader.FileDownloadCallback callback;
        private File saveFile;
        private Context context;

        public MyCallback(Context context, DownloadTask task, final FileBreakpointLoader.FileDownloadCallback callback) {
            this.task = task;
            this.callback = callback;
            this.saveFile = new File(task.getFilePath());
            this.context = context;
        }

        //请求到数据
        @Override
        public void onResponse(Response response) throws IOException {
            Log.d(TAG, "$$ 开始请求下载");
            ResponseBody body = response.body();
            InputStream in = body.byteStream();
            FileChannel channelOut = null;
            // 随机访问文件，可以指定断点续传的起始位置
            RandomAccessFile randomAccessFile = null;
            try {
                randomAccessFile = new RandomAccessFile(saveFile, "rwd");
                //Chanel NIO中的用法，由于RandomAccessFile没有使用缓存策略，直接使用会使得下载速度变慢，亲测缓存下载3.3秒的文件，用普通的RandomAccessFile需要20多秒。
                channelOut = randomAccessFile.getChannel();
                // 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
                long total = body.contentLength() + task.getBreakpoint();
                Log.d(TAG, "$$ 文件的长度" + total);
                MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, task.getBreakpoint(), body.contentLength());
                byte[] buffer = new byte[1024];
                int len;
                Log.d(TAG, "$$ 访问网络:::" + task.toString());
                long point = task.getBreakpoint();
                int updateProgress = 0;
                while ((len = in.read(buffer)) != -1) {
                    //写入到buffer中
                    mappedBuffer.put(buffer, 0, len);
                    //强刷到文件,考虑到关机因素
                    mappedBuffer.force();
                    //计算进度
                    point += len;
                    int progress = (int) (point * 1.0f / total * 100);
                    task.setBreakpoint(point);
                    //直接在这里写入到SP中,考虑到关机因素
                    BreakpointSPUtil.setTask(context, task);
                    if (callback != null && updateProgress != progress)
                        callback.onProgress(progress);
                    updateProgress = progress;
                }
                Log.d(TAG, "$$ 文件下载成功");
                task.setCompelete(true);
                if (callback != null)
                    callback.onFinish(saveFile);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "$$  文件下载失败," + e.getMessage(), e);
                if (callback != null)
                    callback.onError(e);
            } finally {
                try {
                    BreakpointSPUtil.setTask(context, task);
                    in.close();
                    if (channelOut != null) {
                        channelOut.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Request arg0, IOException e) {
            if (callback != null)
                callback.onError(e);
        }
    }
}
