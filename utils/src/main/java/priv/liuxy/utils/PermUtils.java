package priv.liuxy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.List;

/**
 * Created by Liuxy on 2016/4/18.
 */
public class PermUtils {
    private static final String LOG_TAG = LogUtils.makeLogTag(PermUtils.class);

    public static void printAllPermission(Context context) {

        StringBuilder appNameAndPermissions = new StringBuilder();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                if (packageInfo.packageName.equals(context.getPackageName())) {
                    appNameAndPermissions.append(packageInfo.packageName).append("\n");
                    String[] requestedPermissions = packageInfo.requestedPermissions;
                    if (requestedPermissions != null) {
                        for (String requestedPermission : requestedPermissions) {
                            LogUtils.LOGD(LOG_TAG, requestedPermission);
                            appNameAndPermissions.append(requestedPermission).append("\n");
                        }
                        appNameAndPermissions.append("\n");
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        LogUtils.LOGD(LOG_TAG, appNameAndPermissions.toString());
    }

    public static void checkPermission(final Activity activity, final String permName, final int requestCode, String explanation) {
        if (ContextCompat.checkSelfPermission(activity, permName) != PackageManager.PERMISSION_GRANTED) {
            /**
             * 第一次请求权限时，shouldShowRequestPermissionRationale总是返回false,
             * 如果用户以前拒绝了一个请求，这个方法将返回true。
             * 我们需要告诉用户为什么我们需要这个权限，然后再进行权限请求。
             * onRequestPermissionsResult中处理用户的选择
             */
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permName)) {
                DialogUtils.showAlertDialog(activity, explanation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, new String[]{permName}, requestCode);
                    }
                });
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permName}, requestCode);
            }
        } else {
            //TODO:按照旧的权限管理方式进行
        }
    }

}
