package com.ggdsn.slidingview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.OverScroller

/**
 * Created by LiaoXingyu on 6/26/2018.
 */
interface SlidingView {
    /**
     * SlidingView可以打开的距离。-1表示可以完整打开
     */
    var openWidth: Int
}

class SlidingLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), SlidingView {

    override var openWidth: Int = 360

    private val scroller = OverScroller(context)
    private var isOpen = false
    private var lastX = 0f
    private var activePointerId = INVALID_POINTER_ID
    private var isDragging = false
    private val touchSlop: Int
    private val openSlop = 100

    init {
        val configuration = ViewConfiguration.get(context)
        touchSlop = configuration.scaledTouchSlop
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        if (action == MotionEvent.ACTION_MOVE && isDragging) return true

        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = ev.getPointerId(0)
                lastX = ev.getX(activePointerId)
            }
            MotionEvent.ACTION_MOVE -> move@ {
                val pointerId = activePointerId
                val pointerIndex = ev.findPointerIndex(pointerId)
                if (pointerId == INVALID_POINTER_ID || pointerIndex == -1)
                    return@move

                val x = ev.getX(pointerIndex)
                if (Math.abs(x - lastX) > touchSlop) {
                    isDragging = true

                    //告诉底层的view不要阻止这个消息
                    parent?.requestDisallowInterceptTouchEvent(true)
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (childCount == 0) return false
        val pointerIndex = event.findPointerIndex(activePointerId)
        if (pointerIndex == -1) return true

        val x = event.getX(pointerIndex)
        val deltaX = x - lastX
        lastX = x

        val topView = getChildAt(childCount - 1)
        val offset = topView.translationX + deltaX
        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                //正在滑动时如果有新事件，就停止滑动动画，响应新事件
                if (!scroller.isFinished)
                    scroller.abortAnimation()
                if (inDraggableDistance(offset)) {
                    topView.translationX += deltaX
                }
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                if (isNearOpen(offset)) scrollToOpenPosition() else scrollToClosePosition()
                lastX = 0f
                activePointerId = INVALID_POINTER_ID
                isDragging = false
            }
        }
        //只有down的情况的返回值有决定性，其他情况随意返回即可
        return true
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            val childCount = childCount
            if (childCount == 0) return
            val topView = getChildAt(childCount - 1)
            topView.translationX = scroller.currX.toFloat()
            postInvalidate()
        }
    }

    private fun scrollToOpenPosition() {
        val childCount = childCount
        if (childCount == 0) return
        val topView = getChildAt(childCount - 1)
        scroller.startScroll(topView.translationX.toInt(), 0, -(topView.translationX + openWidth).toInt(), 0)
        postInvalidate()
    }

    private fun scrollToClosePosition() {
        val childCount = childCount
        if (childCount == 0) return
        val topView = getChildAt(childCount - 1)
        val translationX = topView.translationX.toInt()
        scroller.startScroll(translationX, 0, -translationX, 0)
        postInvalidate()
    }

    private fun isNearOpen(offset: Float): Boolean {
        return openWidth + offset <= openSlop
    }

    private fun inDraggableDistance(offset: Float): Boolean {
        return -1.2 * openWidth <= offset && offset < openWidth / 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
        val childCount = childCount
        if (childCount == 0) return

        val topView = getChildAt(childCount - 1)
        //封面的view需要遮盖，所以希望是与容器等宽高的
        topView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
        val childWidth = openWidth / (childCount - 1)
        val heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        val widthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY)
        for (i in 0..childCount - 2) {
            getChildAt(i).measure(widthSpec, heightSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (!changed) return
        val childCount = childCount
        if (childCount == 0) return
        //最后一个上层view作为封面显示
        val topView = getChildAt(childCount - 1)
        val height = b - t
        val width = r - l
        topView.layout(0, 0, width, height)

        var usedWidth = width - openWidth
        for (i in 0..childCount - 2) {
            val view = getChildAt(i)
            view.layout(usedWidth, 0, usedWidth + view.measuredWidth, height)
            usedWidth += view.measuredWidth
        }
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }
}