package com.indexyear.jd.dispatch.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.models.Crisis;

public class CrisisReceived extends AppCompatActivity implements View.OnClickListener{

    final String TAG = "CrisisReceived";
    String crisisAddress, crisisId;
    Crisis crisisWaitingResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // LUKE - 12/9/17 - lock orientation to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_crisis_received);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        crisisWaitingResponse = getIntent().getParcelableExtra("crisis");


        TextView crisisAddressTextView = (TextView) findViewById(R.id.crisisAddress);
        TextView crisisTimeTextView = (TextView) findViewById(R.id.crisisTimestamp);

        Log.d(TAG, " received: " + crisisWaitingResponse.getCrisisID() + " " + crisisWaitingResponse.getCrisisAddress());

        crisisAddressTextView.setText(crisisWaitingResponse.getCrisisAddress());
        crisisTimeTextView.setText(crisisWaitingResponse.getCrisisID());

        Button acceptCrisisButton = (Button) findViewById(R.id.acceptDispatchButton);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        acceptCrisisButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "crisis accept button");

        Intent crisisPinIntent = new Intent(this, MainActivity.class);
        crisisPinIntent.putExtra("crisis", crisisWaitingResponse);
        crisisPinIntent.putExtra("intent_purpose", "crisis_map_update");

        startActivity(crisisPinIntent);
    }

}
