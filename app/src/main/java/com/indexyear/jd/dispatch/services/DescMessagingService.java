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
import com.indexyear.jd.dispatch.data.crisis.CrisisManager;
import com.indexyear.jd.dispatch.data.crisis.CrisisParcel;
import com.indexyear.jd.dispatch.data.crisis.ICrisisEventListener;
import com.indexyear.jd.dispatch.models.Crisis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DescMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "DescMessagingService";
    private ICrisisEventListener mCrisisListener;
    private CrisisManager mCrisisManager;
    private String crisisID;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        mCrisisListener = new ICrisisEventListener() {
            @Override
            public void onCrisisCreated(Crisis newCrisis) {
                Log.d(TAG, "oncrisiscreated crisis_id " + remoteMessage.getData().get("crisis_id") + " newcrisis crisis_id " + newCrisis.getCrisisID());
                if (remoteMessage.getData().get("crisis_id").equals(newCrisis.getCrisisID())){
                    sendNotification(newCrisis);
                }
            }

            @Override
            public void onCrisisRemoved(Crisis removedCrisis) {

            }

            @Override
            public void onCrisisUpdated(Crisis updatedCrisis) {
                // TODO: 11/17/17 JD is there a case where we would use this?
            }

        };

        mCrisisManager = new CrisisManager();
        mCrisisManager.setCrisisEventListener(mCrisisListener);

        // the data being received should at the very least be the crisisID
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Map<String, String> messageDataMap = new HashMap<>();

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.d(TAG, "key: " + key + " value: " + value);
                messageDataMap.put(key, value);
            }
            //sendNotification("Some notification text", messageDataMap);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
        }

        if (messageDataMap.containsKey("crisis_id")){
            crisisID = messageDataMap.get("crisis_id");
        } else {
            throw new IllegalArgumentException("The crisis ID was not passed.");
        }
    }

    /**
     * On the onCrisisCreated event, this will fire a notification.
     *
     * @param incomingCrisis passed in from the data snapshot
     *
     */
    public void sendNotification(Crisis incomingCrisis) {
        Log.d(TAG, " sendNotification");
        Intent intent = new Intent(this, CrisisReceived.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // TODO: 11/17/17 JD what I want to do here is to get the crisis by address from the DB

        CrisisParcel crisisParcel = new CrisisParcel(incomingCrisis);

        Log.d(TAG, "message data: " + incomingCrisis.getCrisisAddress());

        intent.putExtra("crisis", crisisParcel);

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
                        .setContentTitle("Incoming Crisis!")
                        .setContentText("A crisis has been added for " + incomingCrisis.getCrisisAddress())
                        .setSound(defaultSoundUri)
                        .setAutoCancel(true)
                        //.setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // temporary solve for unique notification ID
        int randomUUID = UUID.randomUUID().hashCode();

        notificationManager.notify(randomUUID, notificationBuilder.build());
    }
}
