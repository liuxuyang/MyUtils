package priv.liuxy.collagedemo.templates;

import android.graphics.Rect;

import priv.liuxy.collagedemo.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyang.liu on 17-7-3.
 * <p>
 * this template like this:
 * ┌────┬────┐
 * │ 0  │    │
 * ├────┤ 2  │
 * │ 1  │    │
 * └────┴────┘
 */

public class CollageStyle1 extends CollageTemplate {
    public static final String STYLE_ID = "CollageStyle_1";

    public CollageStyle1(Size size) {
        super(size);
    }

    @Override
    public String getID() {
        return STYLE_ID;
    }

    @Override
    protected List<Rect> initAreas() {
        List<Rect> list = new ArrayList<>();
        Rect r0 = new Rect(0, 0, mSize.getWidth() / 2, mSize.getHeight() / 2);
        Rect r1 = new Rect(0, mSize.getHeight() / 2,
                mSize.getWidth() / 2, mSize.getHeight());
        Rect r2 = new Rect(mSize.getWidth() / 2, 0, mSize.getWidth(), mSize.getHeight());
        list.add(r0);
        list.add(r1);
        list.add(r2);
        return list;
    }

    @Override
    public Direction getMovableDirection(int index) {
        if (index == 2) {
            return Direction.HOR;
        } else if (index == 0 || index == 1) {
            return Direction.VER;
        }
        return Direction.FIXED;
    }

}
