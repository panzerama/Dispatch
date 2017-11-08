package com.indexyear.jd.dispatch.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * An {@link IntentService} subclass for handling updates to the Crisis node in the Firebase
 * Realtime database.
 * <p>
 * Realtime Database provides a listener function that will return a datasnapshot every time
 * the crisis node is updated in any fashion. this service provides a thread for that process
 * independent of the main activity.
 * @author JD Panzer
 */
public class CrisisIntentService extends IntentService {
    // Core function - connect to database and monitor
    private static final String ACTION_DATABASE_CONNECT = "com.indexyear.jd.dispatch.services.action.DATABASE_CONNECT";
    // will the intent ever be required to update the crisis table? Why not?
    private static final String ACTION_DATABASE_UPDATE = "com.indexyear.jd.dispatch.services.action.DATABASE_UPDATE";

    // Database criteria. I could generalize to any database monitoring, couldn't I?
    private static final String DATABASE_URI = "com.indexyear.jd.dispatch.services.extra.DATABASE_URI";
    private static final String DATABASE_NODE = "com.indexyear.jd.dispatch.services.extra.DATABASE_NODE";

    public CrisisIntentService() {
        super("CrisisIntentService");
    }

    /**
     * Starts this service to connect the database with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionDatabaseConnect(Context context, String databaseUri, String databaseNode) {
        Intent intent = new Intent(context, CrisisIntentService.class);
        intent.setAction(ACTION_DATABASE_CONNECT);
        intent.putExtra(DATABASE_URI, databaseUri);
        intent.putExtra(DATABASE_NODE, databaseNode);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    /*public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, CrisisIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DATABASE_CONNECT.equals(action)) {
                final String param1 = intent.getStringExtra(DATABASE_URI);
                final String param2 = intent.getStringExtra(DATABASE_NODE);
                handleActionDatabaseConnect(param1, param2);
            } /*else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }*/
        }
    }

    /**
     * Connects to the database and establishes a listener for updates to informaion. Assumes
     * that parameters are valid strings
     */
    private void handleActionDatabaseConnect(String databaseUri, String nodeReference) {
        DatabaseReference mDB = FirebaseDatabase.getInstance().getReference(databaseUri + nodeReference);

        mDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Notify the activity of the new crisis. Do not put logic about teams here...?
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
