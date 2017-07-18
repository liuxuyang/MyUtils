package com.liuxy.tct;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyang.liu on 17-7-3.
 */

public class CollageManager {
    public static final int BACKGROUND_COLOR = 0xFF0D0D0D;
    public static final int DIVIDER_WIDTH = 2;

    private static final String TAG = "CollageManager";

    private List<Item> mItems;

    private Size mLayoutSize;

    public CollageManager(Size size) {
        mLayoutSize = size;
        Log.d(TAG, "collage init & size : " + mLayoutSize);
    }

    public Size getLayoutSize() {
        return mLayoutSize;
    }

    public void setLayoutSize(Size layoutSize) {
        mLayoutSize = layoutSize;
    }


    public List<Item> getItems() {
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        return mItems;
    }

    public void updateItem(int index, Rect area, Rect rect) {
        if (mItems == null) {
            return;
        }
        for (Item item : mItems) {
            if (item.getIndex() == index) {
                item.setPreviewArea(area);
                item.setBitmapArea(rect);
            }
        }
    }

    public int getItemSize() {
        if (mItems == null) {
            return 0;
        }
        return mItems.size();
    }

    public void reset() {
        if (mItems != null) {
            for (Item i : mItems) {
                i.getBitmap().recycle();
            }
            mItems = null;
        }
        mLayoutSize = null;
    }

    public Bitmap merge(Size size, Bitmap.Config config) {
        if (mItems == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight(), config);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(BACKGROUND_COLOR);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        for (Item item : mItems) {
            Rect preview = new Rect(item.getPreviewArea().left + DIVIDER_WIDTH,
                    item.getPreviewArea().top + DIVIDER_WIDTH,
                    item.getPreviewArea().right - DIVIDER_WIDTH,
                    item.getPreviewArea().bottom - DIVIDER_WIDTH);
            Rect dst = transform(preview, mLayoutSize, size);
            if (dst == null) {
                return null;
            }
            Log.d(TAG, "dst : " + dst);
            canvas.drawBitmap(item.getBitmap(), item.getBitmapArea(), dst, paint);
        }
        Log.d(TAG, "merge bitmap's size : " + bitmap.getWidth() + "x" + bitmap.getHeight());
        return bitmap;
    }

    public static class Item {
        private int mIndex;

        private Rect mPreviewArea;

        private Bitmap mBitmap;

        private Rect mBitmapArea;

        public Item(int index, Size previewSize, Rect area, Bitmap src) {
            this.mIndex = index;
            this.mPreviewArea = area;
            Size bitmapSize = new Size(src.getWidth(), src.getHeight());
            Rect rect = transform(getPreviewArea(), previewSize, bitmapSize);
            this.mBitmap = Bitmap.createBitmap(src,
                    rect.left, rect.top, rect.width(), rect.height(),
                    new Matrix(), false);
        }

        public int getIndex() {
            return mIndex;
        }

        public Rect getPreviewArea() {
            return mPreviewArea;
        }

        public void setPreviewArea(Rect previewArea) {
            mPreviewArea = previewArea;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public Rect getBitmapArea() {
            return mBitmapArea;
        }

        public void setBitmapArea(Rect bitmapArea) {
            mBitmapArea = bitmapArea;
        }
    }

    //preview's rect => result bitmap's rect
    private static Rect transform(Rect preview, Size src, Size dst) {
        if (preview == null
                || src == null || src.isEmpty() || !src.isSquare()
                || dst == null || dst.isEmpty() || !dst.isSquare()) {
            return null;
        }
        float ratio = dst.getWidth() * 1.0f / src.getWidth();

        return new Rect((int) (preview.left * ratio),
                (int) (preview.top * ratio),
                (int) (preview.right * ratio),
                (int) (preview.bottom * ratio));
    }
}
