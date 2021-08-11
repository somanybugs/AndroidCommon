package lhg.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.List;


/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2021/2/1 10:47
 * Note: 实现内部view.layout_height=march_parent的功能,
 * 可以将NestedPageLayout的子view的高度设置为scrollview的高度,
 * 如果NestedPageLayout内部有NestedScrollingChild, 则滚动child时,
 * 优先滚动NestedPageScrollView, 等到child完全可见才会进行child的滚动
 */
public class NestedPageScrollView4 extends NestedScrollMultiView {
    private static final String TAG = "NestedPageScrollView4";

    public NestedPageScrollView4(@NonNull Context context) {
        super(context);
    }

    public NestedPageScrollView4(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedPageScrollView4(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (dy < 0 && target.canScrollVertically(-1)) {//手指从上向下
            View child = findDirectChild(target);
            int invisibleHeight = getInVisibleHeightByTop(child);
            if (invisibleHeight > 0) {
                consumed[1] = -Math.min(-dy, invisibleHeight);
            }
            scrollBy(0, consumed[1]);
            return;
        }
        if (dy > 0 && target.canScrollVertically(1)) {//手指从下向上
            View child = findDirectChild(target);
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
        int childTop = child.getTop() - getPaddingTop();
        int invisibleHeight = getScrollY() - childTop;
        return invisibleHeight;
    }

    private int getInVisibleHeightByBottom(View child) {
        int childTop = child.getTop() - getPaddingTop();
        int childBottom = childTop + child.getHeight();
        int scrollHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int invisibleHeight = childBottom - getScrollY() - scrollHeight;
        return invisibleHeight;
    }

    private View findDirectChild(View target) {
        View child = target;
        while (child != null && child.getParent() != this) {
            child = (View) child.getParent();
        }
        return child;
    }

    @Override
    public void requestChildFocus(View child, View focused) {//禁止自动滚动到焦点视图

    }
}
