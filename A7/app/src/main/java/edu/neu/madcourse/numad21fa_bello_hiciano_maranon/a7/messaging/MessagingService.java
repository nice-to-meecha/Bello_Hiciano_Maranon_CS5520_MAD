package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.R;


/**
 * Class allowing for messages sent by Firebase Cloud Messaging Service
 * to be handled in the foreground
 *
 * All code was generated based on related documentation
 * (https://firebase.google.com/docs/cloud-messaging/android/receive)
 * and the linked code, stored on GitHub
 * (https://github.com/firebase/quickstart-android/blob/
 * 320f5fb45f155de3daf8b997c3788a4a187a024d/messaging/app/src/main/java/
 * com/google/firebase/quickstart/fcm/java/MyFirebaseMessagingService.
 * java#L58-L101)
 *
 *
 * @author bello
 */
public class MessagingService extends FirebaseMessagingService {
    private final String TAG = "MessagingService";
    private final int DISPLAY_MESSAGES_RECEIVED_ACTIVITY_CODE = 104;
    private int notificationID = 1;


    /**
     * Handles the activity of notifications and data received from
     * Firebase Cloud Messaging, when the app is operating in the
     * foreground. When in the background, only data is handled here.
     * The notification is sent directly to the system tray.
     *
     * @param remoteMessage - the message object provided by Firebase
     *                      Cloud Messaging services, as sent by a
     *                      device (via SendMessageActivity)
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        /*
         * The note below comes directly from Firebase documentation.
         * It is a useful note, so I am leaving it here fore reference:
         *
         * There are two types of messages data messages and
         * notification messages. Data messages are handled
         * here in onMessageReceived, whether the app is in
         * the foreground or background. Data messages are the type
         * traditionally used with GCM. Notification messages
         * are only received here in onMessageReceived when the app
         * is in the foreground. When the app is in the background
         * an automatically generated notification is displayed.
         * When the user taps on the notification they are returned
         * to the app. Messages containing both notification
         * and data payloads are treated as notification messages.
         * The Firebase console always sends notification messages.
         * For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
         */

        Log.v(TAG, "Message source: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            if (data.containsKey("stickerLocation")) {
                sendNotification(remoteMessage);
            }
        }
    }


    public void sendNotification(RemoteMessage stickerMessage) {
        Log.v(TAG, "Creating notification: " + stickerMessage.getNotification());
        String channelID = getResources().getString(R.string.channel_id);
        Intent receivedMessagesIntent = new Intent(this, DisplayMessagesReceivedActivity.class);
        receivedMessagesIntent.putExtra("username", stickerMessage.getData().get("recipientUsername"));
        receivedMessagesIntent.putExtra("loginTime", stickerMessage.getData().get("loginTime"));
        PendingIntent openMessageHistory = PendingIntent.getActivity(this,
                DISPLAY_MESSAGES_RECEIVED_ACTIVITY_CODE,
                receivedMessagesIntent,
                PendingIntent.FLAG_ONE_SHOT);


        /*
         * I used the code from the top-voted response here
         * (https://stackoverflow.com/questions/57626230/
         * decoding-bitmap-from-drawable-resource-id-is-giving-null)
         * in order to produce a bitmap from a drawable
         */
        String stickerLocation = stickerMessage.getData().get("stickerLocation");
        Log.v(TAG, "Sticker Location: " + stickerLocation);
        int stickerResID = getResources().getIdentifier(stickerLocation,
                "drawable", getPackageName());
        Bitmap stickerBitmap = convertDrawableToBitmap(stickerResID);

        Log.v(TAG, "Sticker Bitmap: " + stickerBitmap.toString());

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelID)
                        .setContentTitle(stickerMessage.getNotification().getTitle())
                        .setContentText(stickerMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setContentIntent(openMessageHistory)
                        .setPriority(stickerMessage.getPriority())
                        .setSmallIcon(R.mipmap.a7_home_icon_round)
                        .setLargeIcon(stickerBitmap);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationID, notificationBuilder.build());


    }


    /**
     * This code is from the top-voted response here
     * (https://stackoverflow.com/questions/57626230/
     * decoding-bitmap-from-drawable-resource-id-is-giving-null).
     *
     * It is used to produce a bitmap from a drawable, such that a
     * sticker can be used as a large icon in a notification.
     * @param stickerID
     * @return
     */
    public Bitmap convertDrawableToBitmap(int stickerID) {
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),
                stickerID);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
