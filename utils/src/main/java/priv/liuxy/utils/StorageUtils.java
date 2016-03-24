package priv.liuxy.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by Liuxy on 2016/3/14.
 * <p/>
 * 外部储存相关工具类
 * <p/>
 * 相关权限：
 */
public class StorageUtils {

    /**
     * 判断SD卡等外部储存是否可读
     */
    public static boolean isExternalStorageReadOnly() {
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }

    /**
     * 判断SD卡等外部储存是否可写
     */
    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 判断SD卡等外部储存容量是否充足
     */
    public static boolean isExternalStorageEnough(int length) {

        File path = Environment.getExternalStorageDirectory();
        StatFs statfs = new StatFs(path.getPath());

        long size;// 获取block的SIZE
        long availBlock;// 空闲的Block的数量
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            size = statfs.getBlockSizeLong();
            availBlock = statfs.getAvailableBlocksLong();
        } else {
            size = statfs.getBlockSize();
            availBlock = statfs.getAvailableBlocks();
        }
        return availBlock * size >= (length + 1024 * 10);
    }

    /**
     * 该方法会判断当前sd卡是否存在，然后选择缓存地址
     *
     * @param context
     * @param uniqueName
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }
}
