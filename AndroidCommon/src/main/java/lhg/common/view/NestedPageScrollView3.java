package lhg.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;
import java.util.List;


/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2021/2/1 10:47
 * Note: 配合NestedPageLayout实现内部view.layout_height=march_parent的功能,
 * 可以将NestedPageLayout的子view的高度设置为scrollview的高度,
 * 如果NestedPageLayout内部有NestedScrollingChild, 则滚动child时,
 * 优先滚动NestedPageScrollView, 等到child完全可见才会进行child的滚动
 */
public class NestedPageScrollView3 extends NestedScrollView {
    private static final String TAG = "NestedPageScrollView";

    public NestedPageScrollView3(@NonNull Context context) {
        super(context);
    }

    public NestedPageScrollView3(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedPageScrollView3(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 0) {
            NestedPageLayout pageLayout = getPageLayout();
            final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) pageLayout.getLayoutParams();
            int scrollViewHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
            if (pageLayout.getScrollViewHeight() != scrollViewHeight) {
                int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                        getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
                int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                        getPaddingTop() + getPaddingBottom(), lp.height);
                pageLayout.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (dy < 0 && target.canScrollVertically(-1)) {//手指从上向下
            View child = findPageChild(target);
            int invisibleHeight = getInVisibleHeightByTop(child);
            if (invisibleHeight > 0) {
                consumed[1] = -Math.min(-dy, invisibleHeight);
            }
            scrollBy(0, consumed[1]);
            return;
        }
        if (dy > 0 && target.canScrollVertically(1)) {//手指从下向上
            View child = findPageChild(target);
            int invisibleHeight = getInVisibleHeightByBottom(child);
            if (invisibleHeight > 0) {
                consumed[1] = Math.min(dy, invisibleHeight);
            }
            scrollBy(0, consumed[1]);
            return;
        }
        super.onNestedPreScroll(target, dx, dy, consumed, type);
    }

    private final List<Integer> parentScrollYList = new ArrayList<>();
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        parentScrollYList.clear();
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            checkParentScrollY(parentScrollYList, false);
        }
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            checkParentScrollY(parentScrollYList, true);
            if (parentScrollYList.isEmpty()) {//parentScrollYList为空 说明 父视图没有滚动
                //Log.i("RTYHJKpp", "nestedScrollChild 停止惯性滚动" + target.getClass().getSimpleName());
                ViewCompat.stopNestedScroll(target, type);
            }
        }
    }

    private void checkParentScrollY(List<Integer> scrollYs, boolean check) {
        View v = this;
        while (v != null) {
            if (check) {
                if (scrollYs.isEmpty()) {
                    return;
                }
                if (scrollYs.get(0) != v.getScrollY()) {
                    break;
                }
                scrollYs.remove(0);
            } else {
                scrollYs.add(v.getScrollY());
            }
            if (v.getParent() == null || !(v.getParent() instanceof View)) {
                break;
            }
            v = (View) v.getParent();
        }
    }

    private int getInVisibleHeightByTop(View child) {
        ViewGroup pageLayout = getPageLayout();
        int childTop = child.getTop() + pageLayout.getTop() - getPaddingTop();
        int invisibleHeight = getScrollY() - childTop;
        return invisibleHeight;
//        Log.i(TAG, "pageLayout.top="+pageLayout.getTop() + ", child.top=" + child.getTop());
    }
    private int getInVisibleHeightByBottom(View child) {
        ViewGroup pageLayout = getPageLayout();
        int childTop = child.getTop() + pageLayout.getTop() - getPaddingTop();
        int childBottom = childTop + child.getHeight();
        int scrollHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int invisibleHeight = childBottom - getScrollY() - scrollHeight;
        return invisibleHeight;
//        Log.i(TAG, "pageLayout.top="+pageLayout.getTop() + ", child.top=" + child.getTop());
    }

    private View findFirstView() {
        //寻找第一个露头的view
        ViewGroup pageLayout = getPageLayout();
        for (int i = 0; i < pageLayout.getChildCount(); i++) {
            View child = pageLayout.getChildAt(i);
            int childTop = child.getTop() + pageLayout.getTop() - getPaddingTop();
            int childBottom = childTop + child.getHeight();
            if (childTop <= getScrollY() && childBottom > getScrollY()) {
                return child;
            }
        }
        return pageLayout.getChildAt(0);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (!(child instanceof NestedPageLayout)) {
            throw new IllegalStateException("only NestedPageLayout can be added");
        }
    }

    private NestedPageLayout getPageLayout() {
        View root = getChildAt(0);
        if (root instanceof NestedPageLayout) {
            return (NestedPageLayout) root;
        }
        throw new IllegalStateException("must has child NestedPageLayout");
    }

    private View findPageChild(View target) {
        View root = getPageLayout();
        View child = target;
        while (child.getParent() != root) {
            child = (View) child.getParent();
        }
        return child;
    }

    @Override
    public void requestChildFocus(View child, View focused) {//禁止自动滚动到焦点视图

    }
}
