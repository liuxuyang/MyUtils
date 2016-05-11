package priv.liuxy.pushsmsdome.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.SmsMessage;

import priv.liuxy.pushsmsdome.ISmsAidlInterface;
import priv.liuxy.utils.LogUtils;

/**
 * @author Liuxy
 *         监听短信广播
 */
public class SMSReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = LogUtils.makeLogTag(SMSReceiver.class);

    private static final String PDUS = "pdus";
    //短信内容
    private String body;
    //收到短信时间
    private String address;
    //发件人地址
    private long time;

    public SMSReceiver() {
        LogUtils.LOGD(LOG_TAG, "create MyReceiver instance");

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.LOGD(LOG_TAG, "onReceive");
        body = "";
        address = "";
        time = 0L;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            initData(bundle);
            transferData(context);
        }
    }

    /**
     * 初始化数据
     *
     * @param bundle 数据bundle
     */
    private void initData(Bundle bundle) {
        SmsMessage smsMessage;
        if (bundle != null) {
            Object[] objects = (Object[]) bundle.get(PDUS);

            //获取短信内容
            for (Object object : objects != null ? objects : new Object[0]) {
                smsMessage = SmsMessage.createFromPdu((byte[]) object);
                //短信内容可能过长。
                body += smsMessage.getDisplayMessageBody();
                time = smsMessage.getTimestampMillis();
                address = smsMessage.getDisplayOriginatingAddress();
            }
            LogUtils.LOGD(LOG_TAG, "address = " + address + ",body=" + body + ",time=" + String.valueOf(time));
        }
    }

    /**
     * 传递数据
     *
     * @param context 上下文
     */
    private void transferData(Context context) {

        if (matchSMS()) {
            IBinder iBinder = peekService(context, new Intent(context, SMSService.class));
            if (iBinder == null) {
                LogUtils.LOGD(LOG_TAG, "IBinder is null");
                //TODO:保存到数据库，service未bind,故取不到IBinder（google 工程师提供，官方文档未做说明，经验证确实需要bind生成IBinder对象）,
            } else {
                ISmsAidlInterface aidlInterface = ISmsAidlInterface.Stub.asInterface(iBinder);
                try {
                    aidlInterface.pushData(body, address, time);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    //TODO:远程调用出错，保存到数据库
                }
            }
        }
    }

    /**
     * 筛选短信
     *
     * @return
     */
    private boolean matchSMS() {

        //
        //TODO:筛选短信
        /*return address.equals(Config.ADDRESS)*/

        return true;
    }
}
