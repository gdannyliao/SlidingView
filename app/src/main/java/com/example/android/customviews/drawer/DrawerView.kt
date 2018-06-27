package com.example.android.customviews.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller
import android.widget.Toast
import com.ggdsn.slidingview.R
import java.util.*

@Deprecated("用SlidingView")
class DrawerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private var isOpen: Boolean = false
    private var contentWidth: Float = 0.toFloat()
    private val children = ArrayList<BottomView>()
    private var foregroundView: TopView

    init {
        if (attrs != null) {
            val arr = context.theme.obtainStyledAttributes(attrs,
                    R.styleable.DrawerView, 0, 0)
            try {
                isOpen = arr.getBoolean(R.styleable.DrawerView_open, false)
            } finally {
                arr.recycle()
            }
        }
        setWillNotDraw(false)
        val foregroundView = TopView(context)
        foregroundView.setDrawColor(Color.CYAN)
        foregroundView.setOnClickListener {
            Toast.makeText(context, "foreground view", Toast.LENGTH_SHORT).show()
        }
        addView(foregroundView)
        this.foregroundView = foregroundView
    }

    fun addItem(text: String, l: View.OnClickListener? = null) {
        // FIXME 设置item颜色
        val view = BottomView(context)
        view.setDrawColor(Color.BLACK)
        if (children.size > 0)
            view.setDrawColor(Color.GREEN)
        children.add(view)
        addView(view)
        foregroundView.setMaxOpenLength(children.size * DEFAULT_CHILD_WIDTH)
        view.setOnClickListener { v ->
            Toast.makeText(context, "view:" + v, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isOpen(): Boolean {
        val childLength = children.size * DEFAULT_CHILD_WIDTH
        val sx = foregroundView.scrollX
        return childLength == sx
    }

    private fun isTouchOnChildren(pointx: Float, childIndex: Int): Boolean {
        val left = (contentWidth - DEFAULT_CHILD_WIDTH * children.size + DEFAULT_CHILD_WIDTH * childIndex).toInt()
        val right = left + DEFAULT_CHILD_WIDTH
        return pointx >= left && pointx < right
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (isOpen()) {
            /** FIXME 确认调用子类的dispatchTouchEvent是不是正确的选择
             * 2017.11.19 应该不是，父类应该处理滑动逻辑，如果不是滑动逻辑，再交给子类处理，否则不能轻易地替换子view
             */
            val x = ev.x
            if (x >= 0 && x < contentWidth - DEFAULT_CHILD_WIDTH * children.size) {
                //落在前景上
                return foregroundView.dispatchTouchEvent(ev)
            } else {
                for (i in children.indices) {
                    if (isTouchOnChildren(x, i)) {
                        return children[i].dispatchTouchEvent(ev)
                    }
                }
            }
        } else {
            return foregroundView.dispatchTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.d(TAG, "padding left:" + paddingLeft + "  top:"
                + paddingTop + "  right:" + paddingRight
                + "  bottom:" + paddingBottom)
        Log.v(TAG, "new width:" + w + "  new height:" + h + "  old width:"
                + oldw + "  old height:" + oldh)

        val xpadding = (paddingLeft + paddingRight).toFloat()
        val ypadding = (paddingTop + paddingBottom).toFloat()

        contentWidth = w.toFloat() - xpadding
        val contentHeight = (h - ypadding).toInt()

        foregroundView.layout(paddingLeft, paddingTop, contentWidth.toInt(), contentHeight)
        foregroundView.bringToFront()

        val childrenSize = children.size
        for (i in 0 until childrenSize) {
            val view = children[i]
            val r = (contentWidth - i * DEFAULT_CHILD_WIDTH).toInt()
            val l = r - DEFAULT_CHILD_WIDTH
            view.layout(l, 0, r, contentHeight)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }

    private class BottomView @JvmOverloads constructor(
            context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {

        private var paint: Paint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }
        private var mDrawRect: Rect = Rect(0, 0, DEFAULT_CHILD_WIDTH, DEFAULT_CHILD_WIDTH)

        fun setDrawColor(color: Int) {
            paint.color = color
        }

        override fun layout(l: Int, t: Int, r: Int, b: Int) {
            setBackgroundSize(l, t, r, b)
            super.layout(l, t, r, b)
        }

        private fun setBackgroundSize(l: Int, t: Int, r: Int, b: Int) {
            val w = r - l
            val h = b - t
            mDrawRect.set(0, 0, w, h)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawRect(mDrawRect, paint)
        }
    }

    private class TopView @JvmOverloads constructor(
            context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {

        private var mBackgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
        private var mBackgroundRect: Rect = Rect(0, 0, DEFAULT_CHILD_WIDTH, DEFAULT_CHILD_WIDTH)
        private var mScroller: Scroller = Scroller(context)
        private var lastx: Float = 0.toFloat()
        private var startx: Float = 0.toFloat()
        private val dragableDistance = 144.0f
        private var maxOpenLength: Int = 0
        private var isTouchIn: Boolean = false

        fun setDrawColor(color: Int) {
            mBackgroundPaint.color = color
        }

        fun setMaxOpenLength(maxOpenLength: Int) {
            this.maxOpenLength = maxOpenLength
        }

        override fun computeScroll() {
            smoothScrollToNewPlace()
        }

        private fun smoothScrollToNewPlace() {
            if (mScroller.computeScrollOffset()) {
                scrollTo(mScroller.currX, mScroller.currY)
                invalidate()
            }
        }

        private fun inDraggableDistance(currentScrollDistance: Float): Boolean =
                -dragableDistance <= currentScrollDistance && currentScrollDistance <= 2 * dragableDistance

        private fun isNearOpen(currentScrollDistance: Float): Boolean = currentScrollDistance > DEFAULT_CHILD_WIDTH

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val x: Float
            val deltaX: Float
            val offset: Float
            x = event.x
            deltaX = lastx - x
            offset = scrollX + deltaX
            lastx = x
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isTouchIn = isTouchIn(x)
                    if (isTouchIn) {
                        startx = event.x
                        lastx = startx
                        return true

                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    //FIXME onClickListener被屏蔽
                    Log.i(TAG, "delta x:" + deltaX)
                    if (inDraggableDistance(offset)) {
                        scrollBy(deltaX.toInt(), scrollY)
                    }
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    if (isNearOpen(offset)) {
                        scrollToOpenPosition()
                    } else {
                        scrollToClosePosition()
                    }
                    isTouchIn = false
                    return true
                }
            }
            return super.onTouchEvent(event)
        }

        private fun isTouchIn(touchDownX: Float): Boolean {
            val right = right - scrollX
            return touchDownX <= right
        }

        private fun scrollToOpenPosition() {
            val distance = maxOpenLength - scrollX
            mScroller.startScroll(scrollX, 0, distance, 0)
            postInvalidate()
        }

        private fun scrollToClosePosition() {
            mScroller.startScroll(scrollX, 0, -scrollX, 0)
            postInvalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawRect(mBackgroundRect, mBackgroundPaint)
        }

        override fun layout(l: Int, t: Int, r: Int, b: Int) {
            setBackgroundRectSize(l, t, r, b)

            super.layout(l, t, r, b)
        }

        private fun setBackgroundRectSize(l: Int, t: Int, r: Int, b: Int) {
            val w = r - l
            val h = b - t
            mBackgroundRect.set(0, 0, w, h)
        }
    }

    companion object {

        private val TAG = "DrawerView"
        private val DEFAULT_CHILD_WIDTH = 128
    }
}
