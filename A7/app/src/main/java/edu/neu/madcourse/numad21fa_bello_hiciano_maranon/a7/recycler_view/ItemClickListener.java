package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view;

/**
 * An interface, providing for a click listener,
 * utilized by the MessageRViewHolder class
 *
 * @author bello
 */
public interface ItemClickListener {

    /**
     * Defines the action to be taken, once an item at a
     * specified position within a list is clicked
     *
     * @param position - the location of the desired item
     *                 within a list
     */
    void onItemClick(int position);
}