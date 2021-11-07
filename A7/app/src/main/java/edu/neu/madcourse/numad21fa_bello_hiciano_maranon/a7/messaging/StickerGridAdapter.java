package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;

/**
 * Provides an adapter, such that the GridView used to showcase
 * and select stickers is maintained appropriately.
 *
 * This code was largely based on the GridView tutorial provided
 * at this site (https://abhiandroid.com/ui/gridview)
 *
 *
 * @author bello
 */
public class StickerGridAdapter extends BaseAdapter {
    private String TAG = "StickerGridAdapter";

    private Context context;
    private ArrayList<Sticker> stickerList;
    private LayoutInflater layoutInflater;
    private ImageView gridImage;


    /**
     * Generates a StickerGridAdapter object, using the context of the Activity,
     * within which the grid will display, and the list of stickers to be shown
     * in the grid.
     *
     * @param context - the context of the Activity, in which the grid will be displayed
     * @param stickerList - the list of Stickers, from which users will select
     */
    public StickerGridAdapter(Context context, ArrayList<Sticker> stickerList) {
        this.context = context;
        this.stickerList = stickerList;
    }


    /**
     * Returns the number of stickers to be shown in the grid
     * @return the number of stickers to be shown in the grid
     */
    @Override
    public int getCount() {
        return this.stickerList.size();
    }

    /**
     * Returns the Sticker at the specified index of the Sticker list
     *
     * @param i - the index at which the desired Sticker resides
     *
     * @return the Sticker at the specified index of the Sticker list
     */
    @Override
    public Object getItem(int i) {
        return this.stickerList.get(i);
    }

    /**
     * Returns 0, since Stickers do not have specified IDs
     *
     * @param i - the index of the Sticker, of which ID is desired
     *
     * @return 0, since Stickers do not have specified IDs
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Displays the grid items, depending on the indices of the stickers
     * currently within view. Recycles views, to save time and power.
     *
     * @param i - the index of the desired Sticker to be displayed
     * @param view - the view which will be displayed as a grid item
     * @param viewGroup - the parent of the view to be displayed
     *
     * @return the view currently being displayed as a grid item
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.v(TAG, "Getting view: " + i);
        Log.v(TAG, "List length: " + this.getCount());
        layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            view = layoutInflater.inflate(R.layout.sticker_layout, viewGroup, false);
        }
        Sticker currSticker = stickerList.get(i);
        int id = context.getResources().getIdentifier(currSticker.getLocation(), "drawable",
                context.getPackageName());

        gridImage = view.findViewById(R.id.stickerGridLayout);
        gridImage.setImageResource(id);

        return view;
    }
}
