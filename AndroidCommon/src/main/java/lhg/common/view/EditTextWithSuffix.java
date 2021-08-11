package lhg.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import lhg.common.R;

/**
 * Created by lhg on 15/10/9.
 */
public class EditTextWithSuffix extends EditText {

    String suffix;
    Bitmap suffixBitmap;
    Canvas suffixCanvas;

    public EditTextWithSuffix(Context context) {
        this(context, null);
    }

    public EditTextWithSuffix(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditTextWithSuffix(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EditTextWithSuffix);
        String suffix = a.getString(R.styleable.EditTextWithSuffix_suffix);
        setSuffix(suffix);
        a.recycle();
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (suffixBitmap != null && ! suffixBitmap.isRecycled()) {
            suffixBitmap.recycle();
            suffixBitmap = null;
        }
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
        Drawable drawableRight = null;
        if (!TextUtils.isEmpty(suffix)) {
            Paint paint =  (getPaint());
            Rect bounds = new Rect();
            paint.getTextBounds(suffix, 0, suffix.length(), bounds);
            if (suffixBitmap != null) {
                suffixBitmap.recycle();
            }
            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            int baseline = (bounds.height() - fontMetrics.bottom - fontMetrics.top ) / 2;
            suffixBitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.RGB_565);
            suffixCanvas = new Canvas(suffixBitmap);
            suffixCanvas.drawText(suffix, 0, baseline, paint);
            drawableRight = new BitmapDrawable(suffixBitmap);
            drawableRight.setBounds(0, 0, bounds.width(), bounds.height()); //设置边界
        }
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawables[1], drawableRight, drawables[3]);//画在右边
    }
}
