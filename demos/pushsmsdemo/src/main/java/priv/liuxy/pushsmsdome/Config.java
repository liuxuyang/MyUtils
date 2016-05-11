package priv.liuxy.pushsmsdome;

/**
 * Created by Liuxy on 2016/4/18.
 */
public class Config {
    /**
     * 上传的url
     */
    public static final String PUSH_URL = "http://192.168.1.141:8087/code/message/short-message!saveData.action";
    /**
     *
     */
    public static final String PARAM_BODY = "messageData";

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
     * 短信信息结构
     */
    public static final String[] projection = new String[]{"_id", "address", "body", "date"};

    /**
     * shared preferences file name
     */
    public static final String SPF_FILE_NAME = "suncar_sms_cache";
    /**
     * shared preferences key name
     */
    public static final String SPF_LATEST_DATE = "latest";
    public static final String SPF_ADDRESS = "address";

    /**
     * intent key name
     */
    public static final String INTENT_KEY_SMS_INFO = "sms info";
    public static final String INTENT_KEY_BODY = "body";
    public static final String INTENT_KEY_ADDRESS = "address";
    public static final String INTENT_KEY_TIME = "time";

    /**
     * database
     */
    public static final String DATABASE_NAME = "sms.db";
    public static final int DATABASE_VERSION = 1;

    public static final String FIELD_BODY = "body";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_TIME = "time";
    public static final String FIELD_UPLOAD = "has_upload";

    /**
     * 默认的筛选号码
     */
    public static final String DEFAULT_ADDRESS = "";
}
