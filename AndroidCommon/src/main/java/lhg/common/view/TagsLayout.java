package lhg.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2016/10/19 10:41
 * Note:
 */
public class TagsLayout extends ViewGroup {

	protected int	mHorizontalSpacing	= 0;
	protected int	mVerticalSpacing	= 0;
	protected boolean mStretchHorizontalSpacing = true;

	public TagsLayout(Context context) {
		this(context, null);
	}

	public TagsLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TagsLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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

	List<Integer> hSpaceList = new ArrayList<>();
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		final int count = getChildCount();
		int row = 0;
		int layoutX = 0;
		int layoutY = 0;
		int rowHeight = 0;

		hSpaceList.clear();
		if (mStretchHorizontalSpacing && count > 1) {
			int col = 0;
			for (int i = 0; i < count; i++) {
				final View child = this.getChildAt(i);
				int width = child.getMeasuredWidth();
				if (layoutX + width > r - l && layoutX > 0) {
					if (col > 1) {
						hSpaceList.add(Math.max((r - l - layoutX + mHorizontalSpacing) / (col - 1), 0) + mHorizontalSpacing);
					} else {
						hSpaceList.add(mHorizontalSpacing);
					}
					layoutX = 0;
					row++;
					col = 0;
				}
				col++;
				layoutX += width + mHorizontalSpacing;
			}
			if (col > 1) {
				hSpaceList.add(Math.max((r - l - layoutX + mHorizontalSpacing) / (col - 1), 0) + mHorizontalSpacing);
			} else {
				hSpaceList.add(mHorizontalSpacing);
			}
		}

		row = 0;
		layoutX = 0;
		layoutY = 0;
		rowHeight = 0;
		for (int i = 0; i < count; i++) {
			final View child = this.getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			rowHeight = Math.max(rowHeight, height);
			if (layoutX + width > r - l && layoutX > 0) {
				row++;
				layoutX = 0;
				layoutY += (rowHeight + mVerticalSpacing);
			}
			child.layout(layoutX, layoutY, layoutX + width, layoutY + height);
			int spaceH = mHorizontalSpacing;
			if (hSpaceList.size() > row) {
				spaceH = hSpaceList.get(row);
			}
			layoutX += width + spaceH;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthMode == MeasureSpec.UNSPECIFIED) {
			widthSize = Integer.MAX_VALUE;
		}

		int ourWidth = 0;
		int ourHeight = 0;

		final int count = getChildCount();
		int layoutX = 0;
		int layoutY = 0;
		int rowHeight = 0;
		for (int i = 0; i < count; i++) {

			final View child = this.getChildAt(i);
			LayoutParams p = child.getLayoutParams();
			if (p == null) {
				p = generateDefaultLayoutParams();
				child.setLayoutParams(p);
			}
			int childHeightSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0,
					p.height);
			int childWidthSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0,
					p.width);
			child.measure(childWidthSpec, childHeightSpec);
			
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();

			if (layoutX + width > widthSize && layoutX > 0) {
				layoutX = 0;
				layoutY += (rowHeight + mVerticalSpacing);
				rowHeight = 0;
			}
			rowHeight = Math.max(rowHeight, height);
			
			layoutX += width + mHorizontalSpacing;
			ourWidth = Math.max(ourWidth, layoutX - mHorizontalSpacing);
		}
		ourHeight = layoutY + rowHeight;


		if (heightMode == MeasureSpec.UNSPECIFIED) {
			heightSize = ourHeight;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			heightSize = Math.min(heightSize, ourHeight);
		}

		if (widthMode == MeasureSpec.UNSPECIFIED) {
			widthSize = Integer.MAX_VALUE;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			widthSize = Math.min(ourWidth, widthSize);
		}

		setMeasuredDimension(widthSize, heightSize);
	}

}
