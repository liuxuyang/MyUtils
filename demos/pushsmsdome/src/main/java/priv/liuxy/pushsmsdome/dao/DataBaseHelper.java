package priv.liuxy.pushsmsdome.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import priv.liuxy.pushsmsdome.Config;
import priv.liuxy.pushsmsdome.bean.SmsInfo;
import priv.liuxy.utils.LogUtils;

/**
 * Created by Liuxy on 2016/4/19.
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String LOG_TAG = LogUtils.makeLogTag(DataBaseHelper.class);

    private static DataBaseHelper mInstance;

    public static DataBaseHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DataBaseHelper.class) {
                if (mInstance == null) {
                    mInstance = new DataBaseHelper(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public DataBaseHelper(Context context) {
        super(context, Config.DATABASE_NAME, null, Config.DATABASE_VERSION);
        LogUtils.LOGD(LOG_TAG, "constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        LogUtils.LOGD(LOG_TAG, "onCreate");
        try {
            TableUtils.createTable(connectionSource, SmsInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        LogUtils.LOGD(LOG_TAG, "onUpgrade");
        try {
            TableUtils.dropTable(connectionSource, SmsInfo.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
