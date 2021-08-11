package lhg.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lhg.common.R;


/**
 * 详见NestedPageScrollView注释
 */
public class NestedPageLayout extends ViewGroup {
    private int scrollViewHeight = 0;

    public NestedPageLayout(Context context) {
        this(context, null);
    }

    public NestedPageLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedPageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public int getScrollViewHeight() {
        return scrollViewHeight;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int y = getPaddingTop();
        int left = getPaddingLeft();
        int right = getWidth() - getPaddingRight();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int height = child.getMeasuredHeight();
            y += lp.topMargin;
            child.layout(left + lp.getMarginStart(), y, right - lp.getMarginEnd(), y + height);
            y += height + lp.bottomMargin;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup scrollView = (ViewGroup) getParent();
        boolean scrollViewExactly = scrollView.getLayoutParams().height != LayoutParams.WRAP_CONTENT;
        scrollViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        int outHeight = getPaddingTop() + getPaddingBottom();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                    getPaddingLeft() + getPaddingRight() + lp.getMarginStart() + lp.getMarginEnd(), lp.width);
            int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                    getPaddingTop() + getPaddingBottom(), lp.height);

            if (lp.height == LayoutParams.MATCH_PARENT && scrollViewExactly) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(scrollViewHeight, MeasureSpec.EXACTLY);
            } else if (lp.maxMatchParent && lp.height == LayoutParams.WRAP_CONTENT && scrollViewExactly) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(scrollViewHeight, MeasureSpec.AT_MOST);
            }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            outHeight += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        }
        if (scrollViewExactly && scrollViewHeight > outHeight) {
            outHeight = scrollViewHeight;
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), outHeight);
    }


    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p) && p instanceof LayoutParams;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public boolean maxMatchParent = false;
        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.NestedPageLayout_LayoutParams);
            this.maxMatchParent = a.getBoolean(R.styleable.NestedPageLayout_LayoutParams_layout_maxMatchParent, false);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
