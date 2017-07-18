package com.liuxy.tct.templates;

import android.graphics.Rect;

import com.liuxy.tct.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyang.liu on 17-7-3.
 * <p>
 * <p>
 * ┌─────────┐  0
 * ├─────────┤  1
 * ├─────────┤  2
 * ├─────────┤  3
 * └─────────┘
 */

public class CollageStyle2 extends CollageTemplate {
    public static final String STYLE_ID = "CollageStyle_2";

    public static int CHILD_COUNT = 4;

    public CollageStyle2(Size size) {
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
