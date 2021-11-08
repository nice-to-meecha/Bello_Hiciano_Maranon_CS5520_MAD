package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.databinding.ActivityMainBinding;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.DisplayMessagesReceivedActivity;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.DisplayMessagesSentActivity;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.SendMessageActivity;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.Sticker;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging.StickerHistoryActivity;
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
    private final String CHANNEL_ID = "1";
    private NotificationManager notificationManager;

    public FirebaseDatabase database;
    private ActivityMainBinding binding;
    private User currUser;
    private Token fcmToken;
    private ArrayList<Sticker> stickerList;


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
                                database.getReference("Users")
                                        .child(currUser.getUsername()).setValue(currUser);
                                addNotificationListner();
                                pairToken(currUser);
                            }
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
        createNotificationChannel();
        database = FirebaseDatabase.getInstance();
        initializeMainActivity(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                generateStickers();
            }
        }).start();
    }

    private void createNotificationChannel() {
        CharSequence name = "mainChannel";
        String description = "mainDescription";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("1", name, importance);
        channel.setDescription(description);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
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
        if (savedInstanceState == null) {
            return;
        }

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
            stickerList = new ArrayList<>();
            for (Parcelable parcel: savedInstanceState.getParcelableArrayList("stickerList")) {
                stickerList.add((Sticker) parcel);
            }
        }

    }

    /**
     * https://stackoverflow.com/questions/53138762/notificationcompat-builder-not-accepting-channel-id-as-argument
     */
    private void addNotificationListner() {
        if (currUser == null) {
            return;
        }
        database.getReference("ReceivedMessages")
                .child(currUser.getUsername())
                .getRef()
                .addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (!snapshot.exists()) {
                            return;
                        }
                        Map<Object, Object> data = (HashMap<Object, Object>)snapshot.getValue();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/uuuu H:m:s:S");
                        LocalDateTime dateTime = LocalDateTime.parse((String)data.get("timeSent"), formatter);
                        int second1 = dateTime.getSecond() + 60 * dateTime.getMinute() + (60 * 60 * dateTime.getHour());
                        LocalDateTime timeNow = LocalDateTime.now();
                        int second2 = timeNow.getSecond() + 60 * timeNow.getMinute() + (60 * 60 * timeNow.getHour());
                        if (second2 - second1 > 15) {
                            return;
                        }
                        long stickerID = (long)data.get("stickerID");
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this, "1")
                                .setSmallIcon((int)stickerID)
                                .setBadgeIconType((int)stickerID)
                                .setContentTitle("Sticker alert!")
                                .setContentText((String)data.get("sender") + " just sent you a sticker!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, getIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
                        getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mBuilder.setContentIntent(pi);
                        notificationManager.notify(1, mBuilder.build());
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
    }


    /**
     * Provides a state of the MainActivity, such that the user can
     * interact with the app.
     *
     * Ensures that the current user is signed in to the app. If not,
     * opens the SignInActivity.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (currUser == null) {
            signIn();
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
        database.getReference("Tokens").child(user.getUsername()).setValue(fcmToken);
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

                    if (user != null) {
                        pairToken(user);
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
     * Allows the current User to send a message to another registered user,
     * who is currently signed in to the app.
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
     *
     * @param view - the button used to start DisplayMessagesSentActivity
     */
    public void openSentMessageHistory(View view) {
        Intent openDisplayMessagesSent = new Intent(this,
                DisplayMessagesSentActivity.class);
        openDisplayMessagesSent.putExtra("username", currUser.getUsername());
        openDisplayMessagesSent.putExtra("loginTime", currUser.getLoginTime());
        activityResultLauncher.launch(openDisplayMessagesSent);
    }


    /**
     * Allows the current user to view all messages received, via the
     * DisplayMessagesReceivedActivity
     *
     * @param view - the button used to start DisplayMessagesReceivedActivity
     */
    public void openMessagesReceived(View view) {
        Intent openDisplayMessagesReceived = new Intent(this,
                DisplayMessagesReceivedActivity.class);
        openDisplayMessagesReceived.putExtra("username", currUser.getUsername());
        openDisplayMessagesReceived.putExtra("loginTime", currUser.getLoginTime());
        activityResultLauncher.launch(openDisplayMessagesReceived);
    }


    /**
     * Allows the current user to view all the number of each sticker
     * sent in messages
     *
     * @param view - the button used to start StickerHistoryActivity
     */
    public void openStickerHistory(View view) {
        Intent openStickerHistory = new Intent(this, StickerHistoryActivity.class);
        openStickerHistory.putExtra("username", currUser.getUsername());
        openStickerHistory.putExtra("loginTime", currUser.getLoginTime());
        activityResultLauncher.launch(openStickerHistory);
    }


    /**
     * Allows a user to logout from the app
     * @param view - the button by which users will logout from the app,
     *             via click
     */
    public void logOut(View view) {
        // Removes user from Tokens node
        database.getReference("Tokens").child(currUser.getUsername())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Something went wrong",
                            Toast.LENGTH_LONG).show();
                    Log.v(TAG, "Unable to remove user from Tokens node.");
                }
            }
        });

        currUser = null;

        signIn();
    }

    @Override
    public void finish() {
        logOut(binding.logoutButton);
        super.finish();
    }
}