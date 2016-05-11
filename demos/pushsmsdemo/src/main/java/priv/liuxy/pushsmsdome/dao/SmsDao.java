package priv.liuxy.pushsmsdome.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import priv.liuxy.pushsmsdome.Config;
import priv.liuxy.pushsmsdome.bean.SmsInfo;
import priv.liuxy.utils.LogUtils;

/**
 * Created by Liuxy on 2016/4/19.
 */
public class SmsDao {
    private static final String LOG_TAG = LogUtils.makeLogTag(SmsDao.class);

    private Context mContext;
    private DataBaseHelper mHelper;
    private Dao<SmsInfo, Integer> mDao;

    public SmsDao(Context context) {
        mContext = context;
        try {
            mHelper = DataBaseHelper.getInstance(context);
            mDao = mHelper.getDao(SmsInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add(SmsInfo info) {
        try {
            mDao.createIfNotExists(info);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAll(List<SmsInfo> infos) {
        for (SmsInfo info : infos) {
            add(info);
        }
    }

    public void update(SmsInfo info) {
        try {
            mDao.update(info);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SmsInfo> readAll() {
        List<SmsInfo> smsInfos = null;
        try {
            smsInfos = mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return smsInfos;
    }

    public void deleteForTime(long time) {
        try {
            mDao.queryForEq(Config.FIELD_TIME, time);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
