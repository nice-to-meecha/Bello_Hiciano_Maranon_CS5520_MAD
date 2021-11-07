package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.MessageSent;

/**
 * Class establishing a RecyclerView adapter, which binds senders
 * and users to the sender_recipient_message card layout.
 *
 * @author bello
 */
public class SenderRecipientRViewAdapter extends RecyclerView.Adapter<MessageRViewHolder> {

    private final ArrayList<MessageSent> messageList;
    private ItemClickListener listener;
    private ViewGroup parent;


    /**
     * Generates the RecyclerView Adapter, using the list of messages
     * to be displayed
     *
     * @param messageList - the list of messages to be bound/displayed by the
     *                    RecyclerView adapter
     */
    public SenderRecipientRViewAdapter(ArrayList<MessageSent> messageList) {
        this.messageList = messageList;
    }


    /**
     * Sets the ItemClickListener, such that particular actions may
     * be taken, once an item is selected
     *
     * @param listener - a listener, which provides for an action,
     *                 once a view is clicked
     */
    public void setOnClickListener(ItemClickListener listener) {
        this.listener = listener;
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
    public MessageRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sender_recipient_message_card, parent, false);
        return new MessageRViewHolder(view, listener);
    }


    /**
     * Binds the data of the Message at the current position within
     * the RecyclerView adapter's list with the elements of the
     * item to be displayed within the RecyclerView
     *
     * @param holder - the RecyclerView holder, which is directly associated
     *               with the individual cards to be displayed in the RecyclerView
     * @param position - the position of the current Message to be bound, within
     *                 the RecyclerView adapter's list
     */
    @Override
    public void onBindViewHolder(@NonNull MessageRViewHolder holder, int position) {
        MessageSent currentMessage = this.messageList.get(position);

        holder.messageSenderUsername.setText(currentMessage.getSender());
        holder.messageRecipientUsername.setText(currentMessage.getRecipient());
        holder.messageStickerSent.setImageResource(currentMessage.getStickerID());
    }


    /**
     * Returns the number of messages to be displayed, in total,
     * by the RecyclerView adapter
     *
     * @return the number of messages to be displayed, in total,
     * by the RecyclerView adapter
     */
    @Override
    public int getItemCount() {
        return this.messageList.size();
    }
}
