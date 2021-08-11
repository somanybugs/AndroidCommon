package lhg.common.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import androidx.core.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import lhg.common.R;

import java.util.Arrays;

public class ImageTextView extends RatioFrameLayout {
    TextView textView;
    int color = Color.TRANSPARENT;
    int radius = 0;
    ShapeDrawable normal, select, disable;
    int iconPadding = 0;


    public ImageTextView(Context context) {
        this(context, null);
    }

    public ImageTextView(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs) {
        RoundRectShape shape = new RoundRectShape(null, null, null);
        normal = new ShapeDrawable(shape);
        select = new ShapeDrawable(shape);
        disable = new ShapeDrawable(shape);
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_enabled}, disable);//  状态  , 设置按下的图片
        drawable.addState(new int[]{android.R.attr.state_pressed}, select);//  状态  , 设置按下的图片
        drawable.addState(new int[]{}, normal);//默认状态,默认状态下的图片
        setBackground(drawable);

        textView = new TextView(getContext());
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setLayoutParams(
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER)
        );
        int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
                getContext().getResources().getDisplayMetrics());
        int iconPadding = textSize/4;
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ImageTextView);
        setTextColor(ta.getColorStateList(R.styleable.ImageTextView_android_textColor));
        setTextSize(ta.getDimensionPixelSize(R.styleable.ImageTextView_android_textSize, textSize));
        setColor(ta.getColor(R.styleable.ImageTextView_itv_color, color));
        setRadius(ta.getDimensionPixelSize(R.styleable.ImageTextView_itv_radius, 0));
        setIcon(ta.getDrawable(R.styleable.ImageTextView_itv_icon));
        setText(ta.getString(R.styleable.ImageTextView_android_text));
        setIconPadding(ta.getDimensionPixelSize(R.styleable.ImageTextView_itv_icon_padding, iconPadding));
        ta.recycle();

        addView(textView);
    }

    public void setText(String string) {
        textView.setText(string);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setTextColor(ColorStateList colors) {
        textView.setTextColor(colors);
    }

    public void setTextSize(int textSize) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
    public void setTextSize(int unit, float size) {
        textView.setTextSize(unit, size);
    }

    public void setRadius(int radius) {
        this.radius = radius;
        float[] outerR = new float[8];
        Arrays.fill(outerR, radius);
        RoundRectShape shape = new RoundRectShape(outerR, null, null);
        normal.setShape(shape);
        select.setShape(shape);
        disable.setShape(shape);
    }

    public void setColor(int color) {
        this.color = color;
        normal.getPaint().setColor(color);
        select.getPaint().setColor(ColorUtils.blendARGB(color, 0x77999999, 0.5f));
        disable.getPaint().setColor(ColorUtils.blendARGB(color, 0x77999999, 0.7f));
        normal.invalidateSelf();
        select.invalidateSelf();
        disable.invalidateSelf();
    }

    public void setIcon(Drawable icon) {
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        textView.setCompoundDrawables(null, icon, null, null);
    }

    public void setIcon(int iconId) {
        Drawable icon = getResources().getDrawable(iconId);
        setIcon(icon);
    }

    public void setIconPadding(int iconPadding) {
        this.iconPadding = iconPadding;
        textView.setCompoundDrawablePadding(iconPadding);
    }
}
