package lhg.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public class FloatTitleLayout extends FrameLayout implements CoordinatorLayout.AttachedBehavior {
    private OverScrollListener overScrollListener;
    private int overScrollHeight = 0;// 单位 像素px

    public FloatTitleLayout(@NonNull Context context) {
        super(context);
    }

    public FloatTitleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatTitleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOverScrollHeight(int overScrollHeight) {
        this.overScrollHeight = overScrollHeight;
    }

    public void setOverScrollListener(OverScrollListener overScrollListener) {
        this.overScrollListener = overScrollListener;
    }

    private static FloatTitleLayout findFloatTitle(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof FloatTitleLayout) {
                return (FloatTitleLayout) v;
            }
        }
        return null;
    }
    private static View findMainLayout(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            if (lp == null || !(lp instanceof CoordinatorLayout.LayoutParams)) {
                continue;
            }
            if (((CoordinatorLayout.LayoutParams) lp).getBehavior() == null) {
                continue;
            }
            if (((CoordinatorLayout.LayoutParams) lp).getBehavior() instanceof MainBehavior) {
                return v;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public CoordinatorLayout.Behavior getBehavior() {
        return new MyBehavior();
    }

    static class MyBehavior extends CoordinatorLayout.Behavior<FloatTitleLayout> {

        @Override
        public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull FloatTitleLayout child, @NonNull View dependency) {

            if (dependency instanceof AppBarLayout) {
                return true;
            }
            return false;
        }

        @Override
        public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull FloatTitleLayout child, @NonNull View dependency) {
            int totalOverHeight = child.overScrollHeight;
            if (totalOverHeight <= 0) {
                totalOverHeight = child.getHeight();
            }
            float offset = 0;
            if (dependency.getTop() >= child.getTop()) {
                offset = 0;
            } else if (child.getTop() - dependency.getTop() > totalOverHeight) {
                offset = 1;
            } else {
                offset = (child.getTop() - dependency.getTop()) * 1f / totalOverHeight;
            }
            if (child.overScrollListener != null) {
                child.overScrollListener.callback(offset);
            }
            return true;
        }
    }

    public static class BarBehavior extends AppBarLayout.Behavior {
        public BarBehavior() {
        }

        public BarBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        WeakReference<AppBarLayout> appBarLayoutWeakReference;

        @Override
        public boolean setTopAndBottomOffset(int offset) {
            AppBarLayout appBarLayout = appBarLayoutWeakReference.get();
            if (appBarLayout != null) {
                ViewGroup parentView = (ViewGroup) appBarLayout.getParent();
                FloatTitleLayout floatTitleLayout = findFloatTitle(parentView);
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                View mainLayout = findMainLayout(parentView);
                if (mainLayout == null || mainLayout.getVisibility() == View.GONE) {
                    //main不显示,调整bar位置, 兼容不需要mainlayout的情况, 防止滑不动
                    if (offset < 0 && offset + totalScrollRange < parentView.getHeight()) {
                        offset = Math.min(0, parentView.getHeight() - totalScrollRange);
                    }
                }

                if (floatTitleLayout != null) {
                    int titleHeight = floatTitleLayout.getHeight();
                    if (offset + totalScrollRange < titleHeight) {
                        offset = titleHeight - totalScrollRange;
                    }
                }
            }
            return super.setTopAndBottomOffset(offset);
        }

        @Override
        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout child, View target, int dx, int dy, int[] consumed, int type) {
            int oldConsumedY = consumed[1];
            int oldOffsetY = getTopAndBottomOffset();
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
            if (oldConsumedY != consumed[1] && oldOffsetY == getTopAndBottomOffset()) {
                //如果super.onNestedPreScroll 内部消费了 滑动, 但是 appbarlayout 的 offset被我们子类覆盖修改了
                //则恢复consumed
                consumed[1] = oldConsumedY;
            }
        }

        Object overScroller = null;
        private OverScroller getOverScroller() {
            if (overScroller != null) {
                return (OverScroller) overScroller;
            }
            try {
                for (Class clazz = getClass(); clazz != Object.class && clazz != null; clazz = clazz.getSuperclass()) {
                    for (Field f : clazz.getDeclaredFields()) {
                        if (OverScroller.class.isAssignableFrom(f.getType())) {
                            f.setAccessible(true);
                            return (OverScroller) (overScroller = f.get(this));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                // google包内部的bug
                // 修复applayout超过屏幕长度之后的抖动问题
                OverScroller scroller = getOverScroller();
                if (scroller != null) {
                    scroller.abortAnimation();
                }
            }
            return super.onInterceptTouchEvent(parent, child, ev);
        }


        @Override
        public boolean onMeasureChild(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
            appBarLayoutWeakReference = new WeakReference<>(child);
            return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
        }
    }


    public static class MainBehavior extends AppBarLayout.ScrollingViewBehavior {
        public MainBehavior() {
        }

        public MainBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return dependency instanceof FloatTitleLayout || super.layoutDependsOn(parent, child, dependency);
        }

        @Override
        public boolean onMeasureChild(@NonNull CoordinatorLayout parent, @NonNull View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
            FloatTitleLayout titleLayout = findFloatTitle(parent);
            if (titleLayout != null && titleLayout.getMeasuredHeight() > 0) {
                heightUsed += titleLayout.getMeasuredHeight();
            }
            return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
        }

    }

    public interface OverScrollListener {
        /**
         * @param offset 0..1
         */
        void callback(float offset);
    }
}
