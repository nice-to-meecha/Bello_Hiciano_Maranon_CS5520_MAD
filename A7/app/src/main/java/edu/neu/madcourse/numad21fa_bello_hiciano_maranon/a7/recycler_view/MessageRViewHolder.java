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
public class MessageRViewHolder extends RecyclerView.ViewHolder {

    public TextView messageSenderUsername;
    public TextView messageRecipientUsername;
    public ImageView messageStickerSent;

    /**
     * Generates a Recycler View Holder, using the current view
     * of interest (an individual item within the RecyclerView)
     * and an ItemClickListener
     *
     * @param view - the current RecyclerView item being handled by
     *             the RecyclerView Adapter
     * @param listener - a listener, which provides for an action,
     *                 once the view is clicked
     */
    public MessageRViewHolder(@NonNull View view, final ItemClickListener listener) {
        super(view);
        this.messageSenderUsername = view.findViewById(R.id.messageSenderUsername);
        this.messageRecipientUsername = view.findViewById(R.id.messageRecipientUsername);
        this.messageStickerSent = view.findViewById(R.id.messageStickerSent);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    int position = getLayoutPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);

                    }
                }
            }
        });
    }
}
