package com.example.android.customviews.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.example.android.customviews.R;
import java.util.ArrayList;
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

		//for (int i = 0; i < childCount; i++) {
		//	View child = getChildAt(i);
		//	//对于每一个view，找到它依赖的对象，并把这个view添加到依赖对象的关系表中
		//	LayoutParams clp = (LayoutParams) child.getLayoutParams();
		//	int dependId = clp.getRule(ALIGN_LEFT);
		//	View dependView = findViewById(dependId);
		//	Node dpNode = new Node(dependView);
		//
		//}
		DependencyGraph graph = new DependencyGraph();
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			graph.add(child);
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

		public int getRule(int verb) {
			return rules[verb];
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

	static class DependencyGraph {
		SparseArray<Node> keyNodes = new SparseArray<>();
		ArrayList<Node> nodes = new ArrayList<>();

		void add(View view) {
			Node node = new Node(view);
			int id = view.getId();

			if (id != View.NO_ID) {
				keyNodes.put(id, node);
			}
			nodes.add(node);
		}

		ArrayList<View> getSortedViews(int[] rules) {
			ArrayList<View> res = new ArrayList<>();
			for (int r = 0; r < rules.length; r++) {
				switch (r) {
					case ALIGN_TOP:
					case ALIGN_LEFT:
						for (Node n : nodes) {
							LayoutParams lp = (LayoutParams) n.view.getLayoutParams();
							int dependId = lp.getRule(r);
							if (dependId > 0) {
								Node dependNode = keyNodes.get(dependId);
								//对于每一个节点n，以及它依赖的节点dependNode，添加双向的依赖关系
								dependNode.dependents.put(r, n);
								n.dependencies.put(r, dependNode);
							}
						}
						break;
				}

			}
			//找到每个规则依赖的根节点，并根据依赖最少最先原则排序

		}
	}

	static class Node {
		View view;

		/**
		 * 用于存储被依赖的node
		 */
		SparseArray<Node> dependents = new SparseArray<>();
		/**
		 * 用于存储依赖的node
		 */
		SparseArray<Node> dependencies = new SparseArray<>();

		Node(View view) {
			this.view = view;
		}
	}
}
