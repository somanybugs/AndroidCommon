package lhg.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2021/3/25 16:39
 * Note:
 */
public class SuffixEditText extends AppCompatEditText {
    private String suffixHint = null;
    private int suffixHintPading = 8;
    private TextPaint suffixPaint = null;


    public SuffixEditText(@NonNull Context context) {
        super(context);
        init();
    }

    public SuffixEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuffixEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (suffixPaint == null) {
            suffixPaint = new TextPaint();
            suffixPaint.setAntiAlias(true);
            setSuffixHintColor(Color.BLACK);
            setSuffixHintTextSize(getTextSize());
            setSuffixHintPadding(8);
        }
    }

    public void setSuffixHintPadding(int suffixHintPading) {
        this.suffixHintPading = suffixHintPading;
        postInvalidate();
    }

    public void setSuffixHint(String suffixHint) {
        this.suffixHint = suffixHint;
        postInvalidate();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    public String getSuffixHint() {
        return suffixHint;
    }

    public void setSuffixHintColor(int color) {
        suffixPaint.setColor(color);
        postInvalidate();
    }

    public void setSuffixHintTextSize(float size) {
        suffixPaint.setTextSize(size);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (TextUtils.isEmpty(suffixHint)) {
            return;
        }
        float maxWidth = 0;
        for (int i = 0; i < getLayout().getLineCount(); i++) {
            maxWidth = Math.max(maxWidth, getLayout().getLineWidth(i));
        }
        int left = (int) (getCompoundPaddingLeft() + maxWidth + suffixHintPading);
        Paint.FontMetrics fontMetrics = suffixPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = getHeight() / 2 + distance;
        canvas.drawText(suffixHint, left, baseline, suffixPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
