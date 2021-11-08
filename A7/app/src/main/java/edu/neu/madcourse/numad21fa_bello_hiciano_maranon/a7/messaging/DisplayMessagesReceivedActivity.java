package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.databinding
        .ActivityDisplayMessagesReceivedBinding;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.ItemClickListener;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.SenderRecipientRViewAdapter;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.VerticalCardDecoration;
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
        VerticalCardDecoration verticalItemDecoration = new VerticalCardDecoration(20);
        recyclerView.addItemDecoration(verticalItemDecoration);

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
    }
}