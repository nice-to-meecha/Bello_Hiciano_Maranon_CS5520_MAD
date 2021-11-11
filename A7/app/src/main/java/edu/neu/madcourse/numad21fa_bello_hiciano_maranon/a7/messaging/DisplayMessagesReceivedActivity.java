package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.databinding
        .ActivityDisplayMessagesReceivedBinding;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.ItemClickListener;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.SenderRecipientRViewAdapter;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in.User;

public class DisplayMessagesReceivedActivity extends AppCompatActivity {
    private final String TAG = "DisplayMessagesReceivedActivity";
    private final int DISPLAY_MESSAGES_RECEIVED_ACTIVITY_CODE = 104;

    private ActivityDisplayMessagesReceivedBinding binding;
    private ArrayList<MessageSent> messageList = new ArrayList<>();
    private FirebaseDatabase database;
    private User currUser;
    private RecyclerView recyclerView;
    private SenderRecipientRViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ChildEventListener messageListener;


    /**
     * Sets up the DisplayMessagesReceivedActivity, primarily initializing
     * larger, more time-intensive objects
     * @param savedInstanceState - information related to an active state
     *                           of the DisplayMessagesReceivedActivity,
     *                           prior to orientation or state change
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDisplayMessagesReceivedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();

        if (savedInstanceState == null) {
            initializeMessageList();

        } else {
            initializeDisplayMessagesReceived(savedInstanceState);
            createRecyclerView();
        }
    }


    /**
     * Generates the messages to be displayed, when the
     * DisplayMessagesReceivedActivity is first opened
     */
    public void initializeMessageList() {
        Intent currUserIntent = getIntent();

        if (currUserIntent.hasExtra("recipientUsername")) {
            currUser = new User(currUserIntent.getStringExtra("recipientUsername"),
                    currUserIntent.getStringExtra("loginTime"));

        } else {
            currUser = new User(currUserIntent.getStringExtra("username"),
                    currUserIntent.getStringExtra("loginTime"));
        }

        Log.v(TAG, currUser.toString());

        retrieveMessagesFromDatabase(currUser);

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                retrieveMessagesFromDatabase(currUser);
            }
        }).start();

         */
    }


    /**
     * Gets all messages received by the provided user from the database
     * in which they are stored
     *
     * @param user - the user for which received messages will be retrieved
     */
    private void retrieveMessagesFromDatabase(User user) {
        database.getReference("ReceivedMessages").child(user.getUsername())
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
     * Adds a listener to the current user's received messages,
     * such that the RecyclerView retaining the received messages
     * will be updated accordingly.
     *
     * @param user - the current user for whom received messages
     *             are displayed
     */
    public void newMessageListener(User user) {
        messageListener = database.getReference("ReceivedMessages").child(user.getUsername())
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                if (snapshot.getValue() == null) {
                    Log.v(TAG, "Something went wrong when adding a message");
                    return;
                }

                int prevMessageCount = 0;
                Log.v(TAG, "previous child: " + previousChildName);

                /* Getting number of previous children, such that won't add duplicates
                 * (since onChildAdded() called for all existing children AND those
                 * added afterward
                 */
                if (previousChildName != null) {
                    String[] splitPrevMessageBySpaces = previousChildName.split("\\s");
                    prevMessageCount = Integer.parseInt(
                            splitPrevMessageBySpaces[splitPrevMessageBySpaces.length - 1]);
                    Log.v(TAG, "Prev Message Count: " + prevMessageCount);
                }

                String[] splitCurrMessageBySpaces = snapshot.getKey().split("\\s");
                int currMessageCount = Integer.parseInt(
                        splitCurrMessageBySpaces[splitCurrMessageBySpaces.length - 1]);

                if (prevMessageCount == messageList.size() &&
                        currMessageCount == messageList.size() + 1) {
                    Log.v(TAG, "Adding to message list");
                    messageList.add(0, new MessageSent(
                            snapshot.child("sender").getValue().toString(),
                            snapshot.child("recipient").getValue().toString(),
                            snapshot.child("stickerLocation").getValue().toString(),
                            Integer.parseInt(snapshot.child("stickerID").getValue().toString()),
                            snapshot.child("timeSent").getValue().toString()));

                    recyclerViewAdapter.notifyItemInserted(0);
                    recyclerView.getLayoutManager().scrollToPosition(0);

                    /*
                    synchronized (recyclerViewAdapter) {
                        recyclerViewAdapter.notifyItemInserted(0);
                    }

                     */
                }
                Log.v(TAG, "new child: " + snapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    /**
     * Saves the current state of the DisplayReceivedMessagesActivity,
     * should it undergo an orientation or state change.
     *
     * @param outState - a Bundle containing received messages (of the
     *                 current user), from which the current state
     *                 of the DisplayMessagesReceivedActivity can be updated
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("messageList", messageList);
        outState.putString("username", currUser.getUsername());
        outState.putString("loginTime", currUser.getLoginTime());
    }



    /**
     * Restores the state of the DisplayReceivedMessagesActivity, prior to an
     * orientation or state change, such that received messages will not be lost.
     *
     * @param savedInstanceState - a Bundle containing received messages (of the
     *                           current user), from which the current state
     *                           of the DisplayMessagesReceivedActivity can be updated
     */
    public void initializeDisplayMessagesReceived(Bundle savedInstanceState) {

        if (savedInstanceState.containsKey("messageList")) {
            messageList = savedInstanceState.getParcelableArrayList("messageList");
        }

        if (savedInstanceState.containsKey("username") &&
                savedInstanceState.containsKey("loginTime")) {
            currUser = new User(savedInstanceState.getString("username"),
                    savedInstanceState.getString("loginTime"));
        }
    }


    /**
     * Generates the RecyclerView to display the messages received
     * by the current user.
     */
    public void createRecyclerView() {
        Log.v(TAG, "Creating recycler view");
        layoutManager = new LinearLayoutManager(this);
        recyclerView = binding.receivedMessagesRecyclerView;
        recyclerView.setHasFixedSize(true);

        recyclerViewAdapter = new SenderRecipientRViewAdapter(messageList);
        ItemClickListener listener = new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // TODO - Open intent to show bigger version of message
                Toast.makeText(DisplayMessagesReceivedActivity.this,
                        messageList.get(position).getStickerLocation(),
                        Toast.LENGTH_LONG).show();
            }
        };

        recyclerViewAdapter.setOnClickListener(listener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        newMessageListener(currUser);
    }


    /**
     * Removes the new message child listener, such that multiple are
     * not created, if resumed.
     */
    public void onPause() {
        super.onPause();
        database.getReference("ReceivedMessages").child(currUser.getUsername())
                .removeEventListener(messageListener);
    }
}