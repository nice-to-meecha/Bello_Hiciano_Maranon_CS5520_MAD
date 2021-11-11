package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.databinding
        .ActivityShowSelectedMessageBinding;


/**
 * Class which displays a single message selected by the user
 *
 * @author bello
 */
public class ShowSelectedMessageActivity extends AppCompatActivity {

    private ActivityShowSelectedMessageBinding binding;
    private String sender;
    private String recipient;
    private int stickerID;
    private String timeSent;


    /**
     * Sets up the ShowSelectedMessageActivity, primarily initializing
     * larger, more time-intensive objects
     * @param savedInstanceState - information related to an active state
     *                           of the ShowSelectedMessageActivity, prior
     *                           to orientation or state change
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowSelectedMessageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (savedInstanceState == null) {
            initializeCurrentMessage();

        } else {
            initializeShowSelectedMessageActivity(savedInstanceState);
        }
    }


    /**
     * Displays an image, according to the sender, recipient,
     * and sticker location presented by the Intent used to
     * open this Activity.
     */
    public void initializeCurrentMessage() {
        Intent currentMessageIntent = getIntent();

        if (currentMessageIntent.hasExtra("sender") &&
                currentMessageIntent.hasExtra("recipient")) {
            sender = currentMessageIntent.getStringExtra("sender");
            recipient = currentMessageIntent.getStringExtra("recipient");

            binding.biggerDisplaySenderRecipientTextView.setText(
                    String.format(getString(R.string.sender_recipient),
                            sender, recipient));
        }

        if (currentMessageIntent.hasExtra("stickerLocation")) {
            stickerID = getResources().getIdentifier(
                    currentMessageIntent.getStringExtra("stickerLocation"),
                    "drawable", getPackageName());
            binding.biggerDisplaySticker.setImageResource(stickerID);
        }

        if (currentMessageIntent.hasExtra("timeSent")) {
            timeSent = currentMessageIntent.getStringExtra("timeSent");
            binding.biggerDisplayTimeSent.setText(timeSent);
        }
    }


    /**
     * Saves the sender, recipient and sticker location of the
     * message being displayed, such that it can be retrieved,
     * following an orientation or state change.
     *
     * @param outState - the Bundle within which the sender,
     *                 recipient and sticker location of the
     *                 message are stored.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("sender", sender);
        outState.putString("recipient", recipient);
        outState.putInt("stickerID", stickerID);
        outState.putString("timeSent", timeSent);
    }


    /**
     * Displays the sticker message transmitted between users,
     * following an orientation or state change.
     *
     * @param savedInstanceState - a bundle retaining the sender,
     *                           recipient, and sticker location
     *                           of the transmitted message
     */
    public void initializeShowSelectedMessageActivity(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey("sender") &&
                savedInstanceState.containsKey("recipient")) {
            sender = savedInstanceState.getString("sender");
            recipient = savedInstanceState.getString("recipient");

            binding.biggerDisplaySenderRecipientTextView.setText(
                    String.format(getString(R.string.sender_recipient),
                            sender, recipient));
        }

        if (savedInstanceState.containsKey("stickerID")) {
            stickerID = savedInstanceState.getInt("stickerID");
            binding.biggerDisplaySticker.setImageResource(stickerID);
        }

        if (savedInstanceState.containsKey("timeSent")) {
            timeSent = savedInstanceState.getString("timeSent");
            binding.biggerDisplayTimeSent.setText(timeSent);
        }
    }
}