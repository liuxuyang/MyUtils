package priv.liuxy.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liuxy on 2016/4/18.
 */
public class SMSUtils {
    /**
     * 所有的短信
     */
    public static final String SMS_URI_ALL = "content://sms/";

    /**
     * 收件箱短信
     */
    public static final String SMS_URI_INBOX = "content://sms/inbox";

    /**
     * 已发送短信
     */
    public static final String SMS_URI_SEND = "content://sms/sent";

    /**
     * 草稿箱短信
     */
    public static final String SMS_URI_DRAFT = "content://sms/draft";

    /**
     * 取得短信数据库数据，通过新的API
     *
     * @param context
     * @return
     */
    public static List<SmsInfo> getDataWithNewApi(Context context) {
        List<SmsInfo> infos = new ArrayList<>();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        CursorLoader loader = new CursorLoader(context, Uri.parse(SMS_URI_ALL), projection, null, null, "date desc");

        Cursor cursor = loader.loadInBackground();

        int nameColumn = cursor.getColumnIndex("person");
        int phoneNumberColumn = cursor.getColumnIndex("address");

        int smsBodyColumn = cursor.getColumnIndex("body");

        int dateColumn = cursor.getColumnIndex("date");

        int typeColumn = cursor.getColumnIndex("type");
        if (cursor != null) {
            while (cursor.moveToNext()) {

                SmsInfo smsinfo = new SmsInfo();

                smsinfo.setName(cursor.getString(nameColumn));

                smsinfo.setDate(cursor.getString(dateColumn));

                smsinfo.setPhoneNumber(cursor.getString(phoneNumberColumn));

                smsinfo.setSmsbody(cursor.getString(smsBodyColumn));

                smsinfo.setType(cursor.getString(typeColumn));

                infos.add(smsinfo);

            }
            if (Build.VERSION.SDK_INT < 14) {
                cursor.close();
            }
        }
        return infos;
    }

    /**
     * 取得短信数据库数据
     *
     * @param activity
     * @return
     */
    public static List<SmsInfo> getData(Activity activity) {
        List<SmsInfo> infos = new ArrayList<>();

        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cursor = activity.managedQuery(Uri.parse(SMS_URI_ALL), projection, null, null, "date desc");


        int nameColumn = cursor.getColumnIndex("person");

        int phoneNumberColumn = cursor.getColumnIndex("address");

        int smsBodyColumn = cursor.getColumnIndex("body");

        int dateColumn = cursor.getColumnIndex("date");

        int typeColumn = cursor.getColumnIndex("type");
        if (cursor != null) {
            while (cursor.moveToNext()) {

                SmsInfo smsinfo = new SmsInfo();

                smsinfo.setName(cursor.getString(nameColumn));

                smsinfo.setDate(cursor.getString(dateColumn));

                smsinfo.setPhoneNumber(cursor.getString(phoneNumberColumn));

                smsinfo.setSmsbody(cursor.getString(smsBodyColumn));

                smsinfo.setType(cursor.getString(typeColumn));

                infos.add(smsinfo);

            }
            if (Build.VERSION.SDK_INT < 14) {
                cursor.close();
            }
        }
        return infos;
    }

    public static class SmsInfo {
        /**
         * 短信内容
         */
        private String smsbody;

        /**
         * 发送短信的电话号码
         */

        private String phoneNumber;

        /**
         * 发送短信的日期和时间
         */

        private String date;

        /**
         * 发送短信人的姓名
         */

        private String name;

        /**
         * 短信类型1是接收到的，2是已发出
         */
        private String type;

        public String getSmsbody() {
            return smsbody;
        }

        public void setSmsbody(String smsbody) {
            this.smsbody = smsbody;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
