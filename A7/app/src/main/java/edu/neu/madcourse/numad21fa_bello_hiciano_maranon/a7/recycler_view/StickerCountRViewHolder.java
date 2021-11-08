package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;

/**
 * Class which defines the activity of the View Holder,
 * which identifies individual items that will be affected
 * within the RecyclerView
 *
 * @author bello
 */
public class StickerCountRViewHolder extends RecyclerView.ViewHolder {

    public ImageView stickerCountImageView;
    public TextView stickerCount;

    /**
     * Generates a Recycler View Holder, using the current view
     * of interest (an individual item within the RecyclerView)
     * and an ItemClickListener
     *
     * @param view - the current RecyclerView item being handled by
     *             the RecyclerView Adapter
     */
    public StickerCountRViewHolder(@NonNull View view) {
        super(view);

        this.stickerCountImageView = view.findViewById(R.id.stickerCountImageView);
        this.stickerCount = view.findViewById(R.id.stickerCount);
    }
}
