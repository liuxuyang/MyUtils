package priv.liuxy.collagedemo;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * Created by xuyang.liu on 17-7-13.
 */

public interface CollageItem {

    boolean isInPreview();

    void setInPreview(boolean inPreview);

    void setImageBitmap(Bitmap bitmap);

    void clear();

    Matrix getImageMatrix();

    void setImageMatrix(Matrix imageMatrix);

    Rect getBitmapRect();
}
