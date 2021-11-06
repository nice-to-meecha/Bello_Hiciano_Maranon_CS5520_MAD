package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

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

    private ActivitySendMessageBinding binding;
    private User currUser;
    private Token fcmToken;
    private GridView stickerGrid;
    private ImageView selectedSticker;
    private StickerGridAdapter adapter;
    private ArrayList<Sticker> stickerList;


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

        initializeUserAndToken();
        initializeSendMessageActivity(savedInstanceState);

        gridSetUp();

    }


    /**
     * Using the intent provided by the MainActivity, the current User
     * and associated token are initialized, such that cloud messaging
     * may commence.
     */
    public void initializeUserAndToken() {
        Intent currUserAndTokenIntent = getIntent();
        if (currUserAndTokenIntent != null) {
            currUser = new User(currUserAndTokenIntent.getStringExtra("username"),
                    currUserAndTokenIntent.getStringExtra("loginTime"));

            fcmToken = new Token(currUserAndTokenIntent.getStringExtra("token"),
                    currUserAndTokenIntent.getStringExtra("registerTime"));

        } else {
            Toast.makeText(this, "Cannot send message without user and token.",
                    Toast.LENGTH_LONG).show();
            try {
                wait(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
    }


    public void gridSetUp() {
        stickerList = generateStickerList();
        adapter = new StickerGridAdapter(this, stickerList);
        stickerGrid.setAdapter(adapter);
        stickerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Sticker currSticker = stickerList.get(i);
                int id = getResources().getIdentifier(currSticker.getLocation(), "drawable", getPackageName());
                selectedSticker.setImageResource(id);
            }
        });
    }


    public ArrayList<Sticker> generateStickerList() {
        Sticker sticker1 = new Sticker("android logo", "ic_android_black_100dp");
        Sticker sticker2 = new Sticker("sun", "a7_home_icon_foreground");
        ArrayList<Sticker> newList = new ArrayList<>();
        newList.add(sticker1);
        newList.add(sticker2);
        newList.add(sticker1);
        newList.add(sticker2);
        newList.add(sticker1);
        newList.add(sticker2);
        newList.add(sticker1);
        newList.add(sticker2);
        newList.add(sticker1);
        return newList;

    }


}