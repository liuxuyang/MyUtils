package priv.liuxy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

/**
 * Created by Liuxy on 2016/3/17.
 */
public class BitmapUtils {

    /**
     * 由.9图获取指定大小的bitmap
     *
     * @param resId   资源id
     * @param context 上下文
     * @param width   宽
     * @param height  高
     * @param config  图片配置
     * @return 指定大小的bitmap
     * @throws IllegalArgumentException
     */
    public static Bitmap ninePatchDrawable2Bitmap(int resId, Context context, int width, int height, Bitmap.Config config) throws IllegalArgumentException {
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        NinePatchDrawable drawable = (NinePatchDrawable) context.getResources().getDrawable(resId);
        return ninePatchDrawable2Bitmap(drawable, width, height, config);
    }

    /**
     * ninePatchDrawable to bitmap
     */
    public static Bitmap ninePatchDrawable2Bitmap(NinePatchDrawable drawable, int width, int height, Bitmap.Config config) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        if (drawable == null) {
            throw new IllegalArgumentException("drawable is null");
        }
        drawable.setBounds(0, 0, width, height);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * drawable to bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }
}
