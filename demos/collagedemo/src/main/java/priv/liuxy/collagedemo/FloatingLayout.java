package priv.liuxy.collagedemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by xuyang.liu on 17-7-11.
 */

public class FloatingLayout extends LinearLayout {
    private static final String TAG = "FloatingLayout";
    //state
    private static final int STATE_IDLE = 0;
    private static final int STATE_CLOSE = STATE_IDLE + 1;
    private static final int STATE_OPENING = STATE_CLOSE + 1;
    private static final int STATE_OPEN = STATE_OPENING + 1;
    private static final int STATE_CLOSING = STATE_OPEN + 1;
    //view attr
    private static final int ANIMATION_DURATION = 500;
    private static final int ORIENTATION = 0;
    private static final int MAX_SHOW_COUNT = 4;
    private static final int ITEM_SIZE = 36;
    private static final int DIVIDE_SIZE = 24;
    //
    private static final int MENU_DRAWABLE_DEGREES_OFFSET = 90;
    private int mState = STATE_IDLE;
    private onFloatingListener mFloatingListener;
    private int mOrientation;
    private int mAnimationDuration;
    private int mItemSize;
    private int mDivide;
    private int mMaxShowCount;

    private View mSelectView;

    private ImageView mMenuView;
    private int[] mItemDrawableIds;

    private ValueAnimator mOpenAnimator;
    private ValueAnimator mCloseAnimator;
    private float mLength;
    private boolean mReverseFlag = false;

    public FloatingLayout(Context context) {
        this(context, null);
    }

    public FloatingLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.floating_layout);
        mAnimationDuration = typedArray.getInt(R.styleable.floating_layout_floating_duration, ANIMATION_DURATION);
        mOrientation = typedArray.getInt(R.styleable.floating_layout_floating_orientation, ORIENTATION);
        mItemSize = typedArray.getDimensionPixelSize(R.styleable.floating_layout_item_size, ITEM_SIZE);
        mDivide = typedArray.getDimensionPixelSize(R.styleable.floating_layout_floating_divide, DIVIDE_SIZE);
        mMaxShowCount = typedArray.getInt(R.styleable.floating_layout_floating_max_show_count, MAX_SHOW_COUNT);
        typedArray.recycle();

        switch (mOrientation % 360) {
            case 0:
            case 180:
                setOrientation(HORIZONTAL);
                break;
            case 90:
            case 270:
                setOrientation(VERTICAL);
                break;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMenuView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.floating_menu, null);
        mMenuView.setRotation(mOrientation + MENU_DRAWABLE_DEGREES_OFFSET);
        mMenuView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState == STATE_CLOSE) {
                    if (mOpenAnimator != null) {
                        mOpenAnimator.start();
                    }
                    if (mFloatingListener != null) {
                        mFloatingListener.onFloatOpen();
                    }
                } else if (mState == STATE_OPEN) {
                    if (mCloseAnimator != null) {
                        mCloseAnimator.start();
                    }
                    if (mFloatingListener != null) {
                        mFloatingListener.onFloatClose();
                    }
                }
            }
        });
    }

    private void rotateMenu(int degrees) {
        float rotate = mMenuView.getRotation();
        mMenuView.setRotation(rotate + degrees);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            LinearLayout.LayoutParams params = (LayoutParams) v.getLayoutParams();
            switch (mOrientation % 360) {
                case 0:
                    int left = (int) (0 - (params.width + params.leftMargin + params.rightMargin) + checkLength(i, mLength)) + params.leftMargin;
                    v.layout(left, 0, left + params.width, params.height);
                    logViewLayout(v);
                    break;
                case 90:
                    int top = (int) (0 - (params.height + params.topMargin + params.bottomMargin) + checkLength(i, mLength)) + params.topMargin;
                    v.layout(0, top, params.width, top + params.height);
                    break;
                case 180:
                    int right = (int) (getWidth() + (params.width + params.leftMargin + params.rightMargin) - checkLength(i, mLength)) - params.rightMargin;
                    v.layout(right - params.width, 0, right, params.height);
                    break;
                case 270:
                    int bottom = (int) (getHeight() + (params.height + params.topMargin + params.bottomMargin) - checkLength(i, mLength)) - params.bottomMargin;
                    v.layout(0, bottom - params.height, params.width, bottom);
                    break;
            }
        }
    }

    private void logViewLayout(View v) {
        Log.d(TAG, "view layout : [" + v.getLeft() + "," + v.getTop() + "]-[" + v.getRight() + "," + v.getBottom() + "]");
    }

    private int getMaxLength() {
        int length = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            LinearLayout.LayoutParams params = (LayoutParams) v.getLayoutParams();
            switch (mOrientation) {
                case 0:
                case 180:
                    length += params.width + params.leftMargin + params.rightMargin;
                    break;
                case 90:
                case 270:
                    length += params.height + params.topMargin + params.bottomMargin;
                    break;
            }
        }
        return length;
    }

    private float checkLength(int index, float length) {
        if (index == getChildCount() - 1) {
            return length;
        } else {
            int maxLength = getMaxLength();
            return (length > maxLength ? maxLength : length) - (mItemSize + mDivide);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public void setItemDrawableIds(int[] itemDrawableIds) {
        mItemDrawableIds = itemDrawableIds;
        init();
        initAnimation();
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    public void setFloatingListener(onFloatingListener floatingListener) {
        mFloatingListener = floatingListener;
    }

    private void init() {
        if (mItemDrawableIds == null) {
            return;
        }
        removeAllViews();

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectView != v) {
                    if (mSelectView != null) {
                        mSelectView.setSelected(false);
                    }
                    v.setSelected(true);
                    mSelectView = v;
                }
                try {
                    int index = (int) v.getTag();
                    if (mFloatingListener != null) {
                        mFloatingListener.onItemClick(index);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams linearParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearParams);

        FrameLayout.LayoutParams itemParams = new FrameLayout.LayoutParams(mItemSize, mItemSize);

        int count = mItemDrawableIds.length > mMaxShowCount ? mMaxShowCount : mItemDrawableIds.length;
        switch (mOrientation) {
            case 0:
            case 180:
                HorizontalScrollView hScrollView = new HorizontalScrollView(getContext());
                FrameLayout.LayoutParams hScrollParam = new FrameLayout.LayoutParams(count * (mItemSize + mDivide), mItemSize);
                hScrollView.setLayoutParams(hScrollParam);
                linearLayout.setOrientation(HORIZONTAL);
                hScrollView.setOverScrollMode(OVER_SCROLL_NEVER);
                hScrollView.setHorizontalScrollBarEnabled(false);
                hScrollView.addView(linearLayout);
                addView(hScrollView);
                Log.d(TAG,"mDivide = " + mDivide);
                itemParams.setMargins(mDivide / 2, 0, mDivide / 2, 0);
                break;
            case 90:
            case 270:
                ScrollView vScrollView = new ScrollView(getContext());
                FrameLayout.LayoutParams vScrollParam = new FrameLayout.LayoutParams(mItemSize, count * (mItemSize + mDivide));
                vScrollView.setLayoutParams(vScrollParam);
                linearLayout.setOrientation(VERTICAL);
                vScrollView.setOverScrollMode(OVER_SCROLL_NEVER);
                vScrollView.setVerticalScrollBarEnabled(false);
                vScrollView.addView(linearLayout);
                addView(vScrollView);

                itemParams.setMargins(0, mDivide / 2, 0, mDivide / 2);
                break;
        }

        for (int i = 0; i < mItemDrawableIds.length; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(itemParams);
            imageView.setImageResource(mItemDrawableIds[i]);
            imageView.setTag(i);
            imageView.setOnClickListener(listener);
            if (mOrientation == 0 || mOrientation == 90) {
                linearLayout.addView(imageView, 0);
            } else if (mOrientation == 180 || mOrientation == 270) {
                linearLayout.addView(imageView);
            }
        }

        mMenuView.setLayoutParams(itemParams);
        addView(mMenuView);
        setState(STATE_CLOSE);
    }

    private void initAnimation() {
        int length = getMaxLength();
        Log.d(TAG, "initAnimation length " + length);
        mLength = (mItemSize + mDivide);
        mOpenAnimator = new ValueAnimator();
        mOpenAnimator.setDuration(mAnimationDuration);
        mOpenAnimator.setFloatValues((mItemSize + mDivide), length);
        mOpenAnimator.setInterpolator(new OvershootInterpolator());
        mOpenAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offset = (float) animation.getAnimatedValue() - mLength;
                mLength = (float) animation.getAnimatedValue();
                requestLayout();
                if (offset < 0 && !mReverseFlag) {
                    mReverseFlag = true;
                    rotateMenu(180);
                }
            }
        });
        mOpenAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mReverseFlag = false;
                setState(STATE_OPENING);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mReverseFlag = false;
                setState(STATE_OPEN);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mReverseFlag = false;
                setState(STATE_OPEN);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mCloseAnimator = new ValueAnimator();
        mCloseAnimator.setDuration(mAnimationDuration);
        mCloseAnimator.setFloatValues(length, (mItemSize + mDivide));
        mCloseAnimator.setInterpolator(new OvershootInterpolator());
        mCloseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offset = (float) animation.getAnimatedValue() - mLength;
                mLength = (float) animation.getAnimatedValue();
                requestLayout();
                if (offset > 0 && !mReverseFlag) {
                    mReverseFlag = true;
                    rotateMenu(180);
                }
            }
        });
        mCloseAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mReverseFlag = false;
                setState(STATE_CLOSING);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mReverseFlag = false;
                setState(STATE_CLOSE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mReverseFlag = false;
                setState(STATE_CLOSE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public interface onFloatingListener {
        void onFloatOpen();

        void onFloatClose();

        void onItemClick(int index);
    }
}
