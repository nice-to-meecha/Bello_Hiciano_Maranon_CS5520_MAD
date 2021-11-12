package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.databinding
        .ActivityDisplayMessagesSentBinding;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.ItemClickListener;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.SenderRecipientRViewAdapter;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in.User;


/**
 * Class which creates an Activity that displays all messages sent
 * by the current user (to other users).
 *
 * @author bello
 */
public class DisplayMessagesSentActivity extends AppCompatActivity {
    private final String TAG = "DisplayMessagesSentActivity";
    private final int DISPLAY_MESSAGES_SENT_ACTIVITY_CODE = 103;

    private ActivityDisplayMessagesSentBinding binding;
    private ArrayList<MessageSent> messageList = new ArrayList<>();
    private FirebaseDatabase database;
    private User currUser;
    private RecyclerView recyclerView;
    private SenderRecipientRViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;


    /**
     * Sets up the DisplayMessagesSentActivity, primarily initializing
     * larger, more time-intensive objects
     * @param savedInstanceState - information related to an active state
     *                           of the DisplayMessagesSentActivity, prior to orientation
     *                           or state change
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDisplayMessagesSentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.sent_messages);

        database = FirebaseDatabase.getInstance();

        if (savedInstanceState == null) {
            initializeMessageList();

        } else {
            initializeDisplayMessagesSent(savedInstanceState);
            createRecyclerView();
        }
    }


    /**
     * Generates the messages to be displayed, when the
     * DisplayMessagesSentActivity is first opened
     */
    public void initializeMessageList() {
        Intent currUserIntent = getIntent();

        currUser = new User(currUserIntent.getStringExtra("username"),
                currUserIntent.getStringExtra("loginTime"));

        Log.v(TAG, currUser.toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                retrieveMessagesFromDatabase(currUser);
            }
        }).start();
    }


    /**
     * Gets all messages sent by the provided user from the database
     * in which they are stored
     *
     * @param user - the user for which sent messages will be retrieved
     */
    private void retrieveMessagesFromDatabase(User user) {
        database.getReference("SentMessages").child(user.getUsername())
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && task.getResult().hasChildren()) {
                        for (DataSnapshot child: task.getResult().getChildren()) {
                            messageList.add(new MessageSent(child.child("sender").getValue().toString(),
                                    child.child("recipient").getValue().toString(),
                                    child.child("stickerLocation").getValue().toString(),
                                    Integer.parseInt(child.child("stickerID").getValue().toString()),
                                    child.child("timeSent").getValue().toString()));
                        }
                        CompareMessage messageComparator = new CompareMessage();
                        messageList.sort(messageComparator);
                        Log.v(TAG, messageList.toString());

                    } else {
                        Log.v(TAG, task.getResult().toString());

                    }
                    createRecyclerView();

                } else {
                    Log.v(TAG, "Something went wrong. Check your internet connection");
                }
            }
        });
    }


    /**
     * Saves the current state of the DisplaySentMessagesActivity,
     * should it undergo an orientation or state change.
     *
     * @param outState - a Bundle containing sent messages (of the
     *                 current user), from which the current state
     *                 of the DisplayMessagesSentActivity can be updated
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("messageList", messageList);
    }



    /**
     * Restores the state of the DisplaySentMessagesActivity, prior to an
     * orientation or state change, such that sent messages will not be lost.
     *
     * @param savedInstanceState - a Bundle containing sent messages (of the
     *                           current user), from which the current state
     *                           of the DisplayMessagesSentActivity can be updated
     */
    public void initializeDisplayMessagesSent(Bundle savedInstanceState) {

        if (savedInstanceState.containsKey("messageList")) {
            messageList = savedInstanceState.getParcelableArrayList("messageList");
        }
    }


    /**
     * Generates the RecyclerView to display the messages sent
     * by the current user.
     */
    public void createRecyclerView() {
        Log.v(TAG, "Creating recycler view");
        layoutManager = new LinearLayoutManager(this);
        recyclerView = binding.sentMessagesRecyclerView;
        recyclerView.setHasFixedSize(true);

        recyclerViewAdapter = new SenderRecipientRViewAdapter(messageList);
        ItemClickListener listener = new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MessageSent selectedMessage = messageList.get(position);
                Intent openSelectedMessage = new Intent(getApplicationContext(),
                        ShowSelectedMessageActivity.class);
                openSelectedMessage.putExtra("sender",
                        selectedMessage.getSender());
                openSelectedMessage.putExtra("recipient",
                        selectedMessage.getRecipient());
                openSelectedMessage.putExtra("stickerLocation",
                        selectedMessage.getStickerLocation());
                openSelectedMessage.putExtra("timeSent",
                        selectedMessage.getTimeSent());

                startActivity(openSelectedMessage);
            }
        };

        recyclerViewAdapter.setOnClickListener(listener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}