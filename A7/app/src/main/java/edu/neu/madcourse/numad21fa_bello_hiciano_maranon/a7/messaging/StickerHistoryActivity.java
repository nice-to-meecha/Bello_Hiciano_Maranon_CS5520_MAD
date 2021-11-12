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
import java.util.HashMap;
import java.util.Objects;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.databinding
        .ActivityStickerHistoryBinding;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.StickerCountRViewAdapter;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in.User;


/**
 * Class providing for the Sticker History page, allowing
 * users to visualize their current sticker usage.
 *
 * @author bello
 */
public class StickerHistoryActivity extends AppCompatActivity {
    private String TAG = "StickerHistoryActivity";

    private FirebaseDatabase database;
    private ActivityStickerHistoryBinding binding;
    private ArrayList<Sticker> stickerList = new ArrayList<>();
    private HashMap<String, Sticker> hashMap = new HashMap<>();
    private User currUser;
    private int totalStickerCount = 0;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private StickerCountRViewAdapter recyclerViewAdapter;


    /**
     * Sets up the StickerHistoryActivity, primarily initializing
     * larger, more time-intensive objects
     * @param savedInstanceState - information related to an active state
     *                           of the StickerHistoryActivity, prior to
     *                           orientation or state change
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStickerHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.sticker_history);

        database = FirebaseDatabase.getInstance();

        if (savedInstanceState == null) {
            initializeCurrentUser();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    initializeStickerMap();
                }
            }).start();

        } else {
            initializeStickerHistoryActivity(savedInstanceState);
            createRecyclerView();
        }
    }


    /**
     * Determines the current user, via Intent
     */
    public void initializeCurrentUser() {
        Log.v(TAG, "Getting current user");
        Intent getCurrUser = getIntent();
        currUser = new User(getCurrUser.getStringExtra("username"),
                getCurrUser.getStringExtra("loginTime"));
    }


    /**
     * Creates a map, storing all available Sticker aliases and
     * their file names. Will be used to count the number of each
     * individual sticker utilized by the current user.
     */
    public void initializeStickerMap() {
        Log.v(TAG, "making sticker map");
        database.getReference("Stickers").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (DataSnapshot child: task.getResult().getChildren()) {
                        hashMap.put(child.getValue().toString(),
                                new Sticker(child.getKey(), child.getValue().toString()));
                    }

                    countStickerUsage(currUser);

                } else {
                    Log.v(TAG, "Something went wrong. Check your internet connection");
                }
            }
        });
    }


    /**
     * Counts the number of times each individual sticker has been
     * sent by the current user.
     *
     * @param user - the user for which individual sticker usage
     *             will be assessed
     */
    public void countStickerUsage(@NonNull User user) {
        database.getReference("SentMessages").child(user.getUsername()).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.v(TAG, "Counting sticker usage");
                if (task.isSuccessful() && task.getResult() != null) {
                    if (task.getResult().hasChildren()) {
                        int numMessages = (int) task.getResult().getChildrenCount();
                        for (int i = 1; i <= numMessages; i++) {
                            String messageNum = "Message " + i;
                            String sentStickerLocation =
                                    task.getResult().child(messageNum).child("stickerLocation")
                                            .getValue().toString();
                            Log.v(TAG, "Collected location: " + sentStickerLocation);
                            hashMap.get(sentStickerLocation).incrementCount();
                        }
                    }

                    createStickerList();

                } else {
                    Log.v(TAG, "Something went wrong. Check your internet connection.");
                }
            }
        });
    }


    /**
     * Counts the total number of stickers utilized by the current
     * user. Also adds all stickers transmitted from the MainActivity
     * or MessagingService to the sticker list, with which the
     * RecyclerView will be generated.
     */
    public void createStickerList() {
        Log.v(TAG, "Creating sticker list" + hashMap.toString());
        for (String key: hashMap.keySet()) {
            Log.v(TAG, "key: " + key);
            Sticker addSticker = hashMap.get(key);
            Log.v(TAG, "Sticker: " + addSticker);
            stickerList.add(addSticker);
            totalStickerCount +=  addSticker.getCount();
        }

        binding.totalStickers.setText(String.format(getResources()
                        .getString(R.string.total_stickers), totalStickerCount));

        createRecyclerView();
    }


    /**
     * Sets up the StickerHistoryActivity with all information
     * present in the activity, prior to a state or orientation change.
     *
     * @param savedInstanceState - the bundle from which relevant user
     *                           and sticker information will be collected
     */
    public void initializeStickerHistoryActivity(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey("username") &&
                savedInstanceState.containsKey("loginTime")) {
            currUser = new User(savedInstanceState.getString("username"),
                    savedInstanceState.getString("loginTime"));
        }

        if (savedInstanceState.containsKey("stickerList")) {
            stickerList = savedInstanceState.getParcelableArrayList("stickerList");
        }

        if (savedInstanceState.containsKey("totalStickerCount")) {
            totalStickerCount = savedInstanceState.getInt("totalStickerCount");
            binding.totalStickers.setText(String.format(getResources()
                            .getString(R.string.total_stickers), totalStickerCount));
        }

    }


    /**
     * Saves the current user's information, as well as the list
     * of stickers displayed, prior to a state or orientation change.
     *
     * @param outState - the bundle within which user and sticker
     *                 information will be stored
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("username", currUser.getUsername());
        outState.putString("loginTime", currUser.getLoginTime());
        outState.putParcelableArrayList("stickerList", stickerList);
        outState.putInt("totalStickerCount", totalStickerCount);
    }


    /**
     * Creates a RecyclerView, which displays the number of times
     * the current user has sent each sticker.
     */
    public void createRecyclerView() {
        Log.v(TAG, "Creating recycler view");
        layoutManager = new LinearLayoutManager(this);
        recyclerView = binding.stickerHistoryRecyclerView;
        recyclerView.setHasFixedSize(true);

        recyclerViewAdapter = new StickerCountRViewAdapter(stickerList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}