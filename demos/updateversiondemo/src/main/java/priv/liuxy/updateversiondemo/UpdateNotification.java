package priv.liuxy.updateversiondemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import priv.liuxy.utils.LogUtils;

/**
 * Created by Liuxy on 2015/11/9.
 * <p/>
 * 更新提示栏显示
 */
public class UpdateNotification {
    public static final String TAG = LogUtils.makeLogTag(UpdateNotification.class);

    //notify id
    private static final int ID = 1;
    //
    private NotificationManager mNotifyManager;
    //
    private NotificationCompat.Builder mBuilder;
    //
    private Context mContext;

    public UpdateNotification() {
        App.getInstance().registerReceiver();//注册接收器
        this.mContext = App.getInstance();
        mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MY_BROADCAST");
        intent.putExtra(UpdateReceiver.TAG, UpdateReceiver.CANCEL_DOWNLOAD);
        mBuilder.setDeleteIntent(PendingIntent.getBroadcast(mContext, UpdateReceiver.CANCEL_DOWNLOAD, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    /**
     * 开始
     */
    public void start() {
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setProgress(0, 0, false)
                .setContentText("准备下载");
        mNotifyManager.notify(ID, mBuilder.build());
    }

    /**
     * 实时下载进度提示
     *
     * @param progress
     */
    public void notify(final int progress) {
        mBuilder.setContentTitle("正在下载")
                .setContentText(progress + "%");
        mBuilder.setProgress(100, progress, false);

        mNotifyManager.notify(ID, mBuilder.build());
    }

    /**
     * 下载完成后显示
     *
     * @param fileName
     */
    public void complete(String fileName) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MY_BROADCAST");
        intent.putExtra(UpdateReceiver.TAG, UpdateReceiver.INSTALL_APK);
        intent.putExtra("fileName", fileName);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, UpdateReceiver.INSTALL_APK, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentText("点击安装")
                .setContentTitle("下载完成")
                .setProgress(0, 0, false)
                .setContentIntent(pi);
        mNotifyManager.notify(ID, mBuilder.build());
    }

    /**
     * 设置显示的内容
     *
     * @param content
     */
    public void setContent(String content) {
        mBuilder.setContentText(content)
                .setProgress(0, 0, false);
        mNotifyManager.notify(ID, mBuilder.build());
    }

    /**
     * 退出
     */
    public void cancel() {
        mNotifyManager.cancel(ID);
    }
}
