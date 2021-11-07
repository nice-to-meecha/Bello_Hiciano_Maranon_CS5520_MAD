package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Class providing additional spacing between individual
 * cards of a RecyclerView
 *
 * @author bello
 */
public class VerticalCardDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpacing;

    /**
     * Generates a VerticalCardDecoration object, using the
     * total amount of spacing to be placed beneath each card
     * in the RecyclerView
     *
     * @param verticalSpacing - the amount of spacing to be
     *                        used between each card in the
     *                        RecyclerView
     */
    public VerticalCardDecoration(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
    }


    /**
     * Produces the amount by which each card will be offset,
     * within the provided RecyclerView
     *
     * @param rect - the rectangular section within which RecyclerView
     *             elements are positioned
     * @param view - a specific item card (whose positioning is to be adjusted)
     * @param parent - the RecyclerView retaining the cards to be positioned
     * @param state - the current state of the RecyclerView(?)
     */
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
