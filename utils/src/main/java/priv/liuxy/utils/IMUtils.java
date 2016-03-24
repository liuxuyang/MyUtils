package priv.liuxy.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Liuxy on 2015/12/21.
 * 软键盘工具
 */
public class IMUtils {
    private static InputMethodManager imm;

    private static InputMethodManager getManager(Context context) {
        if (null == imm) {
            synchronized (InputMethodManager.class) {
                if (null == imm) {
                    imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                }
            }
        }
        return imm;
    }

    /**
     * 软键盘是否显示
     */
    public static boolean isShow(Context context, View view) {
        return getManager(context).isActive(view);
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param view
     */
    public static void hide(Context context, View view) {
        getManager(context).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示/隐藏软键盘
     *
     * @param context
     */
    public static void show(Context context, View view) {
        getManager(context).showSoftInput(view, 0);
    }

}
