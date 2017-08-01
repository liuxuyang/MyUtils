package priv.liuxy.collagedemo.templates;

import android.graphics.Rect;

import priv.liuxy.collagedemo.Size;

import java.util.List;

/**
 * Created by xuyang.liu on 17-7-3.
 */

public abstract class CollageTemplate {
    public static final String TAG = "CollageStyle_";

    protected String mID;

    protected Size mSize;

    protected List<Rect> mItemAreas;

    public CollageTemplate(Size size) {
        this.mSize = size;
        this.mItemAreas = initAreas();
    }

    public abstract String getID();

    protected abstract List<Rect> initAreas();

    public abstract Direction getMovableDirection(int index);

    public Rect getItemArea(int index) {
        if (index < 0 || index >= getChildCount()) {
            return null;
        }
        return mItemAreas.get(index);
    }

    public int getChildCount() {
        return mItemAreas.size();
    }

    public void reset() {
        mSize = null;
        mItemAreas = null;
    }

    public enum Direction {
        FIXED, VER, HOR
    }
}
