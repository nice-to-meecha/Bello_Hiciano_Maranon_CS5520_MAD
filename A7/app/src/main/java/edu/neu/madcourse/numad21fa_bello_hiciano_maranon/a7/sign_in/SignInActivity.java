package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    ActivitySignInBinding binding;
    EditText enterUsername;
    TextView invalidUsernameMessage;


    /**
     * Sets up the SignInActivity, primarily initializing
     * larger, more time-instensive objects
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

        enterUsername = binding.enterUsername;
        invalidUsernameMessage = binding.invalidUsernameMessage;

        // Ensures error message is not visible when first opening
        // the Activity
        invalidUsernameMessage.setVisibility(View.INVISIBLE);
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
}