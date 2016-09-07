package com.mk.slidedelete;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by cabbageMk on 2016/9/6.
 */
public class SlideFrameLayout extends FrameLayout {

    private ViewDragHelper mViewDragHelper;
    private View ll_operate;
    private View ll_name;
    private int mWidth;
    private int mHeight;
    private int mRange;
    /**
     * 状态监听器
     */
    private OnStateChangedListener onStateChangedListener;

    private enum States {
        OPEN, CLOSE, SLIDING
    }

    ;
    private States state = States.CLOSE;

    public OnStateChangedListener getOnStateChangedListener() {
        return onStateChangedListener;
    }

    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.onStateChangedListener = onStateChangedListener;
    }

    public interface OnStateChangedListener {
        void onOpen(SlideFrameLayout sfl);

        void onClose(SlideFrameLayout sfl);

        void onStartOpen(SlideFrameLayout sfl);

        void onStartClose(SlideFrameLayout sfl);
    }

    public SlideFrameLayout(Context context) {
        this(context, null);
    }

    public SlideFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 1.创建拖拽帮助类
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, cb);
    }

    /**
     * 触摸事件结果处理
     */
    ViewDragHelper.Callback cb = new ViewDragHelper.Callback() {
        // 子view是否可以拖拽
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        // 子view水平方向可拖拽的范围
        @Override
        public int getViewHorizontalDragRange(View child) {
            return Integer.MAX_VALUE;
        }

        // 修正水平方向拖拽的距离
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == ll_name) {
                if (left > 0) {
                    return 0;
                } else if (left < -mRange) {
                    return -mRange;
                }
            } else if (child == ll_operate) {
                if (left < (mWidth - mRange)) {
                    return mWidth - mRange;
                } else if (left > mWidth) {
                    return mWidth;
                }
            }
            return left;
        }

        // 子view位置发生改变时
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == ll_name) {
                // 当移动ll_name时,将偏移量赋给ll_operate
                ll_operate.offsetLeftAndRight(dx);
            } else if (changedView == ll_operate) {
                // 当移动ll_operate时,将偏移量赋给ll_name,实现移动同步
                ll_name.offsetLeftAndRight(dx);
            }
            getSate();

            invalidate();
        }

        // 当松手时
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (xvel == 0 && ll_name.getLeft() < (-mRange * 0.5f)) {
                //layoutView(true);
                // 平滑打开
                slowOpen();
            } else if (xvel < 0) {
                //layoutView(true);
                // 平滑打开
                slowOpen();
                Log.e("zheng", "onViewReleased: " + xvel);
            } else {
                //layoutView(false);
                // 平滑关闭
                slowClose();
            }
        }
    };

    /**
     * 获取当前状态
     */
    private void getSate() {
        // 上一次状态
        States lastState = state;
        int left = ll_name.getLeft();
        // 当前状态
        state = updateState(left);
        // 当前状态与上一次不同,且监听器不为空
        Log.e("zheng", "lastState: " + lastState + "left: " + left + "currentState: " + state);
        if (lastState != state && onStateChangedListener != null) {
            if (state == States.OPEN) {
                onStateChangedListener.onOpen(this);
            } else if (state == States.CLOSE) {
                onStateChangedListener.onClose(this);
            } else if (state == States.SLIDING) {
                if (lastState == States.OPEN) {
                    onStateChangedListener.onStartClose(this);
                } else if (lastState == States.CLOSE) {
                    onStateChangedListener.onStartOpen(this);
                }
            }
        }
    }

    private States updateState(int left) {
        if (left == -mRange) {
            return States.OPEN;
        } else if (left == 0) {
            return States.CLOSE;
        } else {
            return States.SLIDING;
        }
    }

    /**
     * 平滑关闭
     */
    public void slowClose() {
        if (mViewDragHelper.smoothSlideViewTo(ll_name, 0, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 平滑打开
     */
    private void slowOpen() {
        if (mViewDragHelper.smoothSlideViewTo(ll_name, -mRange, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 是否拦截事件,交由helper处理
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * 触摸事件的处理,交由helper处理
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float startX = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if ((event.getX() - startX) > 20) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                startX = 0;
                break;

        }
        // 捕捉多点触控操作异常
        try {
            mViewDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 获取子view
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ll_operate = getChildAt(0);
        ll_name = getChildAt(1);
    }

    /**
     * 获取宽高
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mRange = ll_operate.getMeasuredWidth();
    }

    /**
     * 自定义布局
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 默认关闭状态
        layoutView(false);
    }

    /**
     * 内容布局
     *
     * @param b
     */
    private void layoutView(boolean b) {
        // ll_name布局
        Rect rect = getRectName(b);
        ll_name.layout(rect.left, rect.top, rect.right, rect.bottom);
        // 根据ll_name的布局,布局ll_operate,ll_operate的左边靠着ll_name的右边,故
        int left = rect.right;
        int top = 0;
        int right = rect.right + mRange;
        int bottom = rect.bottom;
        ll_operate.layout(left, top, right, bottom);

    }

    private Rect getRectName(boolean b) {
        int left;
        if (b) {
            // 打开状态
            left = -mRange;
        } else {
            // 关闭状态
            left = 0;
        }
        Rect rect = new Rect(left, 0, mWidth + left, mHeight);
        return rect;
    }


}
