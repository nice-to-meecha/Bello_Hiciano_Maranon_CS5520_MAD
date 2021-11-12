package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.databinding
        .ActivityMainBinding;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.DisplayMessagesReceivedActivity;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.DisplayMessagesSentActivity;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.MessageSent;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.SendMessageActivity;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.ShowSelectedMessageActivity;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.Sticker;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.StickerHistoryActivity;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.ItemClickListener;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.recycler_view.SenderRecipientRViewAdapter;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in.SignInActivity;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in.Token;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in.User;

/**
 * Class providing for functionality of the first screen
 * viewed by users
 *
 * @author bello
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private final int SIGN_IN_ACTIVITY_CODE = 101;
    private final int SEND_MESSAGE_ACTIVITY_CODE = 102;
    private final int DISPLAY_MESSAGES_SENT_ACTIVITY_CODE = 103;
    private final int DISPLAY_MESSAGES_RECEIVED_ACTIVITY_CODE = 104;
    private final int STICKER_HISTORY_ACTIVITY_CODE = 105;
    private final int SHOW_SELECTED_MESSAGE_ACTIVITY_CODE = 106;
    private final int EXIT_APP = 107;

    public FirebaseDatabase database;
    private ActivityMainBinding binding;
    private User currUser;
    private Token fcmToken;
    private ArrayList<Sticker> stickerList;
    private ArrayList<MessageSent> communityFeed;
    private RecyclerView recyclerView;
    private SenderRecipientRViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ChildEventListener messageListener;


    /*
    An ActivityResultLauncher, which allows Activities opened by the MainActivity
    to return their results here (below) for additional processing.
     */
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    switch (result.getResultCode()) {
                        case (SIGN_IN_ACTIVITY_CODE):
                            Intent usernameIntent = result.getData();
                            if (usernameIntent != null) {
                                String username =
                                        usernameIntent.getStringExtra("username");
                                currUser = new User(username,
                                        LocalDateTime.now()
                                        .format(DateTimeFormatter
                                                .ofPattern("MM/dd/uuuu H:m:s:S")));

                                pairToken(currUser);
                            }
                            break;
                        case (EXIT_APP):
                            finish();
                            break;
                    }
                }
            });


    /**
     * Sets up the MainActivity, primarily initializing
     * larger, more time-intensive objects
     * @param savedInstanceState - information related to an active state
     *                           of the MainActivity, prior to orientation
     *                           or state change
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, binding.mainDrawerLayout,
                        binding.toolbar, R.string.open_nav_menu, R.string.closed_nav_menu);
        binding.mainDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.toolbar.setNavigationIcon(R.drawable.menu_icon_24dp);
        // Allows navigation menu items to be selected
        binding.navigationMenu.bringToFront();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Community Feed");

        addNavigationItemSelector();

        createNotificationChannel();

        database = FirebaseDatabase.getInstance();

        if (savedInstanceState == null) {
            generateToken(null);
            generateStickers();
            getCommunityFeed();

        } else {
            initializeMainActivity(savedInstanceState);
        }
    }


    /**
     * Prevents the app from closing, if the back button is pressed,
     * while the navigation menu is open.
     */
    @Override
    public void onBackPressed() {
        if (binding.mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        super.onBackPressed();
    }


    /**
     * Restores the state of the MainActivity, prior to an
     * orientation or state change, such that progress previously
     * made is not lost.
     *
     * @param savedInstanceState - a Bundle containing user and token
     *                           information, from which the current state
     *                           of the MainActivity can be updated
     */
    private void initializeMainActivity(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey("username") &&
                savedInstanceState.containsKey("loginTime")) {
            currUser = new User(savedInstanceState.getString("username"),
                    savedInstanceState.getString("loginTime"));
        }

        if (savedInstanceState.containsKey("token") &&
                savedInstanceState.containsKey("registerTime")) {
            fcmToken = new Token(savedInstanceState.getString("token"),
                    savedInstanceState.getString("registerTime"));
        }

        if (savedInstanceState.containsKey("stickerList")) {
            stickerList = savedInstanceState.getParcelableArrayList("stickerList");
        }

        if (savedInstanceState.containsKey("communityFeed")) {
            communityFeed = savedInstanceState.getParcelableArrayList("communityFeed");
        }
    }


    public void checkIfSignedIn() {
        database.getReference("ExistingTokens").child(fcmToken.getToken())
                .child("user").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DataSnapshot data = task.getResult();
                    if (data.hasChildren()) {
                        currUser = new User(data.child("username").getValue().toString(),
                                data.child("loginTime").getValue().toString());

                    } else {
                        signIn();
                    }

                } else {
                    Log.v(TAG, "Something went wrong when checking login status.");
                }
            }
        });
    }


    /**
     * Saves the current state of the MainActivity, should it
     * undergo an orientation or state change.
     *
     * @param outState - a Bundle in which the current user's
     *                 information and token (of the app instance)
     *                 will be recorded
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (currUser != null) {
            outState.putString("username", currUser.getUsername());
            outState.putString("loginTime", currUser.getLoginTime());
        }

        if (fcmToken != null) {
            outState.putString("token", fcmToken.getToken());
            outState.putString("registerTime", fcmToken.getRegisterTime());
        }

        if (stickerList != null) {
            outState.putParcelableArrayList("stickerList", stickerList);
        }

        if (communityFeed != null) {
            outState.putParcelableArrayList("communityFeed", communityFeed);
        }
    }


    /**
     * Opens the SignInActivity, such that users can sign in to the
     * Stick It To 'Em app
     */
    public void signIn() {
        Intent openSignIn = new Intent(this, SignInActivity.class);
        activityResultLauncher.launch(openSignIn);
    }


    /**
     * Stores the token of a particular instance of the Stick It To 'Em app
     * with a user's username, within the Tokens node of the Firebase Realtime
     * Database.
     *
     * @param user - the user with which a particular app instance's token
     *             will be paired
     */
    public void pairToken(User user) {
        if (fcmToken == null) {
            Log.v(TAG, "Generating token");
            generateToken(user);
            return;
        }

        Log.v(TAG, "Pairing token");
        database.getReference("UserTokenLogin")
                .child(user.getUsername()).child("token").setValue(fcmToken);
        database.getReference("UserTokenLogin")
                .child(user.getUsername()).child("loginTime").setValue(user.getLoginTime());
        database.getReference("ExistingTokens")
                .child(fcmToken.getToken()).child("user").setValue(user);
    }


    /**
     * Produces a token for a particular instance of an app, such
     * that certain app installations may be identified for messaging purposes.
     * Will also pair a user with the generated token, if a non-null User object
     * is provided.
     *
     * @param user - a potential user with which the newly-generated token will
     *             be paired
     */
    public void generateToken(User user) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String tokenString = task.getResult();
                    fcmToken = new Token(tokenString,
                            LocalDateTime.now().format(DateTimeFormatter
                                    .ofPattern("MM/dd/uuuu H:m:s:S")));
                    Log.v(TAG, fcmToken.toString());
                    addToExistingTokens(fcmToken);

                    if (user != null) {
                        pairToken(user);

                    } else {
                        checkIfSignedIn();

                    }

                } else {
                    Toast.makeText(MainActivity.this,
                            "Unable to generate a token. Check internet connection.",
                            Toast.LENGTH_LONG).show();
                    Log.v(TAG, "Unable to generate token.");
                }
            }
        });
    }


    /**
     * Adds new tokens to the ExistingTokens node of the database, if
     * they did not already exist. If the tokens were already in existence,
     * their registration time is simply updated.
     *
     * @param newToken - the token generated for a particular instance of the
     *                 Stick It To 'Em app
     */
    public void addToExistingTokens(Token newToken) {
        database.getReference("ExistingTokens").child(newToken.getToken())
                .child("token").setValue(newToken);
    }


    /**
     * Allows the current User to send a message to another registered user,
     * who is currently signed in to the app.
     *
     * @param view - the floating action button selected by users
     *             to open the SendMessageActivity
     */
    public void openSendMessage(View view) {
        if (currUser == null || fcmToken == null) {
            return;
        }

        Intent openSendMessageActivity = new Intent(this,
                SendMessageActivity.class);
        openSendMessageActivity.putExtra("username", currUser.getUsername());
        openSendMessageActivity.putExtra("loginTime", currUser.getLoginTime());
        openSendMessageActivity.putExtra("token", fcmToken.getToken());
        openSendMessageActivity.putExtra("registerTime", fcmToken.getRegisterTime());
        openSendMessageActivity.putParcelableArrayListExtra("stickerList", stickerList);
        activityResultLauncher.launch(openSendMessageActivity);
    }


    /**
     * Allows the current User to send a message to another registered user,
     * who is currently signed in to the app. Called, once the 'Craft'
     * Navigation menu item is selected
     */
    public void openSendMessage() {
        if (currUser == null || fcmToken == null) {
            return;
        }

        Intent openSendMessageActivity = new Intent(this,
                SendMessageActivity.class);
        openSendMessageActivity.putExtra("username", currUser.getUsername());
        openSendMessageActivity.putExtra("loginTime", currUser.getLoginTime());
        openSendMessageActivity.putExtra("token", fcmToken.getToken());
        openSendMessageActivity.putExtra("registerTime", fcmToken.getRegisterTime());
        openSendMessageActivity.putParcelableArrayListExtra("stickerList", stickerList);
        activityResultLauncher.launch(openSendMessageActivity);
    }


    /**
     * Retrieves the aliases and locations of all Stickers
     * that a particular user can access
     */
    public void generateStickers() {
        database.getReference("Stickers").get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        Log.v(TAG, "Making sticker list");
                        stickerList = new ArrayList<>();
                        for (DataSnapshot child: task.getResult().getChildren()) {
                            stickerList.add(new Sticker(child.getKey(),
                                    child.getValue().toString()));
                        }
                        Log.v(TAG, "Stickers: " + stickerList.toString());
                    }
                }
            }
        });
    }


    /**
     * Allows the current user to view all messages sent, via the
     * DisplayMessagesSentActivity
     */
    public void openSentMessageHistory() {
        Intent openDisplayMessagesSent = new Intent(this,
                DisplayMessagesSentActivity.class);
        openDisplayMessagesSent.putExtra("username", currUser.getUsername());
        openDisplayMessagesSent.putExtra("loginTime", currUser.getLoginTime());
        activityResultLauncher.launch(openDisplayMessagesSent);
    }


    /**
     * Allows the current user to view all messages received, via the
     * DisplayMessagesReceivedActivity
     */
    public void openMessagesReceived() {
        Intent openDisplayMessagesReceived = new Intent(this,
                DisplayMessagesReceivedActivity.class);
        openDisplayMessagesReceived.putExtra("username", currUser.getUsername());
        openDisplayMessagesReceived.putExtra("loginTime", currUser.getLoginTime());
        activityResultLauncher.launch(openDisplayMessagesReceived);
    }


    /**
     * Allows the current user to view all the number of each sticker
     * sent in messages
     */
    public void openStickerHistory() {
        Intent openStickerHistory = new Intent(this, StickerHistoryActivity.class);
        openStickerHistory.putExtra("username", currUser.getUsername());
        openStickerHistory.putExtra("loginTime", currUser.getLoginTime());
        activityResultLauncher.launch(openStickerHistory);
    }


    /**
     * Allows a user to logout from the app
     */
    public void logOut() {
        // Removes user from Tokens node
        database.getReference("ExistingTokens").child(fcmToken.getToken()).child("user")
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Something went wrong",
                            Toast.LENGTH_LONG).show();
                    Log.v(TAG, "Unable to remove user from ExistingTokens node.");
                }
            }
        });

        database.getReference("UserTokenLogin").child(currUser.getUsername())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Something went wrong",
                            Toast.LENGTH_LONG).show();
                    Log.v(TAG, "Unable to remove user stats from UserTokenLogin node.");
                }
            }
        });

        currUser = null;

        signIn();
    }


    /**
     * Generates a notification channel, such that all notifications
     * received from Firebase Cloud Messaging are delivered to the correct
     * location
     */
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelID = getResources().getString(R.string.channel_id);
            String channelName = getResources().getString(R.string.channel_name);
            String channelDescription = getResources().getString(R.string.channel_description);
            int notificationImportance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel =
                    new NotificationChannel(channelID, channelName, notificationImportance);
            notificationChannel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


    /**
     * Initializes a listener for the items within the navigation
     * menu, such that each item will elicit a response.
     */
    public void addNavigationItemSelector() {
        binding.navigationMenu.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.v(TAG, "Clicked icon: " + item.getTitle() + " " + item.toString());
                switch (item.getItemId()) {
                    case (R.id.home):
                        Log.v(TAG, "Clicked home icon");
                        onBackPressed();
                        break;
                    case (R.id.craft):
                        Log.v(TAG, "Clicked craft icon");
                        openSendMessage();
                        break;
                    case (R.id.inbox):
                        Log.v(TAG, "Clicked inbox icon");
                        openMessagesReceived();
                        break;
                    case (R.id.sent):
                        Log.v(TAG, "Clicked sent icon");
                        openSentMessageHistory();
                        break;
                    case (R.id.history):
                        Log.v(TAG, "Clicked history icon");
                        openStickerHistory();
                        break;
                    case (R.id.logout):
                        Log.v(TAG, "Clicked logout icon");
                        logOut();
                        break;
                }
                return true;
            }
        });
    }


    public void getCommunityFeed() {
        communityFeed = new ArrayList<>();
        Log.v(TAG, "Getting community feed");
        database.getReference("ReceivedMessages").get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.v(TAG, "ReceivedMessages task successful");
                            DataSnapshot data = task.getResult();
                            if (data.hasChildren()) {
                                for (DataSnapshot child: data.getChildren()) {
                                    Log.v(TAG, "user: " + child.getKey());
                                    int numOfMessages = (int) child.getChildrenCount();
                                    for (int i = 1; i <= numOfMessages; i++) {
                                        communityFeed.add(new MessageSent(
                                                child.child("Message " + i).child("sender")
                                                        .getValue().toString(),
                                                child.child("Message " + i).child("recipient")
                                                        .getValue().toString(),
                                                child.child("Message " + i).child("stickerLocation")
                                                        .getValue().toString(),
                                                Integer.parseInt(child.child("Message " + i)
                                                        .child("stickerID").getValue().toString()),
                                                child.child("Message " + i).child("timeSent")
                                                        .getValue().toString()));
                                    }
                                }
                                Log.v(TAG, "Community Messages: " + communityFeed);
                            }
                        }
                        createRecyclerView();
                    }
                });
    }


    public void createRecyclerView() {
        Log.v(TAG, "Creating recycler view");
        layoutManager = new LinearLayoutManager(this);
        recyclerView = binding.communityFeedRecyclerView;
        recyclerView.setHasFixedSize(true);

        adapter = new SenderRecipientRViewAdapter(communityFeed);
        ItemClickListener listener = new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MessageSent selectedMessage = communityFeed.get(position);
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

        adapter.setOnClickListener(listener);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        // newMessageListener();
    }


    /**
     * Adds a listener to all messages transmitted between users,
     * such that the RecyclerView retaining said messages will be
     * updated accordingly.
     */
    public void newMessageListener() {
        messageListener = database.getReference("ReceivedMessages")
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

                        if (prevMessageCount == communityFeed.size() &&
                                currMessageCount == communityFeed.size() + 1) {
                            Log.v(TAG, "Adding to message list");
                            communityFeed.add(0, new MessageSent(
                                    snapshot.child("sender").getValue().toString(),
                                    snapshot.child("recipient").getValue().toString(),
                                    snapshot.child("stickerLocation").getValue().toString(),
                                    Integer.parseInt(snapshot.child("stickerID").getValue().toString()),
                                    snapshot.child("timeSent").getValue().toString()));

                            adapter.notifyItemInserted(0);
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
}