package com.indexyear.jd.dispatch.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import android.os.Process;

import com.indexyear.jd.dispatch.models.Database;

public class DatabaseService extends Service {

    // TODO: 11/17/17 JD can i remove this

    private final String TEAM_ORANGE= "team-orange-20666/";

    private Database crisisDatabase;
    private Looper mServiceLooper;
    private DatabaseHandler mDatabaseHandler;

    public DatabaseService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mDatabaseHandler = new DatabaseHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // Do I need this message? No, but I do need to start the mDatabaseHandler...
        Message msg = mDatabaseHandler.obtainMessage();
        msg.arg1 = startId;
        mDatabaseHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    // code to handle incoming intents
    // based on the intent (crisis vs. message vs. other) this will handle different things.

    // get a database ref for the desired table (crisis/message/employee) only after the intent
    // is handled
    // when the listener on that database triggers with a success, pass that as a message to a
    // Crisis Update Service (IntentService), which then creates a notification that leads back
    // to the Main Activity

    // Constant START_STICKY is important in onStartCommand

    // Helper class - Handler that receives messages from the thread
    private final class DatabaseHandler extends Handler {
        public DatabaseHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            crisisDatabase = new Database(TEAM_ORANGE, "crisis/");

            // This is where we create the value event listener
            // and on success we take action, creating sending intent or message?


            // When do I want to stop the service?
            // stopSelf(msg.arg1);
        }
    }
}
