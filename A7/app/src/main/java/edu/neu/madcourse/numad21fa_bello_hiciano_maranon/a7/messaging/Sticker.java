package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.widget.ImageView;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;

/**
 * Class representing the stickers that users are able to send
 * to others
 *
 * @author bello
 */
public class Sticker {

    private String alias;
    private String location;

    /**
     * Generates a Sticker object, using its moniker (alias) and
     * its location within the drawable folder
     *  @param alias - an arbitrary name given to a particular sticker
     * @param location - the location of the sticker within the drawable folder
     */
    public Sticker(String alias, String location) {
        this.alias = alias;
        this.location = location;
    }


    /**
     * Returns the arbitrary name given to a particular sticker
     *
     * @return the arbitrary name given to a particular sticker
     */
    public String getAlias() {
        return this.alias;
    }


    /**
     * Returns the location of the sticker within the drawable folder
     *
     * @return the location of the sticker within the drawable folder
     */
    public String getLocation() {
        return this.location;
    }
}
