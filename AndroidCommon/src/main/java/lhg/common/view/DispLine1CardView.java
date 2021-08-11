package lhg.common.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import lhg.common.utils.DimenUtils;
import lhg.common.utils.DispLine;

import java.util.Arrays;
import java.util.List;

public class DispLine1CardView extends TableLayout {
    public DispLine1CardView(Context context) {
        this(context, null);
    }

    public DispLine1CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float[] outerR = new float[8];
        Arrays.fill(outerR, DimenUtils.dip2px(getContext(), 5));
        ShapeDrawable drawable = new ShapeDrawable(new RoundRectShape(outerR, null, null));
        drawable.getPaint().setColor(Color.WHITE);
        setBackground(drawable);

        setColumnStretchable(1, true);
    }

    public void setLines(List<DispLine> lines) {
        removeAllViews();
        int padding = DimenUtils.dip2px(getContext(), 8);
        for (DispLine line : lines) {
            TableRow row = new TableRow(getContext());
            row.setPadding(padding,padding,padding,padding);
            TextView title = new TextView(getContext());
            title.setText(line.name);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            title.setTextColor(line.nameColor);
            title.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.addView(title);
            TextView value = new TextView(getContext());
            value.setText(line.name);
            value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            value.setTextColor(Color.BLACK);
            value.setPadding(padding, 0,0,0);
            value.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.addView(value);

            this.addView(row);

            View sep = new View(getContext());
            sep.setBackgroundColor(Color.LTGRAY);
            sep.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            this.addView(sep);
        }
    }


}
