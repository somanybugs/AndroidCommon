package lhg.common.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2021/1/20 9:59
 * Note:
 */
public class ReApplyInsetsLayout extends FrameLayout {

    public ReApplyInsetsLayout(@NonNull Context context) {
        this(context, null);
    }

    public ReApplyInsetsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReApplyInsetsLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            requestApplyInsets();
        }
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        WindowInsets result = super.onApplyWindowInsets(insets);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                WindowInsets tmp = getChildAt(i).dispatchApplyWindowInsets(insets);
                if (tmp.isConsumed()) {
                    result = tmp;
                }
            }
        }
        return result;
    }

}
