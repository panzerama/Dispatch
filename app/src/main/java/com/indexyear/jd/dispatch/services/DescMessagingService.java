package com.indexyear.jd.dispatch.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.activities.CrisisReceived;

import java.util.HashMap;
import java.util.Map;

public class DescMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "DescMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Map<String, String> messageDataMap = new HashMap<>();

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());



            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.d(TAG, "key, " + key + " value " + value);
                messageDataMap.put(key, value);
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        sendNotification("Some notification text", messageDataMap);
    }

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    public void sendNotification(String messageBody, Map<String, String> messageDataSet) {
        Log.d(TAG, " sendNotification");
        Intent intent = new Intent(this, CrisisReceived.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("source", "crisis message");

        if (!messageDataSet.isEmpty()){
            intent.putExtra("crisis_address", messageDataSet.get("crisis_address"));
            intent.putExtra("crisis_timestamp", messageDataSet.get("crisis_timestamp"));
        }

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_menu_camera)
                        .setContentTitle("FCM Message")
                        .setContentText(messageBody)
                        .setSound(defaultSoundUri)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(3739 /* ID of notification */, notificationBuilder.build());
    }
}
