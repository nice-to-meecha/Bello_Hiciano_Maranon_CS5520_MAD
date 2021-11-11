package edu.neu.madcourse.numad21fa_bello_hiciano_maranon.a7.messaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
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
    private int UNIQUE_INTENT_ID = 1;
    private int notificationID = 1;
    private FirebaseDatabase database;
    private ArrayList<Sticker> stickerList;
    private int prevNotificationNum;


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
                generateStickers(remoteMessage);
            }
        }
    }


    public int checkAllActiveNotifications() {
        int noNotifications = 0, soleNotificationIndex = 0, possiblePrevMessageNum = 2;
        NotificationManager manager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager.getActiveNotifications().length == noNotifications) {
            prevNotificationNum = noNotifications;

        } else {
            // Provides body (text) of most recent notification
            String[] splitMessageBody = manager.getActiveNotifications()[soleNotificationIndex]
                    .getNotification().extras.getCharSequence(Notification.EXTRA_TEXT)
                    .toString().split("\\s");

            String potentialNumberOfMessages =
                    splitMessageBody[splitMessageBody.length - possiblePrevMessageNum];

            if (potentialNumberOfMessages.matches("[0-9]+")) {
                prevNotificationNum = Integer.parseInt(potentialNumberOfMessages);

            } else {
                prevNotificationNum = 1;
            }

            Log.v(TAG, manager.getActiveNotifications()[soleNotificationIndex]
                    .getNotification().extras.getCharSequence(Notification.EXTRA_TEXT)
                    .toString());
        }

        Log.v(TAG, "Number of Previous Notifications: " + prevNotificationNum);
        return prevNotificationNum;
    }


    /**
     * Retrieves the aliases and locations of all Stickers
     * that a particular user can access
     *
     * @param stickerMessage - the data retained within the message initially
     *                       sent to the Firebase cloud, prior to receipt
     */
    public void generateStickers(RemoteMessage stickerMessage) {
        stickerList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

        database.getReference("Stickers").get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                Log.v(TAG, "Making sticker list");
                                for (DataSnapshot child: task.getResult().getChildren()) {
                                    stickerList.add(new Sticker(child.getKey(),
                                            child.getValue().toString()));
                                }
                                Log.v(TAG, "Stickers: " + stickerList.toString());
                                sendNotification(stickerMessage);
                            }
                        }
                    }
                });
    }

    /**
     * Generates a notification, such that users can view or respond to messages,
     * if the Stick It To 'Em app is currently in the foreground
     *
     * @param stickerMessage - the data retained within the message initially
     *                       sent to the Firebase cloud, prior to receipt
     */
    public void sendNotification(RemoteMessage stickerMessage) {
        Log.v(TAG, "Creating notification: " + stickerMessage.getNotification());
        int prevNotificationNum = checkAllActiveNotifications();
        String channelID = getResources().getString(R.string.channel_id);
        UNIQUE_INTENT_ID = (int) System.currentTimeMillis();

        Intent receivedMessagesIntent = new Intent(this, DisplayMessagesReceivedActivity.class);
        receivedMessagesIntent.putExtra("username", stickerMessage.getData().get("recipientUsername"));
        receivedMessagesIntent.putExtra("loginTime", stickerMessage.getData().get("loginTime"));

        PendingIntent openMessageHistory = PendingIntent.getActivity(this,
                UNIQUE_INTENT_ID,
                receivedMessagesIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Intent viewMessage = new Intent(this, ShowSelectedMessageActivity.class);
        viewMessage.putExtra("sender", stickerMessage.getData().get("currentUsername"));
        viewMessage.putExtra("recipient", stickerMessage.getData().get("recipientUsername"));
        viewMessage.putExtra("stickerLocation", stickerMessage.getData().get("stickerLocation"));
        viewMessage.putExtra("timeSent", stickerMessage.getData().get("timeSent"));

        PendingIntent pendingViewMessage = PendingIntent.getActivity(this,
                UNIQUE_INTENT_ID,
                viewMessage,
                PendingIntent.FLAG_ONE_SHOT);

        Intent respondToMessage = new Intent(this, SendMessageActivity.class);
        respondToMessage.putExtra("username", stickerMessage.getData().get("recipientUsername"));
        respondToMessage.putExtra("loginTime", stickerMessage.getData().get("loginTime"));
        respondToMessage.putExtra("recipient", stickerMessage.getData().get("currentUsername"));
        respondToMessage.putParcelableArrayListExtra("stickerList", stickerList);

        PendingIntent pendingRespondToMessage = PendingIntent.getActivity(this,
                UNIQUE_INTENT_ID,
                respondToMessage,
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
                        .addAction(R.drawable.a7_home_icon_round, "VIEW", pendingViewMessage)
                        .addAction(R.drawable.a7_home_icon_round, "REPLY",
                                pendingRespondToMessage)
                        .setAutoCancel(true)
                        .setContentIntent(openMessageHistory)
                        .setPriority(stickerMessage.getPriority())
                        .setSmallIcon(R.mipmap.a7_home_icon_round)
                        .setLargeIcon(stickerBitmap);

        if (prevNotificationNum == 0) {
            notificationBuilder.setContentText(stickerMessage.getNotification().getBody());

        } else {
            notificationBuilder.setContentText(
                    String.format(getString(R.string.notification_body),
                            prevNotificationNum + 1));
            if (prevNotificationNum > 2) {
                notificationBuilder.setSilent(true);
            }
        }

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
