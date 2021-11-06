package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.databinding.ActivityMainBinding;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in.SignInActivity;
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

    FirebaseDatabase database;
    ActivityMainBinding binding;
    User currUser;

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
                            }
                            break;
                    }
                }
            });


    /**
     * Sets up the MainActivity, primarily initializing
     * larger, more time-instensive objects
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

        database = FirebaseDatabase.getInstance();

        initializeMainActivity(savedInstanceState);
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

        // TODO - account for TOKEN as well
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

        if (currUser == null) {
            outState.putString("username", null);
            outState.putString("loginTime", null);

        } else {
            outState.putString("username", currUser.getUsername());
            outState.putString("loginTime", currUser.getLoginTime());
        }

        // TODO - account for TOKEN as well
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
            Intent openSignIn = new Intent(this, SignInActivity.class);
            activityResultLauncher.launch(openSignIn);
        }
    }


    /**
     * Allows a user to logout from the app
     * @param view - the button by which users will logout from the app,
     *             via click
     */
    public void logOut(View view) {
        currUser = null;
        // TODO un-pair user with token -- remove user from Tokens node
    }


}