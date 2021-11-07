package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;

public class StickerHistoryActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_sticker_history);
    }


    // TODO - Create another RecyclerView Adapter class to handle
    // TODO - showing stickers alone. (Create a corresponding layout as well)

    // TODO - show total sticker usage at top and then include # of
    // TODO - stickers used (per sticker), next to corresponding stickers
}