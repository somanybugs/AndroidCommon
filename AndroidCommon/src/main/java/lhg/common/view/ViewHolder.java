package lhg.common.view;

import android.util.SparseArray;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 刘浩歌 okz@outlook.com on 2015/2/26.
 */
public class ViewHolder {
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

    public static TextView textView(View view, int id) {
        return get(view, id);
    }
    public static ImageView imageView(View view, int id) {
        return get(view, id);
    }

    public static CompoundButton checkbox(View view, int id) {
        return get(view, id);
    }

}
