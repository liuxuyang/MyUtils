package priv.liuxy.updateversiondemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

import priv.liuxy.utils.LogUtils;

/**
 * @author Liuxy
 *         2015年11月10日10:50:15
 *         <p>
 *         更新操作接受器
 */
public class UpdateReceiver extends BroadcastReceiver {
    public static final String TAG = LogUtils.makeLogTag(UpdateReceiver.class);

    public static final int DEFAULT = 0x0001;
    public static final int CANCEL_DOWNLOAD = DEFAULT << 2;
    public static final int INSTALL_APK = CANCEL_DOWNLOAD << 2;


    public UpdateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        App.getInstance().unRegisterReceiver();//注销接收器
        int code = intent.getIntExtra(TAG, 0);
        switch (code) {
            case CANCEL_DOWNLOAD:
                LogUtils.LOGD(TAG, "CANCEL_DOWNLOAD");
                UpdateThread.stopMark = true;
                break;
            case INSTALL_APK:
                LogUtils.LOGD(TAG, "INSTALL_APK");
                installAPK(intent);
                break;
            default:
        }
    }

    /**
     * 安装文件,使用系统安装器进行安装。
     *
     * @param intent
     */
    private void installAPK(Intent intent) {
        String fileName = intent.getStringExtra("fileName");
        try {
            String command = "chmod " + 777 + " " + UpdateThread.mCacheUrl + "/" + fileName;
            LogUtils.LOGD(TAG, "command = " + command);
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent i = new Intent();
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(new File(UpdateThread.mCacheUrl + fileName)), "application/vnd.android.package-archive");
        App.getInstance().startActivity(i);
    }
}
