package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Class representing messages between devices,
 * in the Stick It To 'Em app
 *
 * @author bello
 */
public class MessageSent implements Parcelable {

    public String sender;
    public String recipient;
    public String stickerLocation;
    public int stickerID;
    public String timeSent;


    /**
     * Utilized to create a Parcelable object from a MessageSent object
     */
    public static final Parcelable.Creator<MessageSent> CREATOR =
            new Parcelable.Creator<MessageSent>() {
                public MessageSent createFromParcel(Parcel in) {
                    return new MessageSent(in);
                }

                public MessageSent[] newArray(int size) {
                    return new MessageSent[size];
                }
            };


    /**
     * Generates a MessageSent object, from a Parcel object,
     * to which the MessageSent object's properties have been written
     *
     * @param in - the Parcel object retaining saved properties
     *           of a MessageSent object
     */
    public MessageSent(Parcel in) {
        this.sender = in.readString();
        this.recipient = in.readString();
        this.stickerLocation = in.readString();
        this.stickerID = in.readInt();
        this.timeSent = in.readString();
    }


    /**
     * Generates a MessageSent object, using the usernames of the sender
     * and recipient, as well as the alias of the sticker sent, and the
     * time at which it was sent.
     *
     * @param sender - the username of the user who sent the message
     * @param recipient - the username of the recipient of the message
     * @param stickerLocation - the file name of the sticker sent in the message
     * @param stickerID - the resource ID of the sticker sent in the message
     * @param timeSent - the time at which the message was sent
     */
    public MessageSent(String sender, String recipient, String stickerLocation,
                       int stickerID, String timeSent) {
        this.sender = sender;
        this.recipient = recipient;
        this.stickerLocation = stickerLocation;
        this.stickerID = stickerID;
        this.timeSent = timeSent;
    }

    /**
     * Returns the username of the user who sent the message
     *
     * @return the username of the user who sent the message
     */
    public String getSender() {
        return this.sender;
    }


    /**
     * Returns the username of the recipient of the message
     *
     * @return the username of the recipient of the message
     */
    public String getRecipient() {
        return this.recipient;
    }


    /**
     * Returns the file name of the sticker sent in the message
     *
     * @return the file name of the sticker sent in the message
     */
    public String getStickerLocation() {
        return this.stickerLocation;
    }


    /**
     * Returns the resource ID of the sticker sent in the message
     *
     * @return the resource ID of the sticker sent in the message
     */
    public int getStickerID() {
        return this.stickerID;
    }

    /**
     * Returns the time at which the message was sent
     *
     * @return the time at which the message was sent
     */
    public String getTimeSent() {
        return this.timeSent;
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
     * Converts a MessageSent into a Parcel, using its
     * sender, recipient, stickerAlias, stickerID, and
     * timeSent properties
     *
     * @param parcel - the Parcel to be created from a MessageSent
     * @param i - not defined; flags of some sort(?)
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.sender);
        parcel.writeString(this.recipient);
        parcel.writeString(this.stickerLocation);
        parcel.writeInt(this.stickerID);
        parcel.writeString(this.timeSent);
    }


    /**
     * Provides a String representation of a MessageSent object
     *
     * @return a String representation of a MessageSent object
     */
    @NonNull
    @Override
    public String toString() {
        return "Message {" +
                "sender: " + this.sender +
                ", recipient: " + this.recipient +
                ", stickerLocation: " + this.stickerLocation +
                ", stickerID: " + this.stickerID +
                ", timeSent: " + timeSent +
                '}';
    }
}
