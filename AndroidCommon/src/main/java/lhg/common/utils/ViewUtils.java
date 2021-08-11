package lhg.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import lhg.common.R;

import java.util.List;

/**
 * Created by lhg on 2017/6/13.
 */

public class ViewUtils {

    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        } else {
            return null;
        }
    }

    public static void addItemViewsAsGridLayout(LinearLayout ll, List<View> views, int cols, int padding) {
        if (views == null || views.isEmpty()) {
            return;
        }
//        ll.setPadding(padding, padding, padding, padding);

        LinearLayout line = null;
        for (int k = 0; k < views.size(); k++) {
            View view = views.get(k);
            int row = k / cols;
            int col = k % cols;
            if (col == 0) {
                line = new LinearLayout(ll.getContext());
                line.setWeightSum(cols);
                line.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
                if (row > 0) {
                    lineParams.topMargin = padding;
                }
                ll.addView(line, lineParams);
            }

            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            if (col > 0) {
                viewParams.leftMargin = padding;
            }
            line.addView(view, viewParams);
        }
    }

    public static void addItemViewsAsGridLayout(LinearLayout ll, List<View> views, int cellWidth, int cellHeight, int cols, int padding) {
        if (views == null || views.isEmpty()) {
            return;
        }
//        ll.setPadding(padding, padding, padding, padding);

        LinearLayout line = null;
        for (int k = 0; k < views.size(); k++) {
            View view = views.get(k);
            int row = k / cols;
            int col = k % cols;
            if (col == 0) {
                line = new LinearLayout(ll.getContext());
                line.setWeightSum(cols);
                line.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (row > 0) {
                    lineParams.topMargin = padding;
                }
                ll.addView(line, lineParams);
            }

            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(cellWidth, cellHeight);
            if (col > 0) {
                viewParams.leftMargin = padding;
            }
            line.addView(view, viewParams);
        }
    }


    //ll高度未知
    public static void addItemViewsAsGridLayout2(LinearLayout ll, List<? extends View> views, int cols, int padding) {
        if (views == null || views.isEmpty()) {
            return;
        }

        LinearLayout line = null;
        for (int k = 0; k < views.size(); k++) {
            View view = views.get(k);
            int row = k / cols;
            int col = k % cols;
            if (col == 0) {
                line = new LinearLayout(ll.getContext());
                line.setOrientation(LinearLayout.HORIZONTAL);
                line.setWeightSum(cols);
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (row > 0) {
                    lineParams.topMargin = padding;
                }
                ll.addView(line, lineParams);
            }

            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            if (col > 0) {
                viewParams.leftMargin = padding;
            }
            line.addView(view, viewParams);
        }
    }

    //ll 高度固定
    public static void addItemViewsAsGridLayout3(LinearLayout ll, List<View> views, int rows, int cols, int padding) {
        if (views == null || views.isEmpty()) {
            return;
        }
//        ll.setPadding(padding, padding, padding, padding);
        ll.setWeightSum(rows);
        LinearLayout line = null;
        for (int k = 0; k < views.size(); k++) {
            View view = views.get(k);
            int row = k / cols;
            int col = k % cols;
            if (col == 0) {
                line = new LinearLayout(ll.getContext());
                line.setWeightSum(cols);
                line.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
                if (row > 0) {
                    lineParams.topMargin = padding;
                }
                ll.addView(line, lineParams);
            }

            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            if (col > 0) {
                viewParams.leftMargin = padding;
            }
            line.addView(view, viewParams);
        }
    }

    public static void addItemViewsToLinearLayout(LinearLayout ll, List<View[]> lines, int padding) {
        if (lines == null || lines.isEmpty()) {
            return;
        }
        ll.setPadding(padding, padding, padding, padding);
        for (int k = 0; k < lines.size(); k++) {
            View[] views = lines.get(k);
            LinearLayout line = new LinearLayout(ll.getContext());
            line.setOrientation(LinearLayout.HORIZONTAL);
            line.setWeightSum(views.length);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (k > 0) {
                lineParams.topMargin = padding;
            }
            ll.addView(line, lineParams);

            for (int i = 0; i < views.length; i++) {
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                if (i > 0) {
                    viewParams.leftMargin = padding;
                }
                line.addView(views[i], viewParams);
            }
        }
    }


    public static void addItemViewsToLinearLayout(LinearLayout ll, List<View> itemViews, boolean topSep, boolean bottomSep, int hSepPadding) {
        if (itemViews == null || itemViews.isEmpty()) {
            return;
        }
        if (topSep) {
            View sep = new View(ll.getContext());
            sep.setBackgroundColor(Color.LTGRAY);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = hSepPadding;
            params.rightMargin = hSepPadding;
            ll.addView(sep, params);
        }
        for (int i = 0, size = itemViews.size(); i < size; i++) {
            {
                View view = itemViews.get(i);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if (params == null) {
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                ll.addView(view, params);
            }
            if (i < size - 1) {
                View sep = new View(ll.getContext());
                sep.setBackgroundColor(Color.LTGRAY);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                params.leftMargin = hSepPadding;
                params.rightMargin = hSepPadding;
                ll.addView(sep, params);
            }
        }

        if (bottomSep) {
            View sep = new View(ll.getContext());
            sep.setBackgroundColor(Color.LTGRAY);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = hSepPadding;
            params.rightMargin = hSepPadding;
            ll.addView(sep, params);
        }
    }

    static int strLen(CharSequence str) {
        return str == null ? 0 : str.length();
    }

    public static View createDispLine(Context context, DispLine line) {
        int layoutId = 0;
        if (TextUtils.isEmpty(line.name) || TextUtils.isEmpty(line.value)) {
            layoutId = R.layout.item_display_line1;
        } else if (line.name.length() + line.value.length() > 30) {
            layoutId = R.layout.item_display_line2;
        } else {
            layoutId = R.layout.item_display_line1;
        }

        View v = View.inflate(context, layoutId, null);
        TextView name = v.findViewById(R.id.tv_name);
        TextView value = v.findViewById(R.id.tv_value);
        name.setTextColor(line.nameColor);
        name.setText(line.name);
        value.setText(line.value);
        return v;
    }
}
