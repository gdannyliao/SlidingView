package com.example.android.customviews.charting;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.Toast;

import com.example.android.customviews.R;

import java.util.ArrayList;
import java.util.List;

public class DrawerView extends ViewGroup {

    private static final String TAG = "DrawerView";
    private static final int DEFAULT_CHILD_WIDTH = 128;
    private boolean isOpen;
    private Context mContext;
    private float contentWidth;

    private List<BottomView> mChildren = new ArrayList<BottomView>();
    private TopView mForegroundView;

    public DrawerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttribute(context, attrs);
    }

    public DrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs);
    }

    public DrawerView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        mContext = context;
        mForegroundView = new TopView(context);
        mForegroundView.setDrawColor(Color.CYAN);
        addView(mForegroundView);

        mForegroundView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "foreground view", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray arr = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.DrawerView, 0, 0);

        try {
            isOpen = arr.getBoolean(R.styleable.DrawerView_open, false);

        } finally {
            arr.recycle();
        }
        init(context);
    }

    public void addItem(String text, OnClickListener l) {
        // FIXME 设置item颜色

        BottomView view = new BottomView(mContext);
        view.setDrawColor(Color.BLACK);
        if (mChildren.size() > 0)
            view.setDrawColor(Color.GREEN);
        mChildren.add(view);
        addView(view);
        mForegroundView.setMaxOpenLength(mChildren.size() * DEFAULT_CHILD_WIDTH);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.v(TAG, "on click view:" + v);
                Toast.makeText(mContext, "view:" + v, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isOpen() {
        int childLength = mChildren.size() * DEFAULT_CHILD_WIDTH;
        int sx = mForegroundView.getScrollX();
        return childLength == sx;
    }

    private boolean isTouchOnChildren(float pointx, int childIndex) {
        int left = (int) (contentWidth - DEFAULT_CHILD_WIDTH * mChildren.size() + DEFAULT_CHILD_WIDTH * childIndex);
        int right = left + DEFAULT_CHILD_WIDTH;
        return pointx >= left && pointx < right;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (isOpen()) {
            //FIXME 确认调用子类的dispatchTouchEvent是不是正确的选择
            float x = ev.getX();
            if (x >= 0 && x < contentWidth - DEFAULT_CHILD_WIDTH * mChildren.size()) {
                //落在前景上
                return mForegroundView.dispatchTouchEvent(ev);
            } else {
                for (int i = 0; i < mChildren.size(); i++) {
                    if (isTouchOnChildren(x, i)) {
                        return mChildren.get(i).dispatchTouchEvent(ev);
                    }
                }
            }
        } else {
            return mForegroundView.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "padding left:" + getPaddingLeft() + "  top:"
                + getPaddingTop() + "  right:" + getPaddingRight()
                + "  bottom:" + getPaddingBottom());
        Log.v(TAG, "new width:" + w + "  new height:" + h + "  old width:"
                + oldw + "  old height:" + oldh);

        float xpadding = (float) (getPaddingLeft() + getPaddingRight());
        float ypadding = (float) (getPaddingTop() + getPaddingBottom());

        contentWidth = (float) w - xpadding;
        int contentHeight = (int) (h - ypadding);

        mForegroundView.layout(getPaddingLeft(), getPaddingTop(), (int) contentWidth, contentHeight);
        mForegroundView.bringToFront();

        int childrenSize = mChildren.size();
        for (int i = 0; i < childrenSize; i++) {
            BottomView view = mChildren.get(i);
            int r = (int) (contentWidth - i * DEFAULT_CHILD_WIDTH);
            int l = r - DEFAULT_CHILD_WIDTH;
            view.layout(l, 0, r, contentHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    private static class BottomView extends View {

        private Paint mPaint;
        private Rect mDrawRect;

        public BottomView(Context context) {
            super(context);
            init(context);
        }

        public BottomView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public BottomView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        private void init(Context context) {
            setupPaint();

            mDrawRect = new Rect(0, 0, DEFAULT_CHILD_WIDTH, DEFAULT_CHILD_WIDTH);
        }

        private void setupPaint() {
            mPaint = new Paint();
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.FILL);
        }

        public void setDrawColor(int color) {
            mPaint.setColor(color);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return super.onTouchEvent(event);
        }

        @Override
        public void layout(int l, int t, int r, int b) {
            setBackgroundSize(l,t,r,b);
            super.layout(l, t, r, b);
        }

        private void setBackgroundSize(int l, int t, int r, int b) {
            int w = r - l;
            int h = b - t;
            mDrawRect.set(0, 0, w, h);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawRect(mDrawRect, mPaint);
        }
    }

    private static class TopView extends View {

        private Paint mBackgroundPaint;
        private Rect mBackgroundRect;
        private Scroller mScroller;
        private float lastx;
        private float startx;
        private float dragableDistance = 144.0f;
        private int maxOpenLength;
        private boolean isTouchIn;

        public TopView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init(context);
        }

        public TopView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public TopView(Context context) {
            super(context);
            init(context);
        }

        public void setDrawColor(int color) {
            mBackgroundPaint.setColor(color);
        }

        public void setMaxOpenLength(int maxOpenLength) {
            this.maxOpenLength = maxOpenLength;
        }

        private void init(Context context) {
            setupPaint();
            mBackgroundRect = new Rect(0, 0, DEFAULT_CHILD_WIDTH, DEFAULT_CHILD_WIDTH);
            mScroller = new Scroller(context);
        }

        private void setupPaint() {
            mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBackgroundPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void computeScroll() {
            smoothScrollToNewPlace();
        }

        private void smoothScrollToNewPlace() {
            if (mScroller.computeScrollOffset()) {
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
                invalidate();
            }
        }

        private boolean isInDragableDistance(float currentScrollDistance) {
            return -dragableDistance <= currentScrollDistance && currentScrollDistance <= 2 * dragableDistance;
        }

        private boolean isCloseToOpen(float currentScrollDistance) {
            return currentScrollDistance > DEFAULT_CHILD_WIDTH;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float currentx;
            float deltax;
            float currentScrollDistance;
            currentx = event.getX();
            deltax = lastx - currentx;
            currentScrollDistance = getScrollX() + deltax;
            lastx = currentx;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTouchIn = isTouchIn(currentx);
                    if (isTouchIn) {
                        startx = event.getX();
                        lastx = startx;
                        return true;

                    }
                    break;
                case MotionEvent.ACTION_MOVE:
//FIXME onClickListener被屏蔽
                    Log.i(TAG, "delta x:" + deltax);
                    if (isInDragableDistance(currentScrollDistance)) {
                        scrollBy((int) deltax, getScrollY());
                    }
                    return true;

                case MotionEvent.ACTION_UP:

                    if (isCloseToOpen(currentScrollDistance)) {
                        scrollToOpenPosition();
                    } else {
                        scrollToClosePosition();
                    }
                    isTouchIn = false;
                    return true;
            }

            return super.onTouchEvent(event);
        }

        private boolean isTouchIn(float touchDownX) {
            int right = getRight() - getScrollX();
            return touchDownX <= right;
        }

        private void scrollToOpenPosition() {
            int distance = maxOpenLength - getScrollX();
            mScroller.startScroll(getScrollX(), 0, distance, 0);
            postInvalidate();
        }

        private void scrollToClosePosition() {
            mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
            postInvalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawRect(mBackgroundRect, mBackgroundPaint);
        }

        @Override
        public void layout(int l, int t, int r, int b) {
            setBackgroundRectSize(l,t,r,b);

            super.layout(l, t, r, b);
        }

        private void setBackgroundRectSize(int l, int t, int r, int b) {
            int w = r - l;
            int h = b - t;
            mBackgroundRect.set(0, 0, w, h);
        }
    }
}
