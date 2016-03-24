package priv.liuxy.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by Liuxy on 2015/10/13.
 * <p>
 * SharedPreferences 统一管理类
 */
public class SPFUtils {
    /**
     * SharedPreferences 文件名
     */
    private static final String FILE_NAME = "AutoService";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context 上下文
     * @param key     键
     * @param object  存储值
     * @return 返回true如果成功保存
     */

    public static boolean put(Context context, String key, Object object) {
        return put(FILE_NAME, context, key, object);
    }

    public static boolean put(String fileName, Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        return editor.commit();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context       上下文
     * @param key           键
     * @param defaultObject 返回的默认值
     * @return 返回键对应的值，找不到返回null
     */
    public static Object get(Context context, String key, Object defaultObject) {
        return get(FILE_NAME, context, key, defaultObject);
    }

    public static Object get(String fileName, Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 移除某个key值已经对应的键值对
     *
     * @param context 上下文
     * @param key     键
     * @return 返回true如果成功删除
     */
    public static boolean remove(Context context, String key) {
        return remove(FILE_NAME, context, key);
    }

    public static boolean remove(String fileName, Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        return editor.commit();
    }

    /**
     * 清除所有数据
     *
     * @param context 上下文
     * @return 返回true如果成功删除成功
     */
    public static boolean clear(Context context) {
        return clear(FILE_NAME, context);
    }

    public static boolean clear(String fileName, Context context) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        return editor.commit();
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context 上下文
     * @param key     键
     * @return 返回true如果key存在
     */
    public static boolean contains(Context context, String key) {
        return contains(FILE_NAME, context, key);
    }

    public static boolean contains(String fileName, Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context 上下文
     * @return 返回所有的键值对
     */
    public static Map<String, ?> getAll(Context context) {
        return getAll(FILE_NAME, context);
    }

    public static Map<String, ?> getAll(String fileName, Context context) {
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }
}

