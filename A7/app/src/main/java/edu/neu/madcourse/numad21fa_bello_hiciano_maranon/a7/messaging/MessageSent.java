package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import androidx.annotation.NonNull;

/**
 * Class representing messages between devices,
 * in the Stick It To 'Em app
 *
 * @author bello
 */
public class MessageSent {

    public String sender;
    public String recipient;
    public String stickerAlias;
    public int stickerID;
    public String timeSent;


    /**
     * Generates a MessageSent object, using the usernames of the sender
     * and recipient, as well as the alias of the sticker sent, and the
     * time at which it was sent.
     *
     * @param sender - the username of the user who sent the message
     * @param recipient - the username of the recipient of the message
     * @param stickerAlias - the alias of the sticker sent in the message
     * @param stickerID - the resource ID of the sticker sent in the message
     * @param timeSent - the time at which the message was sent
     */
    public MessageSent(String sender, String recipient, String stickerAlias,
                       int stickerID, String timeSent) {
        this.sender = sender;
        this.recipient = recipient;
        this.stickerAlias = stickerAlias;
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
     * Returns the alias of the sticker sent in the message
     *
     * @return the alias of the sticker sent in the message
     */
    public String getStickerAlias() {
        return this.stickerAlias;
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
                ", stickerAlias: " + this.stickerAlias +
                ", stickerID: " + this.stickerID +
                ", timeSent: " + timeSent +
                '}';
    }
}
