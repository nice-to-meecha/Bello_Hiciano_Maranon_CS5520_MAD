package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 * A class utilized to compare dates of MessageSent objects,
 * such that they can be sorted in reverse chronological order,
 * according to send date/time.
 *
 * @author bello
 */
public class CompareMessage implements Comparator<MessageSent> {

    /**
     * Creates an empty CompareMessage object
     */
    public CompareMessage(){}

    /**
     * Compares to MessageSent objects with one another, returning a
     * positive value if the date of the first message is before that of
     * the second message; it returns a negative value if vice versa.
     * If both are equivalent, 0 is returned.
     *
     * @param firstMessage - the first message -- the date of which will be
     *                     compared with the second
     * @param secondMessage - the second message -- the date of which will be
     *                      compared with the first
     *
     * @return a positive value if the date of the first message is before
     * that of the second message. Returns a negative value if vice versa.
     * If both message dates are equivalent, 0 is returned.
     */
    @Override
    public int compare(MessageSent firstMessage, MessageSent secondMessage) {
        int firstBefore = 1, equivalent = 0, firstAfter = -1;
        LocalDateTime firstDateTime = LocalDateTime.parse(firstMessage.getTimeSent(),
                DateTimeFormatter.ofPattern("MM/dd/uuuu H:m:s:S"));
        LocalDateTime secondDateTime = LocalDateTime.parse(secondMessage.getTimeSent(),
                DateTimeFormatter.ofPattern("MM/dd/uuuu H:m:s:S"));

        if (firstDateTime.isBefore(secondDateTime)) {
            return firstBefore;

        } else if (firstDateTime.isAfter(secondDateTime)) {
            return firstAfter;

        } else {
            return equivalent;

        }
    }
}
