package com.liuxy.tct;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.liuxy.tct.templates.CollageStyle1;
import com.liuxy.tct.templates.CollageStyle2;
import com.liuxy.tct.templates.CollageStyle3;
import com.liuxy.tct.templates.CollageStyle4;
import com.liuxy.tct.templates.CollageTemplate;

/**
 * Created by xuyang.liu on 17-6-28.
 */

public class CollageLayout extends ViewGroup implements
        CollageController {
    public static final int INVALID_INDEX = -1;
    private static final String TAG = "CollageLayout";
    private static final long SWAP_ANIMATOR_DURATION = 500;

    private boolean isDebug = true;

    private int mState = STATE_IDLE;

    private String mStyle;
    private CollageTemplate mTemplate;
    private CollageManager mCollage;
    private Size mSize;
    private CollageListener mCollageListener;

    private Point mPrePoint;
    private Point mCurPoint;
    private int mOffsetX;
    private int mOffsetY;
    private int mSelectViewIndex;
    private int mCoverViewIndex;
    private int mPreAreaIndex;
    private int mCurAreaIndex;

    public CollageLayout(Context context) {
        this(context, null);
    }

    public CollageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.collage_layout);
        int index = array.getInt(R.styleable.collage_layout_collage_style, 1);
        array.recycle();

        mStyle = CollageTemplate.TAG + index;
    }

    @Override
    public void setCollageListener(CollageListener listener) {
        this.mCollageListener = listener;
    }

    @Override
    public String getStyle() {
        return mTemplate.getID();
    }

    @Override
    public void setStyle(String style) {
        if (!mStyle.equals(style)) {
            mStyle = style;
            if (mSize == null) {
                Log.e(TAG, "layout size is null");
                return;
            }
            updateStyle();
            requestLayout();
        }
    }

    public void reset() {
        if (mCollage != null) {
            mCollage.reset();
            mCollage = null;
        }
        if (mTemplate != null) {
            mTemplate.reset();
            mTemplate = null;
        }
        if (mState != STATE_IDLE) {
            setState(STATE_IDLE);
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof CollageItem) {
                ((CollageItem) getChildAt(i)).clear();
            }
        }
        removeAllViews();
        init(mSize);
    }


    @Override
    public int getCurCaptureIndex() {
        if (mCollage == null) {
            return INVALID_INDEX;
        }
        return mCollage.getItemSize();
    }

    @Override
    public boolean addImage(int id) {
        return addImage(BitmapFactory.decodeResource(getResources(), id));
    }

    @Override
    public boolean addImage(Bitmap bitmap) {
        if (mState != STATE_CAPTURE) {
            return false;
        }
        if (mTemplate == null || mCollage == null || !checkState()) {
            return false;
        }
        int index = mCollage.getItemSize();
        CollageManager.Item item = new CollageManager.Item(index, mSize, mTemplate.getItemArea(index), bitmap);
        bitmap.recycle();
        mCollage.getItems().add(item);

        //set cur view src
        CollageItem collageItem = getChildItem(index);
        if (collageItem != null) {
            collageItem.setInPreview(false);
            collageItem.setImageBitmap(item.getBitmap());
        }

        //set next preview child
        if (mCollage.getItemSize() < mTemplate.getChildCount()) {
            CollageItem previewItem = getChildItem(mCollage.getItemSize());
            if (previewItem != null) {
                previewItem.setInPreview(true);
            }
        } else {
            setState(STATE_CAPTURE_COMPLETE);
        }
        return true;
    }

    @Override
    public boolean removeImage() {
        if (mState != STATE_CAPTURE && mState != STATE_CAPTURE_COMPLETE) {
            return false;
        }
        if (mTemplate == null || mCollage == null || !checkState()) {
            return false;
        }
        if (mCollage.getItemSize() <= 0) {
            return false;
        }
        int index = mCollage.getItemSize() - 1;

        CollageItem removeItem = getChildItem(index);
        if (removeItem != null) {
            removeItem.clear();
            removeItem.setInPreview(true);
        }

        CollageItem previewItem = getChildItem(mCollage.getItemSize());
        if (previewItem != null) {
            previewItem.setInPreview(false);
        }

        mCollage.getItems().remove(index);
        if (mState != STATE_CAPTURE) {
            setState(STATE_CAPTURE);
        }
        return true;
    }

    @Override
    public void startMerge() {
        if (mCollageListener == null) {
            Log.d(TAG, "no register listener");
            return;
        }
        if (mState != STATE_CAPTURE_COMPLETE && mState != STATE_EDIT) {
            mCollageListener.onError("state error");
            return;
        }
        setState(STATE_MERGE);
        if (mCollage == null || mTemplate == null
                || mCollage.getItemSize() != mTemplate.getChildCount()) {
            mCollageListener.onError("obj has not init");
            setState(STATE_IDLE);
            return;
        }
        updateCollage();
        AsyncTask<Void, Integer, Bitmap> task = new AsyncTask<Void, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void[] params) {
                return mCollage.merge(new Size(2000, 2000), Bitmap.Config.ARGB_8888);
            }

            @Override
            protected void onPostExecute(Bitmap o) {
                setState(STATE_IDLE);
                if (o == null) {
                    mCollageListener.onError("create bitmap fail");
                } else {
                    mCollageListener.onMergeEnd(o);
                }
            }
        };
        task.execute();
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public void setState(int state) {
        if (mState == state) {
            return;
        }
        Log.d(TAG, "state change form " + mState + " to " + state);
        this.mState = state;
        if (mCollageListener != null) {
            mCollageListener.onStateChange(mState);
        }
        switch (mState) {
            case STATE_IDLE:
            case STATE_CAPTURE:
            case STATE_MERGE:
                setBackgroundColor(Color.TRANSPARENT);
                requestDisallowInterceptTouchEvent(false);
                break;
            case STATE_CAPTURE_COMPLETE:
            case STATE_EDIT:
                setBackgroundColor(Color.WHITE);
            case STATE_DRAG:
                requestDisallowInterceptTouchEvent(true);
                break;
        }
    }

    private void init(Size size) {
        if (!checkState()) {
            updateStyle();
        }
        if (mCollage == null) {
            mCollage = new CollageManager(size);
        } else {
            mCollage.setLayoutSize(size);
        }
    }

    private void updateStyle() {
        switch (mStyle) {
            case CollageStyle1.STYLE_ID:
                mTemplate = new CollageStyle1(mSize);
                break;
            case CollageStyle2.STYLE_ID:
                mTemplate = new CollageStyle2(mSize);
                break;
            case CollageStyle3.STYLE_ID:
                mTemplate = new CollageStyle3(mSize);
                break;
            case CollageStyle4.STYLE_ID:
                mTemplate = new CollageStyle4(mSize);
                break;
        }
        removeAllViews();
        for (int i = 0; i < mTemplate.getChildCount(); i++) {
            CollageItemView v = (CollageItemView) LayoutInflater.from(getContext())
                    .inflate(R.layout.collage_item_view, null);
            v.setTag(i);
            v.setInPreview(false);
            addView(v);
        }
        CollageItem firstItem = getChildItem(0);
        if (firstItem != null) {
            firstItem.setInPreview(true);
        }
    }

    private void updateCollage() {
        for (int i = 0; i < getChildCount(); i++) {
            CollageItemView view = (CollageItemView) getChildAt(i);
            Rect area = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            int index = (int) view.getTag();
            mCollage.updateItem(index, area, view.getBitmapRect());
        }
    }

    private boolean checkState() {
        return mTemplate != null && mTemplate.getChildCount() == getChildCount();
    }

    //get (x,y) location on default area index
    private int getCurAreaIndex(int x, int y) {
        if (mTemplate == null) {
            return INVALID_INDEX;
        }
        for (int i = 0; i < mTemplate.getChildCount(); i++) {
            if (mTemplate.getItemArea(i).contains(x, y)) {
                return i;
            }
        }
        return INVALID_INDEX;
    }

    private int getSelectViewIndex() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).isSelected()) {
                return i;
            }
        }
        return INVALID_INDEX;
    }

    //get (x,y) location on view index
    private int getCurViewIndex(int x, int y) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            if (i != mSelectViewIndex && rect.contains(x, y)) {
                return (int) view.getTag();
            }
        }
        return INVALID_INDEX;
    }

    private CollageItem getChildItem(int index) {
        if (mTemplate == null
                || index < 0
                || index >= getChildCount()
                || index >= mTemplate.getChildCount()) {
            return null;
        }
        if (getChildAt(index) instanceof CollageItem) {
            return (CollageItem) getChildAt(index);
        }
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //int spec = Math.min(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        int size = getMeasuredWidth();
        if (mSize == null) {
            mSize = new Size(size, size);
        }
        mSize.setWidth(size);
        mSize.setHeight(size);
        Log.d(TAG, "onMeasure size: " + mSize + "and check state : " + checkState());
        init(mSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "KPI E onLayout()");
        if (!checkState()) {
            return;
        }
        if (mState == STATE_IDLE) {
            //updateStyle();
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                Rect rect = mTemplate.getItemArea(i);
                if (rect == null || rect.isEmpty()) {
                    return;
                }
                child.measure(rect.width(), rect.height());
                child.layout(left + rect.left, top + rect.top, left + rect.right, top + rect.bottom);
                if (isDebug) {
                    Log.d(TAG, "onLayout child view rect : " + rect);
                }
            }
            setState(STATE_CAPTURE);
        } else if (mState == STATE_DRAG) {
            //onDrag

        }
        Log.d(TAG, "KPI X onLayout()");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!checkState4Touch()) {
            return super.onTouchEvent(event);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mSelectViewIndex = getSelectViewIndex();
                Log.d(TAG, "select index : " + mSelectViewIndex);
                if (mSelectViewIndex < 0 || mSelectViewIndex >= getChildCount()) {
                    break;
                }
                setState(STATE_DRAG);

                mPrePoint = new Point((int) getEventLayoutX(event), (int) getEventLayoutY(event));
                mCurPoint = new Point((int) getEventLayoutX(event), (int) getEventLayoutY(event));

                mPreAreaIndex = getCurAreaIndex(mCurPoint.x, mCurPoint.y);
                mCurAreaIndex = mPreAreaIndex;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mSelectViewIndex < 0 || mSelectViewIndex >= getChildCount()) {
                    break;
                }
                if (mState == STATE_DRAG) {
                    mCurPoint.set((int) getEventLayoutX(event), (int) getEventLayoutY(event));
                    mOffsetX = mCurPoint.x - mPrePoint.x;
                    mOffsetY = mCurPoint.y - mPrePoint.y;
                    mPrePoint.set(mCurPoint.x, mCurPoint.y);
                    mCoverViewIndex = getCurViewIndex(mCurPoint.x, mCurPoint.y);
                    int curAreaIndex = getCurAreaIndex(mCurPoint.x, mCurPoint.y);
                    onDrag();
                    if (curAreaIndex == mCurAreaIndex || mCoverViewIndex == INVALID_INDEX) {
                        break;
                    }
                    mCurAreaIndex = curAreaIndex;
                    if (mCurAreaIndex != mPreAreaIndex) {
                        Log.d(TAG, "cur area index : " + mCurAreaIndex + ", pre area index : " + mPreAreaIndex);
                        Log.d(TAG, "select view index : " + mSelectViewIndex + ", cover view index : " + mCoverViewIndex);
                        if (onSwap()) {
                            mPreAreaIndex = mCurAreaIndex;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mSelectViewIndex < 0 || mSelectViewIndex >= getChildCount()) {
                    break;
                }
                if (mState == STATE_DRAG) {
                    onDragEnd();
                    getChildAt(mSelectViewIndex).setElevation(0);
                }
                mSelectViewIndex = INVALID_INDEX;
                mCurPoint = null;
                mPrePoint = null;
                mOffsetX = 0;
                mOffsetY = 0;
                setState(STATE_EDIT);
                break;
        }
        return true;
    }

    private float getEventLayoutX(MotionEvent event) {
        if (mSelectViewIndex == INVALID_INDEX) {
            return 0;
        }
        return getChildAt(mSelectViewIndex).getLeft() + event.getX();
    }

    private float getEventLayoutY(MotionEvent event) {
        if (mSelectViewIndex == INVALID_INDEX) {
            return 0;
        }
        return getChildAt(mSelectViewIndex).getTop() + event.getY();
    }

    private boolean checkState4Touch() {
        return mState != STATE_IDLE
                && mState != STATE_CAPTURE
                && mState != STATE_MERGE;
    }

    private void onDrag() {
        View view = getChildAt(mSelectViewIndex);
        int left = view.getLeft();
        int top = view.getTop();
        int right = view.getRight();
        int bottom = view.getBottom();
        switch (mTemplate.getMovableDirection(mSelectViewIndex)) {
            case VER:
                if (mOffsetY > 0) {
                    bottom += mOffsetY;
                    if (bottom > getBottom()) {
                        bottom = getBottom();
                    }
                    top = bottom - view.getHeight();
                } else {
                    top += mOffsetY;
                    if (top < getTop()) {
                        top = getTop();
                    }
                    bottom = top + view.getHeight();
                }
                break;
            case HOR:
                if (mOffsetX > 0) {
                    right += mOffsetX;
                    if (right > getRight()) {
                        right = getRight();
                    }
                    left = right - view.getWidth();
                } else {
                    left += mOffsetX;
                    if (left < getLeft()) {
                        left = getLeft();
                    }
                    right = left + view.getWidth();
                }
                break;
            case FIXED:
            default:
                return;
        }
        view.layout(left, top, right, bottom);
    }

    private void onDragEnd() {
        View dragView = getChildAt(mSelectViewIndex);
        Log.d(TAG, "onDragEnd view tag " + (int) dragView.getTag());
        Rect curArea = mTemplate.getItemArea(mCurAreaIndex);
        Rect preArea = mTemplate.getItemArea(mPreAreaIndex);
        if (curArea == null || preArea == null) {
            return;
        }
        if (dragView.getWidth() != curArea.width()
                || dragView.getHeight() != curArea.height()) {
            dragView.layout(preArea.left, preArea.top, preArea.right, preArea.bottom);
        } else {
            dragView.layout(curArea.left, curArea.top, curArea.right, curArea.bottom);
        }
    }

    private boolean onSwap() {

        final View coverView = getChildAt(mCoverViewIndex);
        Log.d(TAG, "onSwap view tag " + (int) coverView.getTag());
        final Rect from = new Rect(coverView.getLeft(), coverView.getTop(), coverView.getRight(), coverView.getBottom());
        final Rect to = mTemplate.getItemArea(mPreAreaIndex);

        if (to == null
                || from.width() != to.width()
                || from.height() != to.height()) {
            Log.e(TAG, "onSwap error");
            return false;
        }
        final boolean isVer;
        if (to.left == from.left) {
            isVer = true;
        } else if (to.top == from.top) {
            isVer = false;
        } else {
            Log.e(TAG, "onSwap direction error");
            return false;
        }

        SwapAnimatorTask updateTask = new SwapAnimatorTask() {
            @Override
            public void run() {
                //Log.d(TAG, "offset : " + offset);
                if (isVer) {
                    coverView.layout(from.left,
                            from.top + getOffset(),
                            from.right,
                            from.bottom + getOffset());
                } else {
                    coverView.layout(from.left + getOffset(),
                            from.top,
                            from.right + getOffset(),
                            from.bottom);
                }
            }
        };
        Runnable endTask = new Runnable() {
            @Override
            public void run() {
                coverView.layout(to.left, to.top, to.right, to.bottom);
            }
        };
        if (isVer) {
            startSwapAnimator(updateTask, endTask, 0, to.top - from.top);
            Log.d(TAG, "ver : from : " + from + " to : " + to);
        } else {
            startSwapAnimator(updateTask, endTask, 0, 0, to.left - from.left);
            Log.d(TAG, "hor : from : " + from + " to : " + to);
        }
        return true;
    }

    private void startSwapAnimator(final SwapAnimatorTask update, final Runnable end, int... values) {
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(SWAP_ANIMATOR_DURATION);
        animator.setIntValues(values);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int offset = (int) animation.getAnimatedValue();
                update.setOffset(offset);
                update.run();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                end.run();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                end.run();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private class SwapAnimatorTask implements Runnable {

        private int mOffset;

        public int getOffset() {
            return mOffset;
        }

        public void setOffset(int offset) {
            mOffset = offset;
        }

        @Override
        public void run() {

        }
    }
}
