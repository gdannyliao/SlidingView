package com.example.android.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by LiaoXingyu on 3/6/16.
 */
public class FibonacciProgressBar extends View {
	public static final int MAX_LENGTH = 480;
	public static final int MIN_LENGTH = 240;
	private static final long DEFAULT_DURATION = 4000;
	private static final String TAG = FibonacciProgressBar.class.getSimpleName();

	private int maxWidth;
	private int maxHeight;
	private int minWidth;
	private int minHeight;
	private Drawable indeterminateDrawable;
	private boolean isAnimationRunning;
	private long rotateDegree = 0;

	public FibonacciProgressBar(Context context) {
		super(context);
		init(context);
	}

	public FibonacciProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FibonacciProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		maxWidth = MAX_LENGTH;
		maxHeight = MAX_LENGTH;
		minWidth = MIN_LENGTH;
		minHeight = MIN_LENGTH;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			indeterminateDrawable = getResources().getDrawable(R.drawable.progress, context.getTheme());
		} else {
			indeterminateDrawable = getResources().getDrawable(R.drawable.progress);
		}
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final Drawable d = this.indeterminateDrawable;
		int w = 0;
		int h = 0;
		if (d != null) {
			w = Math.max(minWidth, Math.min(maxWidth, d.getIntrinsicWidth()));
			h = Math.max(minHeight, Math.min(maxHeight, d.getIntrinsicHeight()));
		}

		setMeasuredDimension(resolveSizeAndState(w, widthMeasureSpec, 0), resolveSizeAndState(h, heightMeasureSpec, 0));
		//drawable初始大小为0，设置它的大小
		d.setBounds(0, 0, getWidth(), getHeight());
	}

	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Drawable d = this.indeterminateDrawable;
		if (d != null && isAnimationRunning) {
			//每次将画布旋转一部分，默认旋转点是0，0处，需要设置旋转中心为图像中心
			canvas.rotate(DEFAULT_DURATION / 360 * rotateDegree, getWidth() / 2, getHeight() / 2);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
				postInvalidateOnAnimation();
			} else {
				postInvalidate();
			}
			d.draw(canvas);
		}
		rotateDegree++;
	}

	@Override public void setVisibility(int visibility) {
		if (getVisibility() != visibility) {
			super.setVisibility(visibility);

			if (visibility == VISIBLE) {
				startAnimation();
			} else {
				stopAnimation();
			}
		}
	}

	@Override protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (visibility == VISIBLE) {
			startAnimation();
		} else {
			stopAnimation();
		}
	}

	private void stopAnimation() {
		isAnimationRunning = false;
	}

	void startAnimation() {
		if (getVisibility() != VISIBLE) {
			return;
		}
		isAnimationRunning = true;
		postInvalidate();
	}
}
