package lhg.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


import lhg.common.R;

import java.util.Arrays;

public class HGGridLayout extends ViewGroup {
	public static final int			INVALID_POSITION		= -1;

	public static final int			AUTO_FIT				= 0;
	public static final int			ROW_HEIGHT_WRAP			= -1;
	public static final int			ROW_HEIGHT_FILL			= -2;

	protected int					mHorizontalSpacing		= 0;
	protected int					mVerticalSpacing		= 0;
	private int						mNumColumns				= AUTO_FIT;
	private int						mColumnWidth			= 1;
	private int						mRowHeight				= 0;

	private int						mRequestedColumnWidth;
	private int						mRequestedNumColumns;
	private int 					mRequestRowHeight = ROW_HEIGHT_WRAP;
	private int 					mRequestHeightFillMinRows = 0;//当高度ROW_HEIGHT_FILL时,最少要分割的行数

//	private OnItemClickListener		mOnItemClickListener;
//	private OnItemLongClickListener	mOnItemLongClickListener;
//	private MyHierarchyChangeListener myHierarchyChangeListener = new MyHierarchyChangeListener();
//	private OnChildClickListsner 	mOnChildClickListsner = new OnChildClickListsner();

	public HGGridLayout(Context context) {
		this(context, null);
	}

	public HGGridLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("ClickableViewAccessibility")
	public HGGridLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HGGridLayout);
		setHorizontalSpacing(a.getDimensionPixelOffset(R.styleable.HGGridLayout_hg_horizontalSpacing, 0));
		setVerticalSpacing(a.getDimensionPixelOffset(R.styleable.HGGridLayout_hg_verticalSpacing, 0));
		setNumColumns(a.getInt(R.styleable.HGGridLayout_hg_numColumns, mNumColumns));
		setColumnWidth(a.getDimensionPixelSize(R.styleable.HGGridLayout_hg_columnWidth, 0));
		setRowHeight(a.getLayoutDimension(R.styleable.HGGridLayout_hg_rowHeight, 0));
		setHeightFillMinRows(a.getDimensionPixelSize(R.styleable.HGGridLayout_hg_heightFillMinRows, 0));

		a.recycle();

//		super.setOnHierarchyChangeListener(myHierarchyChangeListener);
	}

//	@Override
//	public void setOnHierarchyChangeListener(OnHierarchyChangeListener onHierarchyChangeListener) {
//		this.myHierarchyChangeListener.superListener = onHierarchyChangeListener;
//	}

	public void setRowHeight(int rowHeight) {
		if (rowHeight != mRequestRowHeight) {
			this.mRequestRowHeight = rowHeight;
			requestLayout();
		}
	}

	/**
	 * @param columnWidth
	 */
	private void setColumnWidth(int columnWidth) {
		if (columnWidth != mRequestedColumnWidth) {
			mRequestedColumnWidth = columnWidth;
			requestLayout();
		}
	}



	/**
	 * @param numColumns
	 */
	public void setNumColumns(int numColumns) {
		if (numColumns != mRequestedNumColumns) {
			mRequestedNumColumns = numColumns;
			requestLayout();
		}

	}

	/**
	 */
	public void setVerticalSpacing(int verticalSpacing) {
		if (verticalSpacing != mVerticalSpacing) {
			mVerticalSpacing = verticalSpacing;
			requestLayout();
		}
	}

	public void setHorizontalSpacing(int horizontalSpacing) {
		if (horizontalSpacing != mHorizontalSpacing) {
			mHorizontalSpacing = horizontalSpacing;
			requestLayout();
		}
	}


	public void setHeightFillMinRows(int heightFillMinRows) {
		if (mRequestHeightFillMinRows != heightFillMinRows) {
			this.mRequestHeightFillMinRows = heightFillMinRows;
			requestLayout();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		final int count = getChildCount();
		int layoutX = getPaddingLeft();
		int layoutY = getPaddingTop();
		int rowHeight = 0;
		for (int i = 0; i < count; i++) {

			final View child = this.getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = mRowHeight > 0 ? mRowHeight : child.getMeasuredHeight();
			child.layout(layoutX, layoutY, layoutX + width, layoutY + height);

			rowHeight = Math.max(rowHeight, height);
			if ((i + 1) % mNumColumns == 0 && layoutX > 0) {
				layoutX = getPaddingLeft();
				layoutY += rowHeight + mVerticalSpacing;
				rowHeight = 0;
			} else {
				layoutX += width + mHorizontalSpacing;
			}
		}
	}

	private void stretchChildren(int index, int count, int width, int height) {
		for (int i = index; i < index + count && i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v.getMeasuredHeight() != height || v.getMeasuredWidth() != width) {
				int childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
				int childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
				getChildAt(i).measure(childWidthSpec, childHeightSpec);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		determineColumns(widthMeasureSpec);
		int numCols = Math.min(getChildCount(), mNumColumns);
		int[] rowHeights = determineRows(heightMeasureSpec);
		int numRows = rowHeights.length;

		int childrenHeight = 0;
		for (int i = 0; i < rowHeights.length; i++) {
			int rowHeight = mRowHeight > 0 ? mRowHeight : rowHeights[i];
			childrenHeight += rowHeight;
			stretchChildren(i * mNumColumns, mNumColumns, mColumnWidth, rowHeight);
		}

		int ourWidth = (numCols * mColumnWidth) + ((numCols - 1) * mHorizontalSpacing)
				+ getPaddingLeft() + getPaddingRight();
		int outHeight = childrenHeight + (numRows - 1) * mVerticalSpacing
				+ getPaddingTop() + getPaddingBottom();

		if (heightMode == MeasureSpec.UNSPECIFIED) {
			heightSize = outHeight;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			heightSize = Math.min(heightSize, outHeight);
		}

		if (widthMode == MeasureSpec.UNSPECIFIED) {
			widthSize = ourWidth;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			widthSize = Math.min(ourWidth, widthSize);
		}
		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}

	private int[] determineRows(int heightMeasureSpec) {
		int count = getChildCount();
		int numRows = (count + mNumColumns - 1) / mNumColumns;
		int[] rowHeights = new int[numRows];
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (mRequestRowHeight == ROW_HEIGHT_FILL && heightMode == MeasureSpec.EXACTLY) {
			numRows = Math.max(numRows, mRequestHeightFillMinRows);
			rowHeights = new int[numRows];
			mRowHeight = (heightSize + mVerticalSpacing) / numRows - mVerticalSpacing;
			Arrays.fill(rowHeights, mRowHeight);
			return rowHeights;
		}

		mRowHeight = mRequestRowHeight;
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			LayoutParams p = child.getLayoutParams();
			if (p == null) {
				p = generateDefaultLayoutParams();
				child.setLayoutParams(p);
			}
			int childHeightSpec;
			if (mRowHeight > 0) {
				childHeightSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(mRowHeight, MeasureSpec.EXACTLY), 0, mRowHeight);
			} else {
				childHeightSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, p.height);
			}
			int childWidthSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(mColumnWidth, MeasureSpec.EXACTLY), 0, mColumnWidth);
			child.measure(childWidthSpec, childHeightSpec);

			int row = i / mNumColumns;
			rowHeights[row] = Math.max(rowHeights[row], child.getMeasuredHeight());
		}
		return rowHeights;
	}

	private void determineColumns(int widthMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			if (mRequestedColumnWidth <= 0) {
				//总宽度不确定,不能进行自适应列宽
				mColumnWidth = 0;
			} else {
				mColumnWidth = mRequestedColumnWidth;
			}
			if (mRequestedNumColumns <= 0) {
				mNumColumns = getChildCount();
			} else {
				mNumColumns = mRequestedNumColumns;
			}
			return;
		}

		int availableSpace = widthSize - getPaddingLeft() - getPaddingRight();
		if (availableSpace < 0) {
			availableSpace = 0;
		}
		if (mRequestedNumColumns <= 0) {// AUTO_FIT
			if (mRequestedColumnWidth > 0) {
				// Client told us to pick the number of columns
				mNumColumns = (availableSpace + mVerticalSpacing)
						/ (mRequestedColumnWidth + mVerticalSpacing);
			} else {
				// Just make up a number if we don't have enough info
				mNumColumns = 2;
			}
			if (mNumColumns <= 0) {
				mNumColumns = 1;
			}
		} else {
			mNumColumns = mRequestedNumColumns;
		}

		mColumnWidth = (availableSpace + mVerticalSpacing) / mNumColumns - mVerticalSpacing;
	}


//	int hitTest(int x, int y) {
//		int childCount = getChildCount();
//		for (int i = 0; i < childCount; i += mNumColumns) {
//			if (y <= getChildAt(i).getBottom()) {
//				for (int k = i; k < childCount; k++) {
//					if (x <= getChildAt(k).getRight()) {
//						return k;
//					}
//				}
//			}
//		}
//		return INVALID_POSITION;
//	}

	// //////////////touch
//
//	public OnItemClickListener getOnItemClickListener() {
//		return mOnItemClickListener;
//	}
//
//	public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
//		this.mOnItemClickListener = mOnItemClickListener;
//	}
//
//	public OnItemLongClickListener getOnItemLongClickListener() {
//		return mOnItemLongClickListener;
//	}
//
//	public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
//		this.mOnItemLongClickListener = mOnItemLongClickListener;
//	}

//
//	public static interface OnItemClickListener {
//		public void onItemClick(HGGridLayout parentView, int position);
//	}
//
//	public static interface OnItemLongClickListener {
//		public void onItemLongClick(HGGridLayout parentView, int position);
//	}

//	class MyHierarchyChangeListener implements OnHierarchyChangeListener {
//
//		OnHierarchyChangeListener superListener;
//		@Override
//		public void onChildViewAdded(View parent, View child) {
//			child.setOnClickListener(mOnChildClickListsner);
//			child.setOnLongClickListener(mOnChildClickListsner);
//			if (superListener != null) {
//				superListener.onChildViewAdded(parent, child);
//			}
//		}
//
//		@Override
//		public void onChildViewRemoved(View parent, View child) {
//			child.setOnClickListener(null);
//			child.setOnLongClickListener(null);
//			if (superListener != null) {
//				superListener.onChildViewRemoved(parent, child);
//			}
//		}
//	};

//	class OnChildClickListsner implements OnClickListener, OnLongClickListener {
//
//		int hitTest(View child) {
//			for (int i = 0, size = getChildCount(); i < size; i++) {
//				if (child == getChildAt(i)) {
//					return i;
//				}
//			}
//			return -1;
//		}
//
//		@Override
//		public void onClick(View view) {
//			int index = hitTest(view);
//			if (index >= 0) {
//				if (getOnItemClickListener() != null) {
//					getOnItemClickListener().onItemClick(HGGridLayout.this, index);
//				}
//			}
//		}
//
//		@Override
//		public boolean onLongClick(View view) {
//			int index = hitTest(view);
//			if (index >= 0) {
//				if (getOnItemLongClickListener() != null) {
//					getOnItemLongClickListener().onItemLongClick(HGGridLayout.this, index);
//				}
//			}
//			return true;
//		}
//	}
}
