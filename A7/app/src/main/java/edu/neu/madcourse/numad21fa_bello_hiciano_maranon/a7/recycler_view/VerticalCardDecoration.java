package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class VerticalCardDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpacing;

    public VerticalCardDecoration(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
    }

    @Override
    public void getItemOffsets(Rect rect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int lastItem = 1;
        if (parent.getChildAdapterPosition(view) !=
                parent.getAdapter().getItemCount() - lastItem) {
            rect.bottom = this.verticalSpacing;
        }
    }
}
