package lhg.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * Created by lhg on 2017/12/7.
 */

public class LineTextView extends androidx.appcompat.widget.AppCompatTextView {
    public LineTextView(Context context) {
        super(context);
    }

    public LineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    int lineDividerHeight = 1;
    Paint linePaint = new Paint();
    Rect mRect = new Rect();
    {
        linePaint.setColor(Color.DKGRAY);
    }

    public void setLineColor(int color) {
        linePaint.setColor(color);
        invalidate();
    }

    public void setLineDividerHeight(int lineDividerHeight) {
        this.lineDividerHeight = lineDividerHeight;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = getLineCount();

        float lineSpaceingExtra = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            lineSpaceingExtra = getLineSpacingExtra();
        }
        for (int i = 0; i < count; i++) {
            getLineBounds(i, mRect);
            canvas.drawLine( mRect.left,
                    (int) (mRect.bottom -  lineSpaceingExtra/ 2f - lineDividerHeight / 2),
                    mRect.right,
                    (int) (mRect.bottom - lineSpaceingExtra / 2f + lineDividerHeight / 2),
                    linePaint);
        }

    }

}
