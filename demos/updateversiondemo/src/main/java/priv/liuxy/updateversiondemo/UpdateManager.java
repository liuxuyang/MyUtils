package priv.liuxy.updateversiondemo;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

import priv.liuxy.utils.LogUtils;

/**
 * Created by Liuxy on 2016/3/15.
 * <p/>
 * 更新管理类
 */
public class UpdateManager {
    private static final String TAG = LogUtils.makeLogTag(UpdateManager.class);
    //测试下载地址
    private static final String URL = "http://dldir1.qq.com/weixin/android/weixin6313android740.apk";
    //下载地址
    private String url;
    //文件保存路径
    private String path;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private UpdateManager() {
        url = URL;
    }

    private static UpdateManager instance;

    public static UpdateManager getInstance() {
        if (instance == null) {
            synchronized (UpdateManager.class) {
                if (instance == null) {
                    instance = new UpdateManager();
                }
            }
        }
        return instance;
    }

    /**
     * 检查更新
     */
    public void checkUpdate() {
        //TODO：检查更新,主要取得url值
    }

    /**
     * 下载更新包
     */
    public void startUpdate() {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url is unavailable");
        }
        try {
            UpdateThread thread = new UpdateThread(url);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安装文件,使用系统安装器进行安装。
     */
    public void installAPK() {
        if (checkAPK(path)) {
            throw new RuntimeException("下载未完成");
        }
        try {
            String command = "chmod " + 777 + " " + path;
            LogUtils.LOGD(TAG, "command = " + command);
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent i = new Intent();
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        App.getInstance().startActivity(i);
    }

    private boolean checkAPK(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        String suffix = path.substring(path.lastIndexOf(".") + 1);
        if (!"APK".equals(suffix)) {
            return false;
        }

        try {
            File apk = new File(path);
            if (!apk.exists()) {
                return false;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
