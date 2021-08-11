package lhg.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import lhg.common.R;


public class RatioFrameLayout extends FrameLayout {

    float heightRatio = 1;

    public RatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
    }

    public RatioFrameLayout(Context context) {
        super(context);
    }

    @SuppressLint("NewApi")
    public RatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttrs(context, attrs);
    }

    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RatioFrameLayout);
        heightRatio = ta.getFloat(R.styleable.RatioFrameLayout_rfl_heightRatio, heightRatio);
        ta.recycle();
    }

    public void setHeightRatio(float heightRatio) {
        if (this.heightRatio != heightRatio) {
            this.heightRatio = heightRatio;
            requestLayout();
            invalidate();
        }
    }

    public float getHeightRatio() {
        return heightRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int width = getDefaultSize(0, widthMeasureSpec);
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int) (width * getHeightRatio()), MeasureSpec.EXACTLY));
    }
}
