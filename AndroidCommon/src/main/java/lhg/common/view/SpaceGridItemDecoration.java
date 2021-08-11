package lhg.common.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2021/4/12 15:04
 * Note:
 */
public class SpaceGridItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpaceGridItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = space;
        outRect.bottom = space;
        int index = parent.getChildLayoutPosition(view);
        int span = ((GridLayoutManager)parent.getLayoutManager()).getSpanCount();
        int row = index / span;
        int col = index % span;
        if (row == 0) {
            outRect.top = space;
        }
        if (col == span - 1) {
            outRect.right = space;
        }
    }

}
