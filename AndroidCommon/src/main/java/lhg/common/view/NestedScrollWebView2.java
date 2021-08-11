package lhg.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

/**
 *
 *
 * Author: liuhaoge
 * Date: 2020/10/17 11:44
 * Note:  支持嵌套滚动的webview
 */

public class NestedScrollWebView2 extends WebView implements NestedScrollingChild3 {



    private static final String TAG = "NestedWebView";



    private final NestedScrollingChildHelper mChildHelper;


    public NestedScrollWebView2(@NonNull Context context) {
        this(context, null);
    }

    public NestedScrollWebView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedScrollWebView2(@NonNull Context context, @Nullable AttributeSet attrs,
                                int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mChildHelper = new NestedScrollingChildHelper(this);

        // ...because why else would you be using this widget?
        setNestedScrollingEnabled(true);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        requestDisallowInterceptTouchEvent(true);
        return true;
    }

    // NestedScrollingChild3

//    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                     int dyUnconsumed, @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
        mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type, consumed);
    }

    // NestedScrollingChild2

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        mChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow,
                                           int type) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return startNestedScroll(axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void stopNestedScroll() {
        stopNestedScroll(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return hasNestedScrollingParent(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    boolean isFlinging = false;
    @Override
    public void computeScroll() {
        isFlinging = true;
        super.computeScroll();
        isFlinging = false;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        int type = isFlinging ? ViewCompat.TYPE_NON_TOUCH : ViewCompat.TYPE_TOUCH;
        nestedHelper.dispatchNestedPreScroll(this, scrollX-getScrollX(), scrollY-getScrollY(), type);
        Log.i("FGHJKLoo", "overScrollBy 父滑动 " + (nestedHelper.consumed[1]));
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        nestedHelper.dispatchNestedScroll(this);
    }
//
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        Log.i("FGHJKLoo", "overScrollBy " + deltaY + ", isTouchEvent=" + isTouchEvent);
        int type = isFlinging ? ViewCompat.TYPE_NON_TOUCH : ViewCompat.TYPE_TOUCH;
//        if (deltaY < 0) {
//            deltaY = Math.max(deltaY, -scrollY);
//        } else if (deltaY > 0) {
//            deltaY = Math.min(deltaY, scrollRangeY- scrollY);
//        }
//        nestedHelper.dispatchNestedPreScroll(this, deltaX, deltaY, type);
//        Log.i("FGHJKLoo", "overScrollBy 父滑动 " + (nestedHelper.consumed[1]));
//        boolean ret = super.overScrollBy(deltaX - nestedHelper.consumed[0], deltaY - nestedHelper.consumed[1], scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
//        nestedHelper.dispatchNestedScroll(this);
//        return ret;
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public void scrollBy(int x, int y) {
        Log.i("FGHJKLoo", "scrollBy " + y);
        int type = isFlinging ? ViewCompat.TYPE_NON_TOUCH : ViewCompat.TYPE_TOUCH;
        nestedHelper.dispatchNestedPreScroll(this, x, y, type);
        super.scrollBy(x - nestedHelper.consumed[0], y - nestedHelper.consumed[1]);
        nestedHelper.dispatchNestedScroll(this);
    }

    NestedHelper nestedHelper = new NestedHelper();
    @Override
    public void scrollTo(int x, int y) {
        Log.i("FGHJKLoo", "scrollTo " + y);
        int type = isFlinging ? ViewCompat.TYPE_NON_TOUCH : ViewCompat.TYPE_TOUCH;
        nestedHelper.dispatchNestedPreScroll(this, x-getScrollX(), y-getScrollY(), type);
        super.scrollTo(x - nestedHelper.consumed[0], y - nestedHelper.consumed[1]);
        nestedHelper.dispatchNestedScroll(this);

    }

    private static class NestedHelper {
        int dx;
        int dy;
        int[] consumed = new int[2];
        int type;
        int oldScrollX, oldScrollY;
        boolean isScrolling = false;

        public void dispatchNestedPreScroll(NestedScrollWebView2 view, int _dx, int _dy, int type) {
            if (isScrolling) {
                return;
            }
            view.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, type);
            isScrolling = true;
            this.dx = _dx;
            this.dy = _dy;
            consumed[0] = consumed[1] = 0;
            this.type = type;
            oldScrollX = view.getScrollX();
            oldScrollY = view.getScrollY();
            view.dispatchNestedPreScroll(dx, dy, consumed, null, type);
            dx -= consumed[0];
            dy -= consumed[1];
        }

        public void dispatchNestedScroll(NestedScrollWebView2 view) {
            if (!isScrolling) {
                return;
            }
            Log.i("FGHJKLoo", "overScrollBy 我滑动 " + (view.getScrollY() - oldScrollY));
            dx -= (view.getScrollX() - oldScrollX);
            dy -= (view.getScrollY() - oldScrollY);
            view.dispatchNestedScroll(
                    consumed[0] + view.getScrollX() - oldScrollX,
                    consumed[1] + view.getScrollY() - oldScrollY,
                    dx, dy, null, type
            );
            isScrolling = false;
            Log.i("FGHJKLoo", "overScrollBy 父滑后 " + (dy));
        }
    }

}

