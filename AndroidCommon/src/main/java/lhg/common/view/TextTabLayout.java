package lhg.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import lhg.common.R;
import lhg.common.utils.Utils;

import java.lang.ref.WeakReference;

/**
 *
 *
 * Author: liuhaoge
 * Date: 2020/10/20 11:14
 * Note: 只有文字的tablayout,可以设置文字的选中色和选中大小
 */
public class TextTabLayout extends HorizontalScrollView {

    public static int MODE_FIXED = 0;
    public static int MODE_SCROLL = 1;

    private InnerLayout container;//内部容器
    private int selectedIndex = 0;

    public static int Gravity_CENTER = 0;
    public static int Gravity_LEFT = 1;
    public static int Gravity_RIGHT = 2;

    private  int tabPaddingLeft;
    private  int tabPaddingTop;
    private  int tabPaddingRight;
    private  int tabPaddingBottom;
    private int tabGravity = Gravity_CENTER;
    private int tabMode = MODE_FIXED;
    private int textSelectedSize;//选中文字尺寸 px
    private int textUnSelectedSize;//未选中文字尺寸 px
    private int textSelectedColor = Color.BLACK;//选中文字颜色
    private int textUnSelectedColor = Color.GRAY;//未选中文字颜色
    private int tabMinWidth = 0;//item最小宽度


    private OnClickListener tabOnClickListener;
    private OnTabSelectedListener onTabSelectedListener;

    public TextTabLayout(@NonNull Context context) {
        this(context, null);
    }

    public TextTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextTabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setFillViewport(true);
        tabPaddingTop = tabPaddingBottom = dip2px(context, 8);
        tabPaddingRight = tabPaddingLeft = dip2px(context, 16);
        textSelectedSize = dip2px(context, 14);
        textUnSelectedSize = dip2px(context, 18);
        this.setHorizontalScrollBarEnabled(false);
        container = new InnerLayout(getContext());
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(container);
        if (attrs == null) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextTabLayout);
        this.setTabGravity(a.getInt(R.styleable.TextTabLayout_ttl_tabGravity, tabGravity));
        this.tabMinWidth = a.getDimensionPixelSize(R.styleable.TextTabLayout_ttl_tabMinWidth, tabMinWidth);
        this.tabPaddingLeft = a.getDimensionPixelSize(R.styleable.TextTabLayout_ttl_tabPaddingLeft, tabPaddingLeft);
        this.tabPaddingTop = a.getDimensionPixelSize(R.styleable.TextTabLayout_ttl_tabPaddingTop, tabPaddingTop);
        this.tabPaddingRight = a.getDimensionPixelSize(R.styleable.TextTabLayout_ttl_tabPaddingRight, tabPaddingRight);
        this.tabPaddingBottom = a.getDimensionPixelSize(R.styleable.TextTabLayout_ttl_tabPaddingBottom, tabPaddingBottom);
        this.setTextUnSelectedSize(a.getDimensionPixelSize(R.styleable.TextTabLayout_ttl_tabUnSelectedSize, textUnSelectedSize));
        this.setTextSelectedSize(a.getDimensionPixelSize(R.styleable.TextTabLayout_ttl_tabSelectedSize, textSelectedSize));
        this.setTextUnSelectedColor(a.getColor(R.styleable.TextTabLayout_ttl_tabUnSelectedColor, textUnSelectedColor));
        this.setTextSelectedColor(a.getColor(R.styleable.TextTabLayout_ttl_tabSelectedColor, textSelectedColor));
        this.setTabMode(a.getInt(R.styleable.TextTabLayout_ttl_tabMode, tabMode));
        this.container.tabMinWidth = tabMinWidth;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        this.onTabSelectedListener = onTabSelectedListener;
    }

    public void setTabGravity(final int tabGravity) {
        this.tabGravity = tabGravity;
        container.forEachChild(new InnerLayout.ForEachIterator() {
            @Override
            public void callback(int i, TextView tv) {
                updateTabGravity(tv);
            }
        });
    }

    private void updateTabGravity(TextView tv) {
        if (tabGravity == Gravity_LEFT) {
            tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        } else if (tabGravity == Gravity_RIGHT) {
            tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        } else {
            tv.setGravity(Gravity.CENTER);
        }
    }

    public void setTabMode(int mode) {
        this.tabMode = mode;
        this.container.tabMode = tabMode;
        container.forEachChild(new InnerLayout.ForEachIterator() {
            @Override
            public void callback(int i, TextView tv) {
                initTabView(i, tv);
            }
        });
        requestLayout();
    }

    public void setTextUnSelectedColor(int textUnSelectedColor) {
        this.textUnSelectedColor = textUnSelectedColor;
        updateAllTextColor();
    }

    public void setTextSelectedColor(int textSelectedColor) {
        this.textSelectedColor = textSelectedColor;
        updateAllTextColor();
    }

    public void setTextSelectedSize(int textSelectedSize) {
        this.textSelectedSize = textSelectedSize;
        updateAllTextSize();
        requestLayout();
    }

    public void setTextUnSelectedSize(int textUnSelectedSize) {
        this.textUnSelectedSize = textUnSelectedSize;
        updateAllTextSize();
        requestLayout();
    }

    private OnClickListener getTabOnClickListener() {
        if (tabOnClickListener == null) {
            tabOnClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = container.indexOfChild(v);
                    selectTab(index);
                    ViewPager viewPager = TextTabLayout.this.viewPager.get();
                    if (viewPager != null) {
                        viewPager.setCurrentItem(index);
                    }
                    if (onTabSelectedListener != null) {
                        onTabSelectedListener.onTabSelected(index);
                    }
                }
            };
        }
        return tabOnClickListener;
    }

    public void addTab(CharSequence text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        initTabView(container.getChildCount(), tv);
        tv.setOnClickListener(getTabOnClickListener());
        container.addView(tv);
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            selectTab(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    WeakReference<ViewPager> viewPager;
    public void setupWithViewPager(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(onPageChangeListener);
        this.viewPager = new WeakReference<>(viewPager);
        this.container.removeAllViews();
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter != null) {
            int adapterCount = pagerAdapter.getCount();
            for(int i = 0; i < adapterCount; ++i) {
                this.addTab(pagerAdapter.getPageTitle(i));
            }
            if (viewPager != null && adapterCount > 0) {
                int curItem = viewPager.getCurrentItem();
                if (curItem != this.getSelectedTabPosition() && curItem < this.getTabCount()) {
                    this.selectTab(curItem);
                }
            }
        }
    }

    public void selectTab(final int index) {
        if (index == selectedIndex) {
            return;
        }
        selectedIndex = index;
        container.forEachChild(new InnerLayout.ForEachIterator() {
            @Override
            public void callback(int i, TextView tv) {
                updateTextSize(i, tv);
                updateTextColor(i, tv);
            }
        });
        requestLayout();
    }

    public int getTabCount() {
        return container.getChildCount();
    }

    public int getSelectedTabPosition() {
        return selectedIndex;
    }


    private void initTabView(int index, TextView view) {
        updateTextColor(index, view);
        updateTextSize(index, view);
        updateTabGravity(view);
        view.setPadding(tabPaddingLeft, tabPaddingTop, tabPaddingRight, tabPaddingBottom);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void updateAllTextSize() {
        container.forEachChild(new InnerLayout.ForEachIterator() {
            @Override
            public void callback(int i, TextView tv) {
                updateTextSize(i, tv);
            }
        });
    }
    private void updateAllTextColor() {
        container.forEachChild(new InnerLayout.ForEachIterator() {
            @Override
            public void callback(int i, TextView tv) {
                updateTextColor(i, tv);
            }
        });
    }

    private void updateTextColor(int index, TextView tv) {
        if (index == selectedIndex) {
            tv.setTextColor(textSelectedColor);
        } else {
            tv.setTextColor(textUnSelectedColor);
        }
    }


    private void updateTextSize(int index, TextView tv) {
        if (index == selectedIndex) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSelectedSize);
        } else {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textUnSelectedSize);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (tabMode == MODE_FIXED) {
            container.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY), container.getMeasuredHeightAndState());
        }
    }



    static class InnerLayout extends LinearLayout {
        int tabMode;
        int tabMinWidth;
        public interface ForEachIterator {
            void callback(int i, TextView tv);
        }

        public void forEachChild(ForEachIterator iterator) {
            for (int i =0;i<getChildCount(); i++) {
                TextView v = (TextView) getChildAt(i);
                iterator.callback(i, v);
            }
        }

        public InnerLayout(Context context) {
            super(context);
            setOrientation(HORIZONTAL);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int ourWidth = 0;
            for (int i =0, childCount = getChildCount();i<childCount; i++) {
                View v = getChildAt(i);
                int childWidthMeasureSpec = v.getMeasuredWidthAndState();
                int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
                if (tabMode == MODE_FIXED && MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec)/childCount, MeasureSpec.EXACTLY);
                }
                if (tabMode == MODE_SCROLL && tabMinWidth > 0) {
                    if (MeasureSpec.getSize(childHeightMeasureSpec) < tabMinWidth) {
                        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(tabMinWidth, MeasureSpec.EXACTLY);
                    }
                }
                v.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                ourWidth += v.getMeasuredWidth();
            }
            setMeasuredDimension(ourWidth, getMeasuredHeight());
        }
    }


    public static interface OnTabSelectedListener {
        void onTabSelected(int index);
    }
}
