package com.example.android.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * Created by LiaoXingyu on 3/6/16.
 */
public class FibonacciProgressBar extends View {
	public static final int MAX_LENGTH = 48;
	public static final int MIN_LENGTH = 24;
	private static final long DEFAULT_DURATION = 4000;
	private int maxWidth;
	private int maxHeight;
	private int minWidth;
	private int minHeight;
	private Drawable indeterminateDrawable;
	private LinearInterpolator interpolator;
	private Transformation transformation;
	private AlphaAnimation animation;
	private boolean animationRunning;

	public FibonacciProgressBar(Context context) {
		super(context);
	}

	public FibonacciProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FibonacciProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			indeterminateDrawable = getResources().getDrawable(R.drawable.progress, context.getTheme());
		} else {
			indeterminateDrawable = getResources().getDrawable(R.drawable.progress);
		}
	}

	private void init() {
		maxWidth = MAX_LENGTH;
		maxHeight = MAX_LENGTH;
		minWidth = MIN_LENGTH;
		minHeight = MIN_LENGTH;
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
	}

	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Drawable d = this.indeterminateDrawable;
		if (d != null && animationRunning) {
			animation.getTransformation(getDrawingTime(), transformation);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
				postInvalidateOnAnimation();
			} else {
				postInvalidate();
			}
			d.draw(canvas);
		}
		//canvas.restore();
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
		animationRunning = false;
	}

	void startAnimation() {
		if (getVisibility() != VISIBLE) {
			return;
		}

		if (interpolator == null) {
			interpolator = new LinearInterpolator();
		}

		if (transformation == null) {
			transformation = new Transformation();
		} else {
			transformation.clear();
		}

		if (animation == null) {
			animation = new AlphaAnimation(0.0f, 1.0f);
		} else {
			animation.reset();
		}

		animation.setRepeatMode(Animation.RESTART);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setDuration(DEFAULT_DURATION);
		animation.setInterpolator(interpolator);
		animation.setStartTime(Animation.START_ON_FIRST_FRAME);
		animationRunning = true;
		postInvalidate();
	}
}
