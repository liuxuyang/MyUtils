package com.liuxy.tct;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

/**
 * Created by xuyang.liu on 17-7-5.
 */

public class CollageItemView extends View
        implements GestureDetector.OnGestureListener, CollageItem {
    private static final String TAG = "ZoomPreviewView";
    //state
    private static final int STATE_IDLE = 1;
    private static final int STATE_READY = STATE_IDLE + 1;
    private static final int STATE_ZOOM = STATE_READY + 1;
    private static final int STATE_ZOOM_DRAG = STATE_ZOOM + 1;
    //attr
    private static final float MIN_RATIO = 1.0f;
    private static final float MAX_RATIO = 1.0f;
    private static final int EMPTY_COLOR = 0xFF1A1A1A;
    private static final int INDEX_TEXT_COLOR = 0xFFFFFFFF;
    private static final int BACKGROUND_COLOR = 0xFF0D0D0D;
    private static final int DIVIDER_WIDTH = 2;
    private static final int TEXT_SIZE = 24;
    private static final int TEXT_OFFSET_X = 36;
    private static final int TEXT_OFFSET_Y = 36;
    //attr field
    private int mDivideWidth = DIVIDER_WIDTH;
    private int mDivideColor = BACKGROUND_COLOR;
    private int mBackground = EMPTY_COLOR;
    private int mTextColor = INDEX_TEXT_COLOR;
    private int mTextSize = TEXT_SIZE;
    private int mTextOffsetX = TEXT_OFFSET_X;
    private int mTextOffsetY = TEXT_OFFSET_Y;
    //field
    private int mState = STATE_IDLE;
    private boolean isInPreview = false;
    private boolean isLayoutReady;
    private Matrix mImageMatrix = new Matrix();
    private Bitmap mBitmap;
    private Paint mPaint;
    //touch event
    private GestureDetector mGestureDetector = null;
    private float mMinRatio = MIN_RATIO;
    private float mMaxRatio = MAX_RATIO;

    private float mRatio = 1.0f;
    private PointF mPrePoint = new PointF();
    private float mStartSpan = 1;
    private Matrix mMatrix = new Matrix();
    private MotionEvent mDownEvent;
    private Rect mCanvasRect = new Rect();

    public CollageItemView(Context context) {
        this(context, null);
    }

    public CollageItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollageItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.zoom_preview_view);
        mDivideWidth = typedArray.getDimensionPixelOffset(R.styleable.zoom_preview_view_divider_width, DIVIDER_WIDTH);
        mDivideColor = typedArray.getColor(R.styleable.zoom_preview_view_divider_color, BACKGROUND_COLOR);
        mBackground = typedArray.getColor(R.styleable.zoom_preview_view_empty_background, EMPTY_COLOR);
        mTextColor = typedArray.getColor(R.styleable.zoom_preview_view_index_text_color, INDEX_TEXT_COLOR);
        mTextSize = typedArray.getDimensionPixelOffset(R.styleable.zoom_preview_view_index_text_size, TEXT_SIZE);
        mTextOffsetX = typedArray.getDimensionPixelOffset(R.styleable.zoom_preview_view_index_text_offset_x, TEXT_OFFSET_X);
        mTextOffsetY = typedArray.getDimensionPixelOffset(R.styleable.zoom_preview_view_index_text_offset_y, TEXT_OFFSET_Y);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setTypeface(Typeface.create("Roboto-Medium", Typeface.NORMAL));
        mPaint.setTextSize(mTextSize);
        Log.d(TAG, "mDivideWidth : " + mDivideWidth);

        mGestureDetector = new GestureDetector(context, this);
    }

    @Override
    public boolean isInPreview() {
        return isInPreview;
    }

    @Override
    public void setInPreview(boolean inPreview) {
        if (isInPreview != inPreview) {
            isInPreview = inPreview;
            invalidate();
            Log.d(TAG, "index " + getTag() + " setInPreview " + inPreview);
        }
    }

    public float getRatio() {
        return mRatio;
    }

    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        initLayout();
        invalidate();
    }

    @Override
    public void clear() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
            invalidate();
        }
        mMatrix.reset();
        mImageMatrix.reset();
        isLayoutReady = false;
        setState(STATE_IDLE);
    }

    @Override
    public Matrix getImageMatrix() {
        return mImageMatrix;
    }

    @Override
    public void setImageMatrix(Matrix imageMatrix) {
        if (imageMatrix != null && !imageMatrix.equals(mImageMatrix)) {
            Log.d(TAG, "setImageMatrix : " + imageMatrix);
            mImageMatrix.set(imageMatrix);
            invalidate();
        }
    }

    @Override
    public Rect getBitmapRect() {
        float[] values = new float[9];
        mImageMatrix.getValues(values);
        Rect rect = new Rect();
        rect.left = (int) (-(values[Matrix.MTRANS_X] / values[Matrix.MSCALE_X]));
        rect.top = (int) (-(values[Matrix.MTRANS_Y] / values[Matrix.MSCALE_Y]));
        rect.right = (int) (rect.left + getWidth() / values[Matrix.MSCALE_X]);
        rect.bottom = (int) (rect.top + getHeight() / values[Matrix.MSCALE_Y]);
        return rect;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (STATE_READY != mState
                && STATE_ZOOM_DRAG != mState
                && STATE_ZOOM != mState) {
            return true;
        }
        if (isSelected() && event.getPointerCount() == 1
                && (event.getActionMasked() == MotionEvent.ACTION_DOWN
                || event.getActionMasked() == MotionEvent.ACTION_MOVE
                || event.getActionMasked() == MotionEvent.ACTION_UP
                || event.getActionMasked() == MotionEvent.ACTION_CANCEL)) {
            if (mDownEvent != null) {
                ((ViewGroup) getParent()).onTouchEvent(mDownEvent);
                mDownEvent = null;
            }
            ((ViewGroup) getParent()).onTouchEvent(event);
        }
        if (isSelected()
                && (event.getActionMasked() == MotionEvent.ACTION_CANCEL
                || event.getActionMasked() == MotionEvent.ACTION_UP)) {
            setSelected(false);
            setElevation(0);
            Log.e(TAG, "end drag");
        }
        if (isSelected()) {
            return true;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                setState(STATE_ZOOM_DRAG);
                mPrePoint.set(event.getX(), event.getY());
                mDownEvent = MotionEvent.obtain(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN: //scale begin
                setState(STATE_ZOOM);
                mStartSpan = getSpan(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mMatrix.set(getImageMatrix());
                if (STATE_ZOOM_DRAG == getState()) { //zoom drag
                    float deltaX = event.getX() - mPrePoint.x;
                    float deltaY = event.getY() - mPrePoint.y;
                    mPrePoint.set(event.getX(), event.getY());
                    if (deltaX == 0 && deltaY == 0) {
                        break;
                    }
                    Log.d(TAG, "drag delta: " + deltaX + "x" + deltaY);
                    checkDrag(deltaX, deltaY);
                } else if (STATE_ZOOM == getState()) { //zoom
                    mRatio = (Math.abs(getSpan(event) / mStartSpan) - 1) / 20 + 1;
                    PointF pivotPoint = getPivotPoint(event);
                    checkZoom(mRatio, pivotPoint);
                }
                setImageMatrix(mMatrix);
                break;
            case MotionEvent.ACTION_POINTER_UP: //scale end
                Log.d(TAG, "scale end");
                setState(STATE_ZOOM_DRAG);
                mStartSpan = 1;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mMatrix.reset();
                setState(STATE_READY);
                Log.d(TAG, "zoom/drag end");
                break;
        }
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(event);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw divide
        canvas.getClipBounds(mCanvasRect);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2 * mDivideWidth);
        mPaint.setColor(mDivideColor);
        canvas.drawRect(mCanvasRect, mPaint);
        //draw content
        canvas.clipRect(mDivideWidth, mDivideWidth,
                getWidth() - mDivideWidth,
                getHeight() - mDivideWidth);
        if (mBitmap != null && !mBitmap.isRecycled()) { //draw bitmap
            initLayout();
            if (isLayoutReady) {
                Log.d(TAG, "mImageMatrix :" + mImageMatrix);
                canvas.drawBitmap(mBitmap, mImageMatrix, mPaint);
            }
        } else if (isInPreview) { //draw nothing
            canvas.drawColor(Color.TRANSPARENT);
        } else {
            canvas.drawColor(mBackground);
            String index = String.valueOf(getTag());
            if (index != null) {
                Log.d(TAG, "draw index " + index + ", offset : " + mTextOffsetX + "x" + mTextOffsetY);
                mPaint.setColor(mTextColor);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawText(index, mTextOffsetX, mTextOffsetY + mTextSize, mPaint);
            }
        }
    }

    private void initLayout() {
        if (!isLayoutReady) {
            int bw = mBitmap.getWidth();
            int bh = mBitmap.getHeight();
            int w = getWidth();
            int h = getHeight();
            if (bw == 0 || bh == 0 || w == 0 || h == 0) {
                return;
            }
            float ratioX = w * 1.0f / bw;
            float ratioY = h * 1.0f / bh;
            float ratio = Math.min(ratioX, ratioY);

            mMinRatio = ratio;

            if (mMaxRatio < mMinRatio) {
                return;
            }
            mImageMatrix.setScale(ratio, ratio);
            isLayoutReady = true;
            setState(STATE_READY);
        }
    }

    private Rect getTransRange(float ratioX, float ratioY) {
        if (mBitmap == null) {
            return null;
        }
        int bw = mBitmap.getWidth();
        int bh = mBitmap.getHeight();
        int w = getWidth();
        int h = getHeight();
        if (bw == 0 || bh == 0 || w == 0 || h == 0) {
            Log.d(TAG, "getTransRange error");
            return null;
        }
        return new Rect((int) (w - bw * ratioX), (int) (h - bh * ratioY), 0, 0);
    }


    private void checkZoom(float ratio, PointF pivotPoint) {
        Log.d(TAG, "checkZoom mRatio : " + ratio);
        float[] values = new float[9];
        mMatrix.getValues(values);
        Log.d(TAG, "mMatrix : " + mMatrix);
        if (ratio > 1 && values[Matrix.MSCALE_X] == mMaxRatio) {
            Log.e(TAG, "checkZoom return max");
            mMatrix.setValues(values);
            return;
        }
        if (Math.abs(ratio) < 1 && values[Matrix.MSCALE_X] == mMinRatio) {
            Log.e(TAG, "checkZoom return min");
            mMatrix.setValues(values);
            return;
        }
        mMatrix.postScale(ratio, ratio, pivotPoint.x, pivotPoint.y);
        mMatrix.getValues(values);

        if (values[Matrix.MSCALE_X] > mMaxRatio || values[Matrix.MSCALE_Y] > mMaxRatio) {
            values[Matrix.MSCALE_X] = mMaxRatio;
            values[Matrix.MSCALE_Y] = mMaxRatio;
        }
        if (values[Matrix.MSCALE_X] < mMinRatio || values[Matrix.MSCALE_Y] < mMinRatio) {
            values[Matrix.MSCALE_X] = mMinRatio;
            values[Matrix.MSCALE_Y] = mMinRatio;
        }
        Rect range = getTransRange(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y]);
        if (range != null) {
            values[Matrix.MTRANS_X] = Math.max(values[Matrix.MTRANS_X], range.left);
            values[Matrix.MTRANS_Y] = Math.max(values[Matrix.MTRANS_Y], range.top);
            values[Matrix.MTRANS_X] = Math.min(values[Matrix.MTRANS_X], range.right);
            values[Matrix.MTRANS_Y] = Math.min(values[Matrix.MTRANS_Y], range.bottom);
        }
        Log.d(TAG, "values : " + Arrays.toString(values));
        mMatrix.setValues(values);
    }

    private void checkDrag(float deltaX, float deltaY) {
        float[] values = new float[9];
        mMatrix.postTranslate(deltaX, deltaY);
        mMatrix.getValues(values);

        Rect range = getTransRange(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y]);
        if (range != null) {
            Log.d(TAG, "range : " + range);
            values[Matrix.MTRANS_X] = Math.max(values[Matrix.MTRANS_X], range.left);
            values[Matrix.MTRANS_Y] = Math.max(values[Matrix.MTRANS_Y], range.top);
            values[Matrix.MTRANS_X] = Math.min(values[Matrix.MTRANS_X], range.right);
            values[Matrix.MTRANS_Y] = Math.min(values[Matrix.MTRANS_Y], range.bottom);
        }
        mMatrix.setValues(values);
    }

    private void checkZoomDrag(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);

    }

    private void checkMatrix(Matrix matrix) {
        //TODO: some bugs !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        float[] values = new float[9];
        matrix.getValues(values);
        //scale range (mMinRatio ~ mMaxRatio)
        values[Matrix.MSCALE_X] = Math.min(values[Matrix.MSCALE_X], mMaxRatio);
        values[Matrix.MSCALE_X] = Math.max(values[Matrix.MSCALE_X], mMinRatio);
        values[Matrix.MSCALE_Y] = Math.min(values[Matrix.MSCALE_Y], mMaxRatio);
        values[Matrix.MSCALE_Y] = Math.max(values[Matrix.MSCALE_Y], mMinRatio);
        //trans range mBitmapRange
        Rect range = getTransRange(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y]);
        if (range != null) {
            values[Matrix.MTRANS_X] = Math.max(values[Matrix.MTRANS_X], range.left * values[Matrix.MSCALE_X]);
            values[Matrix.MTRANS_Y] = Math.max(values[Matrix.MTRANS_Y], range.top * values[Matrix.MSCALE_Y]);
            values[Matrix.MTRANS_X] = Math.min(values[Matrix.MTRANS_X], range.right * values[Matrix.MSCALE_X]);
            values[Matrix.MTRANS_Y] = Math.min(values[Matrix.MTRANS_Y], range.bottom * values[Matrix.MSCALE_Y]);
        }
        matrix.setValues(values);
    }

    private float getSpan(MotionEvent event) {
        if (event.getPointerCount() < 2) {
            return 1;
        }
        double dx = event.getX(1) - event.getX(0);
        double dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private PointF getPivotPoint(MotionEvent event) {
        float[] values = new float[9];
        mImageMatrix.setValues(values);
        float midX = (event.getX(1) + event.getX(0)) / 2 - getLeft() - values[Matrix.MTRANS_X];
        float midY = (event.getY(1) + event.getY(0)) / 2 - getTop() - values[Matrix.MTRANS_Y];
        return new PointF(midX, midY);
    }

    private int getState() {
        return mState;
    }

    private void setState(int state) {
        mState = state;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.e(TAG, "start drag");
        if (e.getPointerCount() == 1) {
            setSelected(true);
            setElevation(20);
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
