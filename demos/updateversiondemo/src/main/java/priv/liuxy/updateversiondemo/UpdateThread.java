package priv.liuxy.updateversiondemo;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import priv.liuxy.utils.LogUtils;
import priv.liuxy.utils.StorageUtils;

/**
 * Created by Liuxy on 2016/3/14.
 * <p>
 * 更新线程
 */
public class UpdateThread extends Thread {
    private static final String TAG = LogUtils.makeLogTag(UpdateThread.class);

    /**
     * 默认下载保存目录
     */
    private static final String DEFAULT_CACHE_URL = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/suncar/download";
    /**
     * 资源地址
     */
    private String mResUrl;
    /**
     * 本地存储地址
     */
    public static String mCacheUrl;
    /**
     * 中断标志
     */
    public static boolean stopMark = false;
    /**
     *
     */
    private UpdateNotification notification;

    public UpdateThread(String resUrl) {
        this(resUrl, DEFAULT_CACHE_URL);
    }

    public UpdateThread(String resUrl, String cacheUrl) {
        LogUtils.LOGD(TAG, "resUrl=" + resUrl + ",cacheUrl=" + cacheUrl);
        if (TextUtils.isEmpty(resUrl)) {
            throw new IllegalArgumentException("资源地址为空");
        }
        if (TextUtils.isEmpty(cacheUrl)) {
            throw new IllegalArgumentException("下载保存目录为空");
        }

        this.mResUrl = resUrl;
        mCacheUrl = cacheUrl;
        this.notification = new UpdateNotification();
    }

    @Override
    public void run() {
        String fileName = mResUrl.substring(mResUrl.lastIndexOf("/") + 1);
        LogUtils.LOGD(TAG, "fileName=" + fileName);
        if (TextUtils.isEmpty(fileName)) {
            LogUtils.LOGD(TAG, "下载文件文件名为空");
            return;
        }
        try {
            URL u = new URL(mResUrl);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            //文件长度
            int length = conn.getContentLength();
            if (length == 0) {
                return;
            }
            if (!StorageUtils.isExternalStorageWritable()) {
                notification.setContent("内部储存不可用");
                LogUtils.LOGD(TAG, "储存不可用");
                return;
            }
            if (!StorageUtils.isExternalStorageEnough(length)) {
                notification.setContent("内部储存空间不足");
                LogUtils.LOGD(TAG, "内部储存空间不足");
                return;
            }

            File dir = new File(mCacheUrl);
            if (!dir.exists()) {
                LogUtils.LOGD(TAG, "下载保存目录不存在，创建该目录");
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            if (file.exists()) {
                LogUtils.LOGD(TAG, "覆盖本地同名文件");
                file.delete();
            }


            InputStream is = conn.getInputStream();
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            byte[] buf = new byte[1024 * 2];
            conn.connect();

            if (conn.getResponseCode() != 200) {
                //连接出错
                LogUtils.LOGD(TAG, "response code = " + conn.getResponseMessage());

                notification.setContent("连接出错了");
            } else {
                long count = 0;//如果apk过大会超过2^32，所以使用long
                int numRead = 0;
                int perCount = 0;
                //拉取数据
                while ((numRead = is.read(buf)) != -1) {
                    if (stopMark) {
                        stopMark = false;
                        notification.cancel();
                        return;
                    }
                    fos.write(buf, 0, numRead);
                    count += numRead;
                    int tmp = (int) (count * 100 / length);
                    if (tmp != perCount) {
                        perCount = tmp;
                        LogUtils.LOGD(TAG, tmp + "%");
                        notification.notify(tmp);
                    }
                }
            }
            is.close();
            fos.close();
            conn.disconnect();
            File apk = new File(mCacheUrl + "/" + fileName);
            LogUtils.LOGD(TAG, apk.exists() ? "下载完成" : "下载失败");
            if (apk.exists()) {
                notification.complete(fileName);
                LogUtils.LOGD(TAG, "下载完成");
            } else {
                notification.setContent("下载出错了,请稍后再试");
                LogUtils.LOGD(TAG, "下载失败");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
