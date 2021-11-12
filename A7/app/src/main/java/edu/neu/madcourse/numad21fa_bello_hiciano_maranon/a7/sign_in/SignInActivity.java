package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;
import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.databinding.ActivitySignInBinding;

/**
 * Class allowing users to sign in to the StickItToEm app
 *
 * @author bello
 */
public class SignInActivity extends AppCompatActivity {
    private final String TAG = "SignInActivity";
    private final int SIGN_IN_ACTIVITY_CODE = 101;
    private final int EXIT_APP = 107;

    ActivitySignInBinding binding;
    EditText enterUsername;
    TextView invalidUsernameMessage;


    /**
     * Sets up the SignInActivity, primarily initializing
     * larger, more time-intensive objects
     * @param savedInstanceState - information related to an active state
     *                           of the SignInActivity, prior to orientation
     *                           or state change
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.sign_in);

        enterUsername = binding.enterUsername;
        invalidUsernameMessage = binding.invalidUsernameMessage;

        // Ensures error message is not visible when first opening
        // the Activity
        invalidUsernameMessage.setVisibility(View.INVISIBLE);

        initializeSignInActivity(savedInstanceState);
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

        outState.putString("enterUsername", enterUsername.getText().toString());
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
    public void initializeSignInActivity(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            enterUsername.setText(savedInstanceState.getString("enterUsername"));
        }

    }


    /**
     * Adds the provided username to an intent and sends it back
     * to the MainActivity
     * @param view - the button by which users will submit their
     *             usernames, via click
     */
    public void transmitUsername(View view) {
        Intent sendUsername = new Intent();
        String username = enterUsername.getText().toString();

        if (!username.equals("")) {
            sendUsername.putExtra("username", username);
            setResult(SIGN_IN_ACTIVITY_CODE, sendUsername);
            finish();

        } else {
            // Shows error message, if no username is provided
            invalidUsernameMessage.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Closes the app, if a user presses the back button from
     * the SignInActivity (otherwise, app will crash)
     */
    @Override
    public void onBackPressed() {
        Intent closeApp = new Intent();
        setResult(EXIT_APP, closeApp);
        finish();
    }
}