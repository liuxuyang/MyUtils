package priv.liuxy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by Liuxy on 2015/10/13.
 * <p/>
 * 网络相关工具类
 * 需要相关权限：ACCESS_NETWORK_STATE
 */
public class NetUtils {
    private static final String TAG = LogUtils.makeLogTag(NetUtils.class);

    private NetUtils() {
        /** cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断网络是否连接
     *
     * @param context 上下文
     * @return 返回true如果有网络连接
     */
    public static boolean isConnecting(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     *
     * @param context 上下文
     * @return 返回true如果连接类型是wifi
     */
    public static boolean isWifiConnecting(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cm) {
            return false;
        }
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 打开网络设置
     *
     * @param activity
     * @param requestCode
     */
    public static void openNetSetting(Activity activity, int requestCode) {
        Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取本地IP地址
     *
     * @return
     */
    public static String getIP() {
        try {
            for (final Enumeration<NetworkInterface> enumerationNetworkInterface = NetworkInterface.getNetworkInterfaces(); enumerationNetworkInterface.hasMoreElements(); ) {
                final NetworkInterface networkInterface = enumerationNetworkInterface.nextElement();
                for (Enumeration<InetAddress> enumerationInetAddress = networkInterface.getInetAddresses(); enumerationInetAddress.hasMoreElements(); ) {
                    final InetAddress inetAddress = enumerationInetAddress.nextElement();
                    final String ip = inetAddress.getHostAddress();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return ip;
                    }
                }
            }
            return "";
        } catch (final Exception e) {
            LogUtils.LOGD(TAG, "获取本地ip地址失败");
            e.printStackTrace();
            return "";
        }
    }
}
