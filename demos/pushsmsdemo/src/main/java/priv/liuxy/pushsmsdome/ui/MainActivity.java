package priv.liuxy.pushsmsdome.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import priv.liuxy.pushsmsdome.Config;
import priv.liuxy.pushsmsdome.R;
import priv.liuxy.pushsmsdome.bean.SmsInfo;
import priv.liuxy.pushsmsdome.component.SMSService;
import priv.liuxy.utils.DialogUtils;
import priv.liuxy.utils.LogUtils;
import priv.liuxy.utils.SPFUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = LogUtils.makeLogTag(MainActivity.class);
    private Activity self = MainActivity.this;
    /**
     * 短信相关权限
     */
    private static final String PERMISSION_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
    private static final String PERMISSION_READ_SMS = Manifest.permission.READ_SMS;
    /**
     * 权限请求回执code
     */
    private static final int REQUEST_SMS_CODE = 0x0001;
    /**
     * Loader回执code
     */
    private static final int ALL_SMS_LOADER = REQUEST_SMS_CODE << 2;
    /**
     * 符合条件的短信总和
     */
    private List<SmsInfo> mSmsInfoList;

    private String mAddress;

    private long mLatestDate;

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //开启Service
        startSmsService();
        init();
        checkPermission(PERMISSION_RECEIVE_SMS, REQUEST_SMS_CODE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    /**
     * 初始化
     */
    private void init() {
        mSmsInfoList = new ArrayList<>();
        mAddress = (String) SPFUtils.get(Config.SPF_FILE_NAME, this, Config.SPF_ADDRESS, Config.DEFAULT_ADDRESS);
        mLatestDate = (long) SPFUtils.get(Config.SPF_FILE_NAME, this, Config.SPF_LATEST_DATE, 0L);
    }

    /**
     * 启动service
     */
    private void startSmsService() {
        mIntent = new Intent();
        mIntent.setClass(this, SMSService.class);
        startService(mIntent);
    }

    /**
     * 绑定service，已取得IBinder
     */
    private void bindSmsService() {
        mIntent.putParcelableArrayListExtra(Config.INTENT_KEY_SMS_INFO, (ArrayList<? extends Parcelable>) mSmsInfoList);
        bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 服务连接
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.LOGD(LOG_TAG, "onServiceConnected");
            //TODO:获取IBinder对象
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.LOGD(LOG_TAG, "onServiceDisconnected");
            //TODO：bind失败处理
        }
    };

    private void loadSms() {
        getSupportLoaderManager().initLoader(ALL_SMS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> cursorLoader = null;
        switch (id) {
            case ALL_SMS_LOADER:
                cursorLoader = new CursorLoader(
                        this,   // Parent activity context
                        Uri.parse(Config.SMS_URI_INBOX),        // Table to query
                        Config.projection,     // Projection to return
                        createWhere(mAddress, mLatestDate),            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        LogUtils.LOGD(LOG_TAG, "onLoadFinished");
        if (data == null) {
            return;
        }
        int phoneNumberColumn = data.getColumnIndex("address");
        int smsBodyColumn = data.getColumnIndex("body");
        long dateColumn = data.getColumnIndex("date");
        while (data.moveToNext()) {
            SmsInfo smsinfo = new SmsInfo();
            smsinfo.setDate(dateColumn);
            smsinfo.setPhoneNumber(data.getString(phoneNumberColumn));
            smsinfo.setBody(data.getString(smsBodyColumn));
            smsinfo.setHasUpload(false);
            mSmsInfoList.add(smsinfo);
        }
        bindSmsService();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        LogUtils.LOGD(LOG_TAG, "onLoaderReset");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SMS_CODE: {
                //如果请求被拒绝，grantResults将会是空的
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO：请求权限被允许，调起service
                    LogUtils.LOGD(LOG_TAG, "request success");
                    loadSms();
                } else {
                    //TODO:请求权限被拒绝，显示提示信息
                    LogUtils.LOGD(LOG_TAG, "request fail");
                }
                return;
            }

        }
    }

    /**
     * 检查权限
     *
     * @param permName    需请求的权限名
     * @param requestCode 请求code
     */
    private void checkPermission(final String permName, final int requestCode) {
        if (ContextCompat.checkSelfPermission(self, permName) != PackageManager.PERMISSION_GRANTED) {
            /**
             * 第一次请求权限时，shouldShowRequestPermissionRationale总是返回false,
             * 如果用户以前拒绝了一个请求，这个方法将返回true。
             * 我们需要告诉用户为什么我们需要这个权限，然后再进行权限请求。
             */
            if (ActivityCompat.shouldShowRequestPermissionRationale(self, permName)) {
                DialogUtils.showAlertDialog(self, "", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(self, new String[]{permName}, requestCode);
                    }
                });
            } else {
                ActivityCompat.requestPermissions(self, new String[]{permName}, requestCode);
            }
        } else {
            //TODO:拥有权限，调起service
            loadSms();
        }
    }

    /**
     * 生成短信筛选条件
     */
    private String createWhere(String address, long timeMillis) {
        String where;
        if (TextUtils.isEmpty(address)) {
            where = " date > " + timeMillis;
        } else {
            where = " address = '" + address + "' AND date > " + timeMillis;
        }
        return where;
    }
}
