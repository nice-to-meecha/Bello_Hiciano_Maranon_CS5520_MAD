package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;


/**
 * Class representing the stickers that users are able to send
 * to others.
 *
 * The Parcelable elements presented below were implemented,
 * based on the code found at the following link:
 * https://www.vogella.com/tutorials/AndroidParcelable/article.html
 *
 * @author bello
 */
public class Sticker implements Parcelable {
    private int INITIALIZE_COUNT = 0;
    private int INCREMENT_COUNT = 1;

    private String alias;
    private String location;
    private int count;


    /**
     * Utilized to create a Parcelable object from a Sticker object
     */
    public static final Parcelable.Creator<Sticker> CREATOR =
            new Parcelable.Creator<Sticker>() {
        public Sticker createFromParcel(Parcel in) {
            return new Sticker(in);
        }

        public Sticker[] newArray(int size) {
            return new Sticker[size];
        }
    };


    /**
     * Generates a Sticker object, from a Parcel object,
     * to which a Sticker's properties have been written
     *
     * @param in - the Parcel object retaining saved properties
     *           of a Sticker
     */
    public Sticker(Parcel in) {
        this.alias = in.readString();
        this.location = in.readString();
        this.count = in.readInt();
    }


    /**
     * Generates a Sticker object, using its moniker (alias) and
     * its location within the drawable folder
     *  @param alias - an arbitrary name given to a particular sticker
     * @param location - the location of the sticker within the drawable folder
     */
    public Sticker(String alias, String location) {
        this.alias = alias;
        this.location = location;
        this.count = INITIALIZE_COUNT;
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


    /**
     * Returns the recorded count of the number of times a sticker
     * has been used
     *
     * @return the recorded count of the number of times a sticker
     * has been used
     */
    public int getCount() {
        return this.count;
    }


    /**
     * Increases the recorded count of the use of a particular sticker
     * by 1
     */
    public void incrementCount() {
        this.count += INCREMENT_COUNT;
    }


    /**
     * Sets the recorded count of the number of times a sticker
     * has been used
     *
     * @param count - the number of times a sticker has been used
     */
    public void setCount(int count) {
        this.count = count;
    }


    /**
     * Unknown function; this is only here, due to the requirement
     * that this be implemented with a class that extends Parcelable
     *
     * @return unknown
     */
    @Override
    public int describeContents() {
        return 0;
    }


    /**
     * Converts a Sticker into a Parcel, using its
     * alias and location properties
     *
     * @param parcel - the Parcel to be created from a Sticker
     * @param i - not defined; flags of some sort(?)
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.alias);
        parcel.writeString(this.location);
        parcel.writeInt(this.count);
    }

    /**
     * Provides a String representation of a Sticker object
     *
     * @return a String representation of a Sticker object
     */
    @NonNull
    @Override
    public String toString() {
        return "Alias: " + this.alias +
                ", Location: " + this.location +
                ", Count: " + this.count;
    }


    /**
     * Provides a basis for which Sticker objects may be compared,
     * in terms of equality
     *
     * @param o - the object to be compared with a Sticker object
     *
     * @return boolean indicating the equality of a Sticker object
     * with the provided object (o)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sticker sticker = (Sticker) o;
        return INITIALIZE_COUNT == sticker.INITIALIZE_COUNT &&
                INCREMENT_COUNT == sticker.INCREMENT_COUNT &&
                count == sticker.count &&
                alias.equals(sticker.alias) &&
                location.equals(sticker.location);
    }


    /**
     * Produces a hash code for a Sticker object
     *
     * @return a hash code for a Sticker object
     */
    @Override
    public int hashCode() {
        return Objects.hash(INITIALIZE_COUNT, INCREMENT_COUNT, alias, location, count);
    }
}
