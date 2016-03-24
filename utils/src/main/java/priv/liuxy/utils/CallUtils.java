package priv.liuxy.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Liuxy on 2016/3/22.
 */
public class CallUtils {
    private static final String TAG = LogUtils.makeLogTag(CallUtils.class);
    /**
     * 手机号码正则
     */
    private static final String MOBILE_PHONE_NUMBER_REG = "(0|86|17951)?(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$*";
    /**
     * 座机号码正则
     */
    private static final String PHONE_NUMBER_REG = "(0\\d{2}-\\d{8})|(0\\d{3}-\\d{8})|(0\\d{3}-\\d{7})|(\\d{10})|(\\d{3}-\\d{3}-\\d{4})$*";

    /**
     * 拨打电话
     *
     * @param context
     * @param phoneNumber
     */
    public static void call(Context context, String phoneNumber) {
        String s = getPhoneNumber(phoneNumber);
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(context, R.string.phone_number_is_error, Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtils.LOGD("phone number = ", phoneNumber + "=>" + s);
        Uri uri = Uri.parse("tel:" + s);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (context instanceof Activity) {
                ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CALL_PHONE);
            }
            return;
        }
        context.startActivity(new Intent(Intent.ACTION_CALL, uri));
    }

    /**
     * 取得手机号码
     *
     * @return 配到的号码，没有的话返回null
     */
    public static String getPhoneNumber(String s) {
        Pattern pattern = Pattern.compile(MOBILE_PHONE_NUMBER_REG);
        Matcher matcher = pattern.matcher(s);

        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            buffer.append(matcher.group()).append(",");
        }
        int len = buffer.length();
        if (len > 0) {
            buffer.deleteCharAt(len - 1);
            String[] strings = buffer.toString().split(",");
            return strings[0];
        } else {//处理座机
            Pattern p = Pattern.compile(PHONE_NUMBER_REG);//座机正则
            Matcher m = p.matcher(s);
            while (m.find()) {
                buffer.append(m.group()).append(",");
            }
            len = buffer.length();
            if (len > 0) {
                String[] strings = buffer.toString().split(",");
                return strings[0];
            } else {
                return null;
            }
        }
    }
}
