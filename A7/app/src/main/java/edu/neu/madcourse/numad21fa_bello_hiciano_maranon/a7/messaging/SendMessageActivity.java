package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.MainActivity;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in.Token;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in.User;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.databinding.ActivitySendMessageBinding;

/**
 * Class generating an Activity that allows users to send
 * messages to other app users.
 *
 * @author bello
 */
public class SendMessageActivity extends AppCompatActivity {
    private final int SEND_MESSAGE_ACTIVITY_CODE = 102;
    private final String TAG = "SendMessageActivity";
    private final String SERVER_KEY = "key=" + "AAAAeKZsXUs:APA91bEWmcC1OL_" +
            "uOHQ8fXKziF7QLAxR7Fnp70kYor2nYpTA4-H2l8IXrXA9uemRczcF326MI5CsVQ" +
            "0PypqyFaHTLsKt36O--rLNyH02M2_BoV4VqBmTg2UiOPmM7F0gEqsgZsDOeO7P";

    private ActivitySendMessageBinding binding;
    private User currUser;
    private Token fcmToken;
    private GridView stickerGrid;
    private ImageView selectedSticker;
    private int selectedStickerResID;
    private EditText enterRecipient;
    private TextView invalidRecipient;
    private StickerGridAdapter adapter;
    private ArrayList<Sticker> stickerList;
    private FirebaseDatabase database;


    /**
     * Sets up the SendActivity, primarily initializing
     * larger, more time-intensive objects
     * @param savedInstanceState - information related to an active state
     *                           of the SendMessageActivity, prior to orientation
     *                           or state change
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySendMessageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        stickerGrid = binding.stickerGrid;
        selectedSticker = binding.selectedSticker;
        enterRecipient = binding.enterRecipient;
        invalidRecipient = binding.invalidRecipientErrorMessage;

        database = FirebaseDatabase.getInstance();

        initializeUserAndToken();
        initializeSendMessageActivity(savedInstanceState);

        gridSetUp();

    }


    /**
     * Using the intent provided by the MainActivity, the current User
     * and associated token are initialized, such that cloud messaging
     * may commence. Available stickers are also processed from the intent.
     */
    public void initializeUserAndToken() {
        int firstSticker = 0;
        Intent currUserAndTokenIntent = getIntent();

        if (currUserAndTokenIntent != null) {
            currUser = new User(currUserAndTokenIntent.getStringExtra("username"),
                    currUserAndTokenIntent.getStringExtra("loginTime"));

            fcmToken = new Token(currUserAndTokenIntent.getStringExtra("token"),
                    currUserAndTokenIntent.getStringExtra("registerTime"));

            stickerList = currUserAndTokenIntent.getParcelableArrayListExtra("stickerList");

            selectedStickerResID = getResources().getIdentifier(stickerList.get(firstSticker)
                            .getLocation(), "drawable", getPackageName());
            selectedSticker.setImageResource(selectedStickerResID);
            selectedSticker.setTransitionName(stickerList.get(firstSticker).getLocation());

        } else {
            Toast.makeText(this, "Cannot send message without user and token.",
                    Toast.LENGTH_LONG).show();

            Log.v(TAG, "User and token unavailable");

            finish();
        }
    }


    /**
     * Saves the current state of the SignInActivity, should it
     * undergo an orientation or state change.
     *
     * @param outState - a Bundle in which the current user's
     *                 information and token (of the app instance)
     *                 will be recorded
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("username", currUser.getUsername());
        outState.putString("loginTime", currUser.getLoginTime());
        outState.putString("token", fcmToken.getToken());
        outState.putString("registerTime", fcmToken.getRegisterTime());
        outState.putString("selectedSticker", selectedSticker.getTransitionName());
        outState.putInt("selectedStickerResID", selectedStickerResID);
        outState.putParcelableArrayList("stickerList", stickerList);
        outState.putInt("invalidRecipient", invalidRecipient.getVisibility());
    }


    /**
     * Initializes the state of the SignInActivity, by setting the
     * text of the EditText view to that which had already been typed,
     * prior to an orientation or state change
     *
     * @param savedInstanceState - a Bundle in which the username
     *                           typed within the EditText view
     *                           will be recorded
     */
    public void initializeSendMessageActivity(Bundle savedInstanceState) {
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

        if (savedInstanceState.containsKey("selectedSticker") &&
                savedInstanceState.containsKey("selectedStickerResID")) {
            selectedSticker.setTransitionName(savedInstanceState.getString("selectedSticker"));
            selectedStickerResID = savedInstanceState.getInt("selectedStickerResID");
            selectedSticker.setImageResource(selectedStickerResID);
        }

        if (savedInstanceState.containsKey("stickerList")) {
            stickerList = savedInstanceState.getParcelableArrayList("stickerList");
        }

        if (savedInstanceState.containsKey("invalidRecipient")) {
            invalidRecipient.setVisibility(
                    savedInstanceState.getInt("invalidRecipient"));
        }
    }


    /**
     * Initializes the GridView, which will display the stickers
     * that users can send
     */
    public void gridSetUp() {
        adapter = new StickerGridAdapter(this, stickerList);
        stickerGrid.setAdapter(adapter);
        stickerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Sticker currSticker = stickerList.get(i);
                /*
                 * I utilized the code provided in the first answer to this post
                 * (https://stackoverflow.com/questions/21856260/how-can-i-convert-string-to-drawable)
                 * in order to set the drawable, from a String
                 */
                selectedStickerResID = getResources().getIdentifier(currSticker.getLocation(),
                        "drawable", getPackageName());
                selectedSticker.setImageResource(selectedStickerResID);
                selectedSticker.setTransitionName(currSticker.getLocation());
            }
        });
    }


    /**
     * Checks the status of the entered recipient, relaying an error message,
     * (1) if the recipient is not signed in to the app or (2) the user does
     * not exist.
     * If the recipient is viable, the selected sticker will be sent.
     *
     * @param view - the button by which the current user is
     *             able to send a message
     */
    public void checkRecipient(View view) {
        String recipient = enterRecipient.getText().toString();

        database.getReference("Tokens").child(recipient).child("token").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            if (task.getResult().getValue() != null) {
                                Log.v(TAG, task.getResult().getValue().toString());
                                String recipientToken = task.getResult().getValue().toString();
                                sendFCMMessage(recipient, recipientToken);

                            } else {
                                invalidRecipient.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toast.makeText(SendMessageActivity.this,
                                    "Something went wrong. Check your internet connection.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    /**
     * Sends a sticker message to a specified recipient
     *
     * @param recipientUsername - the username of the intended recipient of
     *                          the sticker message the current user is
     *                          attempting to send
     * @param recipientToken - the token of the intended recipient of
     *                       the sticker message the current user is
     *                       attempting to send
     */
    public void sendFCMMessage(String recipientUsername, String recipientToken) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();

        try {
            jNotification.put("title", "Sticker Alert!");
            jNotification.put("body", currUser.getUsername() +
                    " just sent you a sticker!");

            jData.put("sticker alias", selectedSticker.getTransitionName());

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);
            jPayload.put("to", recipientToken);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    connectWithServer(recipientUsername, jPayload);
                }
            }).start();


        } catch (JSONException jsonException) {
            Log.v(TAG, Arrays.toString(jsonException.getStackTrace()));
        }
    }


    /**
     * Connects with and sends the desired sticker to the server
     *
     * @param recipientUsername - the username of the intended recipient of
     *                          the sticker message the current user is
     *                          attempting to send
     * @param jPayload - the message to be sent to the server
     */
    public void connectWithServer(String recipientUsername, JSONObject jPayload) {
        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setDoOutput(true);

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            storeMessage(recipientUsername,
                    jPayload.getJSONObject("data").getString("sticker alias"),
                    selectedStickerResID);

            InputStream inputStream = conn.getInputStream();
            logServerInput(inputStream);

        } catch (IOException | JSONException exception) {
            Log.v(TAG, Arrays.toString(exception.getStackTrace()));

        }
    }


    /**
     * Sends a sticker message to a specified recipient
     *
     * @param recipientUsername - the username of the intended recipient of
     *                          the sticker message the current user is
     *                          attempting to send
     * @param stickerAlias - the alias of the sticker sent in the message
     * @param stickerID - the resource ID of the sticker image
     */
    public void storeMessage(String recipientUsername, String stickerAlias, int stickerID) {
        int nextMessage = 1;
        MessageSent message = new MessageSent(currUser.getUsername(),
                recipientUsername,
                stickerAlias,
                stickerID,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/uuuu H:m:s:S")));

        database.getReference("SentMessages").child(currUser.getUsername()).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (task.getResult().hasChildren()) {
                        int messageNumber = (int) task.getResult().getChildrenCount() +
                                nextMessage;
                        // Stores all messages sent by a particular user, within
                        // that user's node
                        task.getResult().getRef().child("Message " + messageNumber)
                                .setValue(message);

                    } else {
                        task.getResult().getRef().child("Message " + nextMessage)
                                .setValue(message);
                    }

                } else {
                    Log.v(TAG, "Something went wrong. Check your internet connection.");
                }
            }
        });


        database.getReference("ReceivedMessages").child(recipientUsername).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            if (task.getResult().hasChildren()) {
                                int messageNumber = (int) task.getResult().getChildrenCount() +
                                        nextMessage;
                                // Stores all messages sent by a particular user, within
                                // that user's node
                                task.getResult().getRef().child("Message " + messageNumber)
                                        .setValue(message);

                            } else {
                                task.getResult().getRef().child("Message " + nextMessage)
                                        .setValue(message);
                            }

                        } else {
                            Log.v(TAG, "Something went wrong. Check your internet connection.");
                        }
                    }
                });
    }


    /**
     * Logs the message returned by the server, following device-to-device
     * messaging.
     *
     * @param inputStream - the stream by which input is received from
     *                    the server
     */
    public void logServerInput(InputStream inputStream) {
        StringBuilder message = new StringBuilder();
        String line;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = reader.readLine()) != null) {
                message.append(line);
            }

            Log.v(TAG, message.toString().replace(",", ",\n"));
            inputStream.close();

        } catch (IOException ioException) {
            Log.v(TAG, Arrays.toString(ioException.getStackTrace()));
        }
    }

}