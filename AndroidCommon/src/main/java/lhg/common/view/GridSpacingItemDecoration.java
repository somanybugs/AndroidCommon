package lhg.common.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private boolean includeEdge;
    private int horizontalSpacing;
    private int verticalSpacing;

    public GridSpacingItemDecoration(boolean includeEdge, int horizontalSpacing, int verticalSpacing) {
        this.includeEdge = includeEdge;
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();

        int spanCount = gridLayoutManager.getSpanCount();
        int position = parent.getChildAdapterPosition(view);
        int spanSize = gridLayoutManager.getSpanSizeLookup().getSpanSize(position);
        int column = gridLayoutManager.getSpanSizeLookup().getSpanIndex(position, spanCount);
        int totalChildCount = parent.getAdapter().getItemCount();
        boolean isLastRow = spanSize == 1 ?
                position + spanCount - column > totalChildCount - 1 :
                position - column / spanSize > totalChildCount - 1;
        boolean isFirstRow = gridLayoutManager.getSpanSizeLookup().getSpanGroupIndex(position, spanCount) == 0;

        if (includeEdge) {
            outRect.left = horizontalSpacing - column * horizontalSpacing / spanCount;
            outRect.right = (column + spanSize) * horizontalSpacing / spanCount;
            outRect.top = verticalSpacing;
            outRect.bottom = isLastRow ? verticalSpacing : 0;
        } else {
            outRect.left = column * horizontalSpacing / spanCount;
            outRect.right = horizontalSpacing - (column + spanSize) * horizontalSpacing / spanCount;
            outRect.top = isFirstRow ? 0 : verticalSpacing;
        }
    }

}