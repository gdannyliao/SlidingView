package com.example.android.customviews.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.example.android.customviews.R;

/**
 * Created by LiaoXingyu on 3/13/16.
 */
public class RelativeLayout extends ViewGroup {
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
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RelativeLayout);
		int indexCount = typedArray.getIndexCount();
	}

	@Override protected void onLayout(boolean changed, int l, int t, int r, int b) {

	}
}
