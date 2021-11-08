package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.Sticker;


/**
 * Class establishing a RecyclerView adapter, which binds stickers
 * and their usage (count) to sticker_history_card_layout.
 *
 * @author bello
 */
public class StickerCountRViewAdapter extends RecyclerView.Adapter<StickerCountRViewHolder> {

    private final ArrayList<Sticker> stickerList;
    private ViewGroup parent;


    /**
     * Generates the RecyclerView Adapter, using the list of stickers
     * to be displayed
     *
     * @param stickerList - the list of stickers to be bound/displayed by the
     *                    RecyclerView adapter
     */
    public StickerCountRViewAdapter(ArrayList<Sticker> stickerList) {
        this.stickerList = stickerList;
    }


    /**
     * Produces the RecyclerView holder, utilizing the parent of the
     * views to be displayed(?)
     *
     * @param parent - the parent of the views to be displayed(??)
     * @param viewType - not sure; not used
     *
     * @return a RecyclerView holder, utilizing the parent of the
     * views to be displayed(?)
     */
    @NonNull
    @Override
    public StickerCountRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sticker_history_card_layout, parent, false);
        return new StickerCountRViewHolder(view);
    }


    /**
     * Binds the data of the Sticker at the current position within
     * the RecyclerView adapter's list with the elements of the
     * item to be displayed within the RecyclerView
     *
     * @param holder - the RecyclerView holder, which is directly associated
     *               with the individual cards to be displayed in the RecyclerView
     * @param position - the position of the current Sticker to be bound, within
     *                 the RecyclerView adapter's list
     */
    @Override
    public void onBindViewHolder(@NonNull StickerCountRViewHolder holder, int position) {
        Sticker currentSticker = this.stickerList.get(position);

        int id = this.parent.getContext().getResources()
                .getIdentifier(currentSticker.getLocation(), "drawable",
                        this.parent.getContext().getPackageName());
        holder.stickerCountImageView.setImageResource(id);
        holder.stickerCount.setText(String.format(
                this.parent.getContext().getResources().getString(R.string.individual_sticker_count),
                currentSticker.getCount()));
    }


    /**
     * Returns the number of stickers to be displayed, in total,
     * by the RecyclerView adapter
     *
     * @return the number of stickers to be displayed, in total,
     * by the RecyclerView adapter
     */
    @Override
    public int getItemCount() {
        return this.stickerList.size();
    }
}
