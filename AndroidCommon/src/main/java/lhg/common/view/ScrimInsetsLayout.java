package lhg.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import lhg.common.R;


public class ScrimInsetsLayout extends FrameLayout {
    private static final String TAG = "ScrimInsetsLayout";
    private static final int[] THEME_ATTRS = {
            android.R.attr.colorPrimaryDark
    };

    private Drawable mStatusBarBackground;

    private Object mInsets;
    private boolean mDrawStatusBarBackground;

    public ScrimInsetsLayout(@NonNull Context context) {
        this(context, null);
    }

    public ScrimInsetsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrimInsetsLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        if (ViewCompat.getFitsSystemWindows(this)) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScrimInsetsLayout);
            mStatusBarBackground = ta.getDrawable(R.styleable.ScrimInsetsLayout_statusBarBackground);
            ta.recycle();

            if (Build.VERSION.SDK_INT >= 21) {
                ViewCompat.setOnApplyWindowInsetsListener(this, (v, insets) -> {
                    setChildInsets(insets, insets.getSystemWindowInsetTop() > 0);
                    return insets.consumeSystemWindowInsets();
                });

                if (mStatusBarBackground == null) {
                    final TypedArray a = context.obtainStyledAttributes(THEME_ATTRS);
                    try {
                        mStatusBarBackground = a.getDrawable(0);
                    } finally {
                        a.recycle();
                    }
                }
                setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                mStatusBarBackground = null;
            }
        }
    }

    public void setChildInsets(Object insets, boolean draw) {
        this.mInsets = insets;
        mDrawStatusBarBackground = draw;
        setWillNotDraw(!draw && getBackground() == null);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final boolean applyInsets = mInsets != null && ViewCompat.getFitsSystemWindows(this);
        if (applyInsets && Build.VERSION.SDK_INT >= 20) {
            final WindowInsets wi = (WindowInsets) mInsets;
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (ViewCompat.getFitsSystemWindows(child)) {
                    child.dispatchApplyWindowInsets(wi);
                } else if (lp.applyWindowInsets == LayoutParams.ApplyWindowInsets_Margin) {
//                    child.setPadding(0, 0, 0, 0);
                    lp.setMargins(wi.getSystemWindowInsetLeft(), wi.getSystemWindowInsetTop(), wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
                } else if (lp.applyWindowInsets == LayoutParams.ApplyWindowInsets_Padding) {
                    child.setPadding(wi.getSystemWindowInsetLeft(), wi.getSystemWindowInsetTop(), wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
//                    lp.setMargins(0, 0, 0, 0);
                }
//                else {
//                    child.setPadding(0, 0, 0, 0);
//                    lp.setMargins(0, 0, 0, 0);
//                }

            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setStatusBarBackground(@Nullable Drawable bg) {
        mStatusBarBackground = bg;
        invalidate();
    }


    @Nullable
    public Drawable getStatusBarBackgroundDrawable() {
        return mStatusBarBackground;
    }


    public void setStatusBarBackground(int resId) {
        mStatusBarBackground = resId != 0 ? ContextCompat.getDrawable(getContext(), resId) : null;
        invalidate();
    }


    public void setStatusBarBackgroundColor(@ColorInt int color) {
        mStatusBarBackground = new ColorDrawable(color);
        invalidate();
    }


    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        if (mDrawStatusBarBackground && mStatusBarBackground != null) {
            final int inset;
            if (Build.VERSION.SDK_INT >= 21) {
                inset = mInsets != null ? ((WindowInsets) mInsets).getSystemWindowInsetTop() : 0;
            } else {
                inset = 0;
            }
            if (inset > 0) {
//                int childCount = getChildCount();
//                boolean hasMargin = false;
//                for (int i = 0; i < childCount; i++) {
//                    final View child = getChildAt(i);
//                    if (child.getVisibility() == GONE) {
//                        continue;
//                    }
//                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
//                    if (lp.applyWindowInsets == LayoutParams.ApplyWindowInsets_Margin) {
//                        hasMargin = true;
//                        break;
//                    }
//                }
//                if (hasMargin) {
                    mStatusBarBackground.setBounds(0, 0, getWidth(), inset);
                    mStatusBarBackground.draw(c);
//                }
            }
        }
    }


    @Override
    protected FrameLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof LayoutParams) {
            return new LayoutParams((FrameLayout.LayoutParams) lp);
        } else if (lp instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) lp);
        } else {
            return new LayoutParams(lp);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public static final int ApplyWindowInsets_None = 0;
        public static final int ApplyWindowInsets_Margin = 1;
        public static final int ApplyWindowInsets_Padding = 2;
        public int applyWindowInsets = ApplyWindowInsets_None;

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ScrimInsetsLayout_LayoutParams);
            this.applyWindowInsets = a.getInt(R.styleable.ScrimInsetsLayout_LayoutParams_layout_applyWindowInsets, ApplyWindowInsets_None);
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
