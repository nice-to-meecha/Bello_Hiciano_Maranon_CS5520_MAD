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
        .ActivityDisplayMessagesSentBinding;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.ItemClickListener;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.SenderRecipientRViewAdapter;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.VerticalCardDecoration;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in.User;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDisplayMessagesSentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance();

        if (savedInstanceState == null) {
            initializeMessageList();

        } else {
            initializeDisplayMessagesSent();
        }
    }


    /**
     * When the Activity is first opened, the messages
     * to be displayed are generated here
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
                                    child.child("stickerAlias").getValue().toString(),
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


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


    }


    public void initializeDisplayMessagesSent() {


    }


    public void createRecyclerView() {
        Log.v(TAG, "Creating recycler view");
        layoutManager = new LinearLayoutManager(this);
        recyclerView = binding.sentMessagesRecyclerView;
        recyclerView.setHasFixedSize(true);
        VerticalCardDecoration verticalItemDecoration = new VerticalCardDecoration(20);
        recyclerView.addItemDecoration(verticalItemDecoration);

        recyclerViewAdapter = new SenderRecipientRViewAdapter(messageList);
        ItemClickListener listener = new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // TODO - Open intent to show bigger version of message
                Toast.makeText(DisplayMessagesSentActivity.this,
                        messageList.get(position).getStickerAlias(),
                        Toast.LENGTH_LONG).show();
            }
        };

        recyclerViewAdapter.setOnClickListener(listener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}