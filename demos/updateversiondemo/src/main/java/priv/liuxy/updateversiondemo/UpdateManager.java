package priv.liuxy.updateversiondemo;

import android.text.TextUtils;

/**
 * Created by Liuxy on 2016/3/15.
 * <p>
 * 更新管理类
 */
public class UpdateManager {
    //测试下载地址
    private static final String URL = "http://dldir1.qq.com/weixin/android/weixin6313android740.apk";

    private String url;

    public UpdateManager() {
        url = URL;
    }

    public UpdateManager(String url) {
        this.url = url;
    }

    /**
     * 检查更新
     */
    public void checkUpdate() {
        //TODO：检查更新,主要取得url值
    }

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
}
