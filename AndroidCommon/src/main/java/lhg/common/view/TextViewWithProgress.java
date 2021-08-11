package lhg.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lhg.common.R;

@SuppressLint("AppCompatCustomView")
public class TextViewWithProgress extends TextView {
    private int progressColor;
    private int progress = 0; // 0 , 100
    private Paint paint;

    public TextViewWithProgress(Context context) {
        super(context);
    }

    public TextViewWithProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context, attrs);
    }

    public TextViewWithProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextViewWithProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttrs(context, attrs);
    }

    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TextViewWithProgress);
        progressColor = ta.getColor(R.styleable.TextViewWithProgress_tvp_progress_color, Color.DKGRAY);
        ta.recycle();
        getProgressPaint();
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        getProgressPaint().setColor(progressColor);
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    private Paint getProgressPaint() {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(progressColor);
        }
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth()*progress/100f, getHeight(), getProgressPaint());
        super.onDraw(canvas);
    }
}
