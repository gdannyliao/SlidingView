package com.example.android.customviews.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.example.android.customviews.R;
import java.util.LinkedList;

/**
 * Created by LiaoXingyu on 3/13/16.
 */
public class RelativeLayout extends ViewGroup {
	private static final int RULES_COUNT = 4;

	public static final int ALIGN_PARENT_LEFT = 1;
	public static final int ALIGN_PARENT_TOP = 2;
	public static final int ALIGN_LEFT = 3;
	public static final int ALIGN_TOP = 4;

	LinkedList<View> sortHorizontalChildren;
	public RelativeLayout(Context context) {
		super(context);
		init(context, null, 0);
	}

	public RelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public RelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
	}

	private void sortChildren() {
		int childCount = getChildCount();
		if (sortHorizontalChildren == null || sortHorizontalChildren.size() != childCount) {
			sortHorizontalChildren = new LinkedList<>();
		}

		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
		}
	}
	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int layoutWidth = 0, layoutHeight = 0;
		int expectWidth = MeasureSpec.getSize(widthMeasureSpec);
		int expectHeight = MeasureSpec.getSize(heightMeasureSpec);
		int wMode = MeasureSpec.getMode(widthMeasureSpec);
		int hMode = MeasureSpec.getMode(heightMeasureSpec);

		if (wMode != MeasureSpec.UNSPECIFIED) {
			layoutWidth = expectWidth;
		}
		if (hMode != MeasureSpec.UNSPECIFIED) {
			layoutHeight = expectHeight;
		}
	}

	@Override protected void onLayout(boolean changed, int l, int t, int r, int b) {

	}

	public static class LayoutParams extends ViewGroup.MarginLayoutParams {
		private static final int TRUE = -1;
		int[] rules = new int[RULES_COUNT];

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
			int[] rules = this.rules;
			TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.RelativeLayout);
			for (int i = 0, count = typedArray.getIndexCount(); i < count; i++) {
				int attr = typedArray.getIndex(i);
				switch (attr) {
					case R.styleable.RelativeLayout_alignParentLeft:
						rules[ALIGN_PARENT_LEFT] = typedArray.getBoolean(attr, false) ? TRUE : 0;
						break;
					case R.styleable.RelativeLayout_alignParentTop:
						rules[ALIGN_PARENT_TOP] = typedArray.getBoolean(attr, false) ? TRUE : 0;
						break;
					case R.styleable.RelativeLayout_alignLeft:
						rules[ALIGN_LEFT] = typedArray.getResourceId(attr, 0);
						break;
					case R.styleable.RelativeLayout_alignTop:
						rules[ALIGN_TOP] = typedArray.getResourceId(attr, 0);
						break;
				}
			}

			typedArray.recycle();
		}

		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(MarginLayoutParams source) {
			super(source);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}
	}
}
