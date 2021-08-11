package lhg.common.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;

public class ColorUtils {

    public static int getSelectBackgroundColor(Context context) {
        return 0x55000000 | (0xffffff & getPrimaryColor(context));
    }

    public static int getPrimaryColor(Context context) {
        final TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.colorPrimary});
        try {
            return a.getColor(0, 0);
        } catch (Exception e) {
            return Color.DKGRAY;
        } finally {
            a.recycle();
        }
    }

    public static int getAccentColor(Context context) {
        final TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.colorAccent});
        try {
            return a.getColor(0, 0);
        } catch (Exception e) {
            return Color.DKGRAY;
        } finally {
            a.recycle();
        }
    }

}
