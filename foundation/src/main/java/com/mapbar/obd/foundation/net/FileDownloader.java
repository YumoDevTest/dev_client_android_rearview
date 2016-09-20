package com.mapbar.obd.foundation.net;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.mapbar.obd.foundation.log.LogUtil;

/**
 * 文件下载
 * Created by zhangyunfei on 16/8/27.
 */
public class FileDownloader {

    private static final String TAG = "HTTP";

    public static void download(String url, File targetFile, final FileDownloadCallback callback) {
        if (url == null) {
            throw new NullPointerException();
        }
        if (targetFile == null) {
            throw new NullPointerException();
        }
        if (callback != null)
            callback.onStart();
        LogUtil.d(TAG, "## 启动下载,url=" + url);
        Request request = new Request.Builder().url(url).build();
        MyHttpContext.getOkHttpClient()
                .newCall(request)
                .enqueue(new MyCallback(targetFile, callback));
    }


    private static class MyCallback implements Callback {
        private File targetFile;
        private FileDownloadCallback callback;

        public MyCallback(File file, final FileDownloadCallback callback) {
            this.targetFile = file;
            this.callback = callback;
        }

        @Override
        public void onResponse(Response response) throws IOException {
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
            try {
                is = response.body().byteStream();
                long total = response.body().contentLength();
                fos = new FileOutputStream(targetFile);
                long sum = 0;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    sum += len;
                    int progress = (int) (sum * 1.0f / total * 100);
                    LogUtil.d(TAG, "## progress=" + progress);
                    if (callback != null)
                        callback.onProgress(progress);
                }
                fos.flush();
                LogUtil.d(TAG, "## 文件下载成功," + targetFile.getPath());
                if (callback != null)
                    callback.onFinish(targetFile);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "## 文件下载失败," + e.getMessage(), e);
                if (callback != null)
                    callback.onError(e);

            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                }
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                }
            }
        }

        @Override
        public void onFailure(Request arg0, IOException e) {
            LogUtil.d("HTTP", "## 文件下载异常:" + e.getMessage());
            if (callback != null)
                callback.onError(e);
        }
    }

    public interface FileDownloadCallback {
        void onStart();

        void onProgress(int percent);

        void onError(Exception ex);

        void onFinish(File binFile);
    }
}
