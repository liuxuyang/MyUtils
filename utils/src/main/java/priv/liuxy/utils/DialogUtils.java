package priv.liuxy.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;

/**
 * Created by Liuxy on 2016/3/17.
 */
public class DialogUtils {

    /**
     * 显示有确认/取消按钮的对话框
     *
     * @param context
     * @param msgRes
     * @param positiveListener
     * @param negativeListener
     */
    public static void show2BtnDialog(Context context, int msgRes, DialogInterface.OnClickListener positiveListener, @Nullable DialogInterface.OnClickListener negativeListener) {
        String msg = context.getResources().getString(msgRes);
        show2BtnDialog(context, msg, positiveListener, negativeListener);
    }

    public static void show2BtnDialog(Context context, CharSequence msg, DialogInterface.OnClickListener positiveListener, @Nullable DialogInterface.OnClickListener negativeListener) {
        if (negativeListener == null) {
            negativeListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.positive_button, positiveListener);
        builder.setNegativeButton(R.string.negative_button, negativeListener);
        builder.show();
    }

    /**
     * 显示有确认按钮的对话框
     *
     * @param context
     * @param msgRes
     * @param positiveListener
     */
    public static void showAlertDialog(Context context, int msgRes, DialogInterface.OnClickListener positiveListener) {
        String msg = context.getResources().getString(msgRes);
        showAlertDialog(context, msg, positiveListener);
    }

    public static void showAlertDialog(Context context, CharSequence msg, DialogInterface.OnClickListener positiveListener) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.positive_button, positiveListener);
        builder.show();
    }
}
