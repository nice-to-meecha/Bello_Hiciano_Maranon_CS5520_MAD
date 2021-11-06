package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.sign_in;

import androidx.annotation.NonNull;

/**
 * Class representing users of the StickItToEm app
 *
 * @author bello
 */
public class User {

    public String username;
    public String loginTime;

    /**
     * Generates a User object, based on a user's username
     * and time of login
     *
     * @param username - an alias for the user, to be displayed
     *                 within the app
     * @param loginTime - the most recent time at which the user
     *                  logged in to the app
     */
    public User(String username, String loginTime) {
        this.username = username;
        this.loginTime = loginTime;
    }

    /**
     * Returns the username of the current User
     *
     * @return the username of the current User
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the time at which the User most recently
     * logged in
     *
     * @return the time at which the User most recently
     * logged in
     */
    public String getLoginTime() {
        return this.loginTime;
    }

    /**
     * Provides a String representation of a User, for logging purposes
     *
     * @return String representation of a User, for logging purposes
     */
    @NonNull
    @Override
    public String toString() {
        return "Username: " + username +
                ", LoginTime: " + loginTime;
    }
}
