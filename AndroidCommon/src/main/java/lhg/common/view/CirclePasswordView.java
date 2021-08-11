package lhg.common.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lhg.common.R;
import lhg.common.utils.Utils;


/**
 */

public class CirclePasswordView extends View {

    int circleRadius = 8;
    int circleBorder = 1;
    int totalCount = 4;
    int space = 20;
    StringBuilder text = new StringBuilder();
    ValueAnimator shakeAnimator;
    private Paint strokePaint;
    private Paint fullPaint;

    public CirclePasswordView(Context context) {
        super(context);
        getAttrs(context, null);
    }

    public CirclePasswordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
    }

    public CirclePasswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CirclePasswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttrs(context, attrs);
    }


    private void getAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CirclePasswordView);
            totalCount = ta.getInteger(R.styleable.CirclePasswordView_cpv_count, 4);
            ta.recycle();
        }
        initPaint();
        circleRadius = Utils.dip2px(context, circleRadius);
        circleBorder = Utils.dip2px(context, circleBorder);
        space = Utils.dip2px(context, space);
    }

    public void append(CharSequence text) {
        if (this.text.length() >= totalCount) {
            return;
        }
        this.text.append(text);
        if (this.text.length() > totalCount) {
            this.text.delete(totalCount, this.text.length());
        }
        invalidate();
    }

    public void backspace() {
        if (text.length() > 0) {
            text.deleteCharAt(text.length()-1);
            invalidate();
        }
    }

    public void initPaint() {
        if (strokePaint == null) {
            strokePaint = new Paint();
            strokePaint.setColor(Color.WHITE);
            strokePaint.setStrokeWidth(circleBorder);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setAntiAlias(true);
            fullPaint = new Paint();
            fullPaint.setAntiAlias(true);
            fullPaint.setColor(Color.WHITE);
            fullPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            fullPaint.setAntiAlias(true);
        }
    }

    public void clear() {
        if (text.length() > 0) {
            text.delete(0, text.length());
            invalidate();
        }
    }

    public void clearWithShake() {
        text.delete(0, text.length());
        if (shakeAnimator == null) {
            shakeAnimator = ValueAnimator.ofFloat(0f, 20f);
            shakeAnimator.setDuration(40);
            shakeAnimator.setRepeatCount(4);
            shakeAnimator.addUpdateListener(animation -> invalidate());
        }
        if (!shakeAnimator.isStarted()) {
            shakeAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (shakeAnimator != null) {
            shakeAnimator.end();
            shakeAnimator.removeAllUpdateListeners();
            shakeAnimator = null;
        }
    }



    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int shakeSpace = shakeSpace();
        int x = 0;
        int ballCount = text.length();

        if (shakeAnimator != null && shakeAnimator.isRunning()) {
            ballCount = totalCount;
            float val = (float) shakeAnimator.getAnimatedValue();
            if (val < 5) {
                x = (int) (shakeSpace*(1-val/5));
            } else if (val < 10) {
                x = (int) (shakeSpace*(val-5)/5);
            } else if (val < 15) {
                x = (int) (shakeSpace + (shakeSpace*(val-10)/5));
            } else {
                x = (int) (shakeSpace*2 - (shakeSpace*(val-15)/5));
            }
        } else {
            x = shakeSpace;
        }

        for (int i = 0; i < totalCount; i++) {
            if (i < ballCount) {
                canvas.drawCircle(x+circleRadius, circleRadius, circleRadius, fullPaint);
            } else {
                canvas.drawCircle(x+circleRadius, circleRadius, circleRadius-circleBorder, strokePaint);
            }

            x += circleRadius*2 + space;
        }
    }

    private int shakeSpace() {
        return  circleRadius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int ourWidth = totalCount * (circleRadius*2 + space) -space + 2*shakeSpace();
        int ourHeight = circleRadius * 2;

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = ourHeight;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = Math.min(heightSize, ourHeight);
        }

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = ourWidth;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = Math.min(ourWidth, widthSize);
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    public int length() {
        return text.length();
    }

    public boolean isFull() {
        return text.length() >= totalCount;
    }

    public String getText() {
        return text.toString();
    }
}
