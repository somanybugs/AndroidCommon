package lhg.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import lhg.common.R;


public class BubbleLayout extends FrameLayout {
    public static final int DirectionTop = 1;
    public static final int DirectionBottom = 2;
    public static final int DirectionLeft = 3;
    public static final int DirectionRight = 4;

    private int mArrowWidth = 20;
    private int mArrowHeight= 8;
    private int mArrowDirection = DirectionBottom;
    private int mRadius = 4;
    private int mSoldColor = Color.BLACK;
    private int mArrowOffset = -1;
    private int mShadowColor = mSoldColor;
    private int mShadowThickness = 2;
    private Paint mBorderPaint = new Paint();
    private Paint mSoldPaint = new Paint();
    private Path mBoderPath = null;
//    private Path mSoldPath = null;

    public BubbleLayout(Context context) {
        this(context, null);
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        // set Xfermode for source and shadow overlap
        mBorderPaint.setMaskFilter(new BlurMaskFilter(mShadowThickness, BlurMaskFilter.Blur.OUTER));
        mSoldPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        mSoldPaint.setStyle(Paint.Style.FILL);
        mSoldPaint.setAntiAlias(true);

        mArrowWidth = dip2px(mArrowWidth);
        mArrowHeight = dip2px(mArrowHeight);
        mRadius = dip2px(mRadius);
        mShadowThickness = dip2px(mShadowThickness);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BubbleLayout);
        mArrowWidth = ta.getDimensionPixelOffset(R.styleable.BubbleLayout_bbl_arrowWidth, mArrowWidth);
        mArrowHeight = ta.getDimensionPixelOffset(R.styleable.BubbleLayout_bbl_arrowHeight, mArrowHeight);
        mSoldColor = ta.getColor(R.styleable.BubbleLayout_bbl_soldColor, mSoldColor);
        mShadowColor = ta.getColor(R.styleable.BubbleLayout_bbl_shadowColor, mSoldColor);
        mRadius = ta.getDimensionPixelOffset(R.styleable.BubbleLayout_bbl_radius, mRadius);
        ta.recycle();

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        updatePadding();
    }

    protected int dip2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void setRadius(int r) {
        this.mRadius = r;
        invalidateAll();
    }

    public void setShadowThickness(int s) {
        this.mShadowThickness = s;
        invalidateAll();
    }


    public void setArrowSize(int height, int width) {
        this.mArrowHeight = height;
        this.mArrowWidth = width;
        invalidateAll();
    }

    public void setArrowDirection(int d) {
        this.mArrowDirection = d;
        invalidateAll();
    }

    public int getArrowHeight() {
        return mArrowHeight;
    }

    public int getShadowThickness() {
        return mShadowThickness;
    }

    private void invalidateAll() {
        mBoderPath = null;
//        mSoldPath = null;
        updatePadding();
        invalidate();
        requestLayout();
    }

    private void initPath(boolean force) {
        if (force || mBoderPath == null) {
            mBoderPath = createPath(0, 0);
        }
//        if (force || mSoldPath == null) {
//            mSoldPath = createPath(mShadowThickness, mShadowThickness);
//        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        initPath(false);
        mSoldPaint.setColor(mSoldColor);
        canvas.drawPath(mBoderPath, mSoldPaint);

        if (mShadowThickness > 0) {
            mBorderPaint.setColor(mShadowColor);
            canvas.drawPath(mBoderPath, mBorderPaint);
        }
        super.dispatchDraw(canvas);
    }

    private Path createPathTop(Rect r) {
        int rd = mRadius*2;
        Path path = new Path();
        path.moveTo(mRadius + r.left, r.top);
        //左上角圆弧
        path.arcTo(new RectF(r.left, r.top, r.left + rd, r.top + rd), 270, -90);
        //左侧直线
        path.lineTo(r.left, r.bottom-mRadius);
        //左下角圆弧
        path.arcTo(new RectF(r.left, r.bottom-rd, r.left + rd, r.bottom), 180, -90);
        //下边直线
        path.lineTo(r.right - mRadius, r.bottom);
        //右下角圆弧
        path.arcTo(new RectF(r.right-rd, r.bottom-rd, r.right, r.bottom), 90, -90);
        //右侧直线
        path.lineTo(r.right, r.top + mRadius);
        //右上角圆弧
        path.arcTo(new RectF(r.right-rd, r.top, r.right, r.top+rd), 0, -90);

        //上册直线 三角形
        {
            int x1 = mArrowOffset + r.left;
            if (mArrowOffset == -1) {
                x1 = (r.width() - mArrowWidth) / 2 + r.left;
            }
            int x2 = x1 + mArrowWidth;

            path.lineTo(x2, r.top);
            path.lineTo((x1 + x2) / 2, r.top - mArrowHeight);
            path.lineTo(x1, r.top);
        }

        path.close();
        return path;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initPath(true);
    }

    private Path createPathLeft(Rect r) {
        int rd = mRadius*2;
        Path path = new Path();
        path.moveTo(mRadius + r.left, r.top);
        //左上角圆弧
        path.arcTo(new RectF(r.left, r.top, r.left + rd, r.top + rd), 270, -90);
        //左侧直线
        {
            int y1 = mArrowOffset + r.top;
            if (mArrowOffset == -1) {
                y1 = (r.height() - mArrowWidth) / 2 + r.top;
            }
            int y2 = y1 + mArrowWidth;

            path.lineTo(r.left, y1);
            path.lineTo((y1 + y2) / 2, r.left - mArrowHeight);
            path.lineTo(y2, r.left);
        }
        //左下角圆弧
        path.arcTo(new RectF(r.left, r.bottom-rd, r.left + rd, r.bottom), 180, -90);
        //下边直线
        path.lineTo(r.right - mRadius, r.bottom);
        //右下角圆弧
        path.arcTo(new RectF(r.right-rd, r.bottom-rd, r.right, r.bottom), 90, -90);
        //右侧直线
        path.lineTo(r.right, r.top + mRadius);
        //右上角圆弧
        path.arcTo(new RectF(r.right-rd, r.top, r.right, r.top+rd), 0, -90);

        path.close();
        return path;
    }

    private Path createPathBottom(Rect r) {
        int rd = mRadius*2;
        Path path = new Path();
        path.moveTo(mRadius + r.left, r.top);
        //左上角圆弧
        path.arcTo(new RectF(r.left, r.top, r.left + rd, r.top + rd), 270, -90);
        //左侧直线
        path.lineTo(r.left, r.bottom-mRadius);
        //左下角圆弧
        path.arcTo(new RectF(r.left, r.bottom-rd, r.left + rd, r.bottom), 180, -90);
        //下边直线
        {
            int x1 = mArrowOffset + r.left;
            if (mArrowOffset == -1) {
                x1 = (r.width() - mArrowWidth)/2 + r.left;
            }
            int x2 = x1 + mArrowWidth;

            path.lineTo(x1, r.bottom);
            path.lineTo((x1+x2)/2, r.bottom+mArrowHeight);
            path.lineTo(x2, r.bottom);

        }
        //右下角圆弧
        path.arcTo(new RectF(r.right-rd, r.bottom-rd, r.right, r.bottom), 90, -90);
        //右侧直线
        path.lineTo(r.right, r.top + mRadius);
        //右上角圆弧
        path.arcTo(new RectF(r.right-rd, r.top, r.right, r.top+rd), 0, -90);

        path.close();
        return path;
    }

    private Path createPathRight(Rect r) {
        int rd = mRadius*2;
        Path path = new Path();
        path.moveTo(mRadius + r.left, r.top);
        //左上角圆弧
        path.arcTo(new RectF(r.left, r.top, r.left + rd, r.top + rd), 270, -90);
        //左侧直线
        path.lineTo(r.left, r.bottom-mRadius);
        //左下角圆弧
        path.arcTo(new RectF(r.left, r.bottom-rd, r.left + rd, r.bottom), 180, -90);
        //下边直线
        path.lineTo(r.right - mRadius, r.bottom);
        //右下角圆弧
        path.arcTo(new RectF(r.right-rd, r.bottom-rd, r.right, r.bottom), 90, -90);
        //右侧直线
        {
            int y1 = mArrowOffset + r.top;
            if (mArrowOffset == -1) {
                y1 = (r.height() - mArrowWidth) / 2 + r.top;
            }
            int y2 = y1 + mArrowWidth;

            path.lineTo(r.right, y1);
            path.lineTo((y1 + y2) / 2, r.right + mArrowHeight);
            path.lineTo(y2, r.right);

        }
        //右上角圆弧
        path.arcTo(new RectF(r.right-rd, r.top, r.right, r.top+rd), 0, -90);

        path.close();
        return path;
    }

    private Path createPath(int xOffset, int yOffset) {
        int width = getWidth();
        int height = getHeight();
        Rect rect = new Rect(xOffset, yOffset, width - xOffset, height - yOffset);
        switch (mArrowDirection) {
            case DirectionTop:
                rect.top += mArrowHeight;
                return createPathTop(rect);
            case DirectionBottom:
                rect.bottom -= mArrowHeight;
                return createPathBottom(rect);
            case DirectionLeft:
                rect.left += mArrowHeight;
                return createPathLeft(rect);
            case DirectionRight:
                rect.right -= mArrowHeight;
                return createPathRight(rect);
        }
        return null;
    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            switch (mArrowDirection) {
//                case DirectionTop:
//                case DirectionBottom:
//                    setMeasuredDimension(getMeasuredWidth() + mShadowThickness*2, getMeasuredHeight()+ mShadowThickness*2 + mArrowHeight);
//                    break;
//                case DirectionLeft:
//                case DirectionRight:
//                    setMeasuredDimension(getMeasuredWidth() + mShadowThickness*2 + mArrowHeight, getMeasuredHeight()+ mShadowThickness*2 );
//                    break;
//            }
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        left += mShadowThickness;
//        top += mShadowThickness;
//        right -= mShadowThickness;
//        bottom -= mShadowThickness;
//        switch (mArrowDirection) {
//            case DirectionTop:
//                top += mArrowHeight;
//                break;
//            case DirectionBottom:
//                bottom -= mArrowHeight;
//                break;
//            case DirectionLeft:
//                left += mArrowHeight;
//                break;
//            case DirectionRight:
//                right -= mArrowHeight;
//                break;
//        }
//        super.onLayout(changed, left, top, right, bottom);
//    }

    private void updatePadding() {
        int s = mShadowThickness;// + mRadius;
        int h = mArrowHeight;
        switch (mArrowDirection) {
            case DirectionTop:
                setPadding(s, s+h, s, s);
                break;
            case DirectionBottom:
                setPadding(s, s, s, s+h);
                break;
            case DirectionLeft:
                setPadding(s+h, s, s, s);
                break;
            case DirectionRight:
                setPadding(s, s, s+h, s);
                break;
        }
    }
}
