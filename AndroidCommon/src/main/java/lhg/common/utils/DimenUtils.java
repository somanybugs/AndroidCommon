package lhg.common.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * Created by 刘浩歌 on 2015/7/14.
 */
public class DimenUtils {
    public static Point screenSizeInPixel(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        return new Point(Math.min(widthPixels, heightPixels), Math.max(widthPixels, heightPixels));
    }
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
