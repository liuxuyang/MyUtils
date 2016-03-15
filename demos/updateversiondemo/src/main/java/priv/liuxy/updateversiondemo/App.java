package priv.liuxy.updateversiondemo;

import android.app.Application;
import android.content.IntentFilter;

/**
 * Created by Liuxy on 2016/3/15.
 * 自定义类
 */
public class App extends Application {
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static App getInstance() {
        return mInstance;
    }

    /**
     * 接收器
     */
    private UpdateReceiver receiver;

    /**
     * 注册接收器
     */
    public void registerReceiver() {
        receiver = new UpdateReceiver();
        registerReceiver(receiver, new IntentFilter("android.intent.action.MY_BROADCAST"));
    }

    /**
     * 注销接收器
     */
    public void unRegisterReceiver() {
        unregisterReceiver(receiver);
    }
}
