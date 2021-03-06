package priv.liuxy.collagedemo.templates;

import android.graphics.Rect;

import priv.liuxy.collagedemo.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyang.liu on 17-7-3.
 * <p>
 * <p>
 * ┌─────────┐
 * │    0    │
 * ├─────────┤
 * │    1    │
 * └─────────┘
 */

public class CollageStyle3 extends CollageTemplate {
    public static final String STYLE_ID = "CollageStyle_3";

    public static int CHILD_COUNT = 2;

    public CollageStyle3(Size size) {
        super(size);
    }

    @Override
    public String getID() {
        return STYLE_ID;
    }

    @Override
    protected List<Rect> initAreas() {
        List<Rect> list = new ArrayList<>();
        for (int i = 0; i < CHILD_COUNT; i++) {
            Rect r = new Rect(0, i * mSize.getHeight() / CHILD_COUNT,
                    mSize.getWidth(), (i + 1) * mSize.getHeight() / CHILD_COUNT);
            list.add(r);
        }
        return list;
    }

    @Override
    public Direction getMovableDirection(int index) {
        if (index >= 0 && index < getChildCount()) {
            return Direction.VER;
        }
        return Direction.FIXED;
    }
}
