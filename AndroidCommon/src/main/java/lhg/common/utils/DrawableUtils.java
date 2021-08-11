package lhg.common.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import androidx.core.graphics.ColorUtils;

import lhg.common.R;

import java.util.Arrays;
import java.util.List;

public class DrawableUtils {

    private static boolean findInts(List<int[]> list, int[] ints) {
        for (int[] a : list) {
            if (Arrays.equals(ints, a)) {
                return true;
            }
        }
        return false;
    }

    public static StateListDrawable clone(StateListDrawable stateListDrawable, List<int[]> filters) {
        StateListDrawable result = new StateListDrawable() ;
        for (int i = 0; i < stateListDrawable.getStateCount(); i++) {
            Drawable drawable = stateListDrawable.getStateDrawable(i);
            int[] stateSet = stateListDrawable.getStateSet(i);
            if (!findInts(filters, stateSet)) {
                result.addState(stateSet, drawable);
            }
        }
        return result;
    }


    public static StateListDrawable listItemBackgroundGray(Context context) {
        Resources resources = context.getResources();
        int selectColor = resources.getColor(R.color.listitem_background_selected);
        ColorDrawable normal = new ColorDrawable(resources.getColor(R.color.listitem_background_normal));
        ColorDrawable select = new ColorDrawable(selectColor);
        ColorDrawable disable = new ColorDrawable(ColorUtils.blendARGB(selectColor, 0x77999999, 0.6f));
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_enabled}, disable);//
        drawable.addState(new int[]{android.R.attr.state_pressed}, select);//
        drawable.addState(new int[]{android.R.attr.state_selected}, select);//
        drawable.addState(new int[]{android.R.attr.state_checked}, select);//
        drawable.addState(new int[]{}, normal);//
        return drawable;
    }

    public static StateListDrawable listItemBackgroundPrimary(Context context) {
        Resources resources = context.getResources();
        int selectColor = lhg.common.utils.ColorUtils.getSelectBackgroundColor(context);
        ColorDrawable normal = new ColorDrawable(resources.getColor(R.color.listitem_background_normal));
        ColorDrawable select = new ColorDrawable(selectColor);
        ColorDrawable disable = new ColorDrawable(ColorUtils.blendARGB(selectColor, 0x77999999, 0.6f));
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_enabled}, disable);//
        drawable.addState(new int[]{android.R.attr.state_pressed}, select);//
        drawable.addState(new int[]{android.R.attr.state_selected}, select);//
        drawable.addState(new int[]{android.R.attr.state_checked}, select);//
        drawable.addState(new int[]{}, normal);//
        return drawable;
    }

    public static Drawable stateListRectDrawable(int color) {
        ColorDrawable normal = new ColorDrawable(color);
        ColorDrawable select = new ColorDrawable(ColorUtils.blendARGB(color, 0x77000000, 0.3f));
        ColorDrawable disable = new ColorDrawable(ColorUtils.blendARGB(color, 0x77999999, 0.6f));
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_enabled}, disable);//
        drawable.addState(new int[]{android.R.attr.state_pressed}, select);//
        drawable.addState(new int[]{android.R.attr.state_selected}, select);//
        drawable.addState(new int[]{android.R.attr.state_checked}, select);//
        drawable.addState(new int[]{}, normal);//
        return drawable;
    }

    public static Drawable stateListRectDrawable(int color, int radius) {
        Drawable normal = rectDrawable(color, radius);
        Drawable select = rectDrawable(ColorUtils.blendARGB(color, 0x77000000, 0.3f), radius);
        Drawable disable = rectDrawable(ColorUtils.blendARGB(color, 0x77999999, 0.6f), radius);
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_enabled}, disable);//
        drawable.addState(new int[]{android.R.attr.state_pressed}, select);//
        drawable.addState(new int[]{android.R.attr.state_selected}, select);//
        drawable.addState(new int[]{android.R.attr.state_checked}, select);//
        drawable.addState(new int[]{}, normal);//
        return drawable;
    }

    public static Drawable rectDrawable(int color, int radiiInPx) {
        int r = radiiInPx;
        float[] outerR = new float[]{r, r, r, r, r, r, r, r};
        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(color);
        return drawable;
    }


    public static ColorStateList colorStateList(int normal, int select) {
        int disable = ColorUtils.blendARGB(normal, 0x77999999, 0.6f);
        int[][] states = new int[5][];
        int i = 0;
        states[i++] = new int[]{-android.R.attr.state_enabled};
        states[i++] = new int[]{android.R.attr.state_pressed};
        states[i++] = new int[]{android.R.attr.state_selected};
        states[i++] = new int[]{android.R.attr.state_checked};
        states[i++] = new int[]{};
        int[] colors = {
                disable, select, select, select, normal
        };
        return new ColorStateList(states, colors);
    }
}
