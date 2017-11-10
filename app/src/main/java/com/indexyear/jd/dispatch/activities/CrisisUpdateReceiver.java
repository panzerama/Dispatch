package com.indexyear.jd.dispatch.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class CrisisUpdateReceiver extends BroadcastReceiver {

    final String TAG = "CrisisUpdateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();

        Log.d(TAG, "Alert Message should fire here!");
    }
}
