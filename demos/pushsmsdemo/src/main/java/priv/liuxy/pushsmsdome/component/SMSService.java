package priv.liuxy.pushsmsdome.component;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import priv.liuxy.pushsmsdome.Config;
import priv.liuxy.pushsmsdome.ISmsAidlInterface;
import priv.liuxy.pushsmsdome.bean.SmsInfo;
import priv.liuxy.pushsmsdome.dao.SmsDao;
import priv.liuxy.utils.LogUtils;
import priv.liuxy.utils.SPFUtils;

/**
 * @author Liuxy
 */
public class SMSService extends Service {
    private static final String LOG_TAG = LogUtils.makeLogTag(SMSService.class);
    //短信监听广播优先级
    private static final int PRIORITY = 999;
    //短信广播
    private SMSReceiver mReceiver;

    //message what
    private static final int MESSAGE_WHAT_PUSH = 0x0001;
    //
    private List<SmsInfo> mList;
    //
    private OkHttpClient mOkHttpClient;
    //
    private SmsDao mDao;

    public SMSService() {
        LogUtils.LOGD(LOG_TAG, "create service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.LOGD(LOG_TAG, "onCreate");
        mOkHttpClient = new OkHttpClient();
        mDao = new SmsDao(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.LOGD(LOG_TAG, "onBind");
        mList = intent.getParcelableArrayListExtra(Config.INTENT_KEY_SMS_INFO);
        mDao.addAll(mList);
        return mIBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.LOGD(LOG_TAG, "onStartCommand");
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(PRIORITY);
        mReceiver = new SMSReceiver();
        registerReceiver(mReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.LOGD(LOG_TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        LogUtils.LOGD(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    //构建binder
    private IBinder mIBinder = new ISmsAidlInterface.Stub() {

        @Override
        public void pushData(String smsBody, String smsAddress, long time) throws RemoteException {
            //TODO：保存到数据库,并尝试上传？
            LogUtils.LOGD(LOG_TAG, "smsBody=" + smsBody + ",smsAddress=" + smsAddress + ",time=" + time);
            //保存到本地数据库
            SmsInfo info = new SmsInfo();
            info.setHasUpload(false);
            info.setBody(smsBody);
            info.setDate(time);
            info.setPhoneNumber(smsAddress);
            mDao.add(info);
            //上传短信
            Message msg = mHandler.obtainMessage(MESSAGE_WHAT_PUSH);
            Bundle bundle = new Bundle();
            bundle.putString(Config.INTENT_KEY_BODY, info.getBody());
            bundle.putLong(Config.INTENT_KEY_TIME, info.getDate());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    };

    //保存最后一条短信
    private void saveLatestSmsData(String address, long time) {
        if (!TextUtils.isEmpty(address)) {
            SPFUtils.put(Config.SPF_FILE_NAME, this, Config.SPF_ADDRESS, address);
        }
        if (time >= 0) {
            SPFUtils.put(Config.SPF_FILE_NAME, this, Config.SPF_LATEST_DATE, time);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WHAT_PUSH:
                    Bundle bundle = msg.getData();
                    String body = bundle.getString(Config.INTENT_KEY_BODY);
                    long time = bundle.getLong(Config.INTENT_KEY_TIME);
                    pushToRequest(body, time);
                    break;
            }
        }
    };


    private void pushToRequest(String body, long time) {

        /*MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart(Config.PARAM_BODY, body);*/
        LogUtils.LOGD(LOG_TAG, Config.PARAM_BODY + "=" + body);
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(Config.PARAM_BODY, body);

        final Request request = new Request.Builder()
                .url(Config.PUSH_URL)
                .post(builder.build())
                .tag(time)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(mCallback);
    }

    private Callback mCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //TODO：不做处理？
            LogUtils.LOGD(LOG_TAG, "onFailure");
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            //TODO：将数据库中此条数据删除
            LogUtils.LOGD(LOG_TAG, response.body().string());
            long time = (long) call.request().tag();
            saveLatestSmsData(Config.DEFAULT_ADDRESS, time);
            mDao.deleteForTime(time);
        }
    };
}
