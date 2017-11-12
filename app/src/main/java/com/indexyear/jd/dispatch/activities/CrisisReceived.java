package com.indexyear.jd.dispatch.activities;

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

public class CrisisReceived extends AppCompatActivity implements View.OnClickListener{

    final String TAG = "CrisisReceived";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crisis_received);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView crisisAddressTextView = (TextView) findViewById(R.id.crisisAddress);
        TextView crisisTimeTextView = (TextView) findViewById(R.id.crisisTimestamp);
        Button acceptCrisisButton = (Button) findViewById(R.id.acceptDispatchButton);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String crisis_address = getIntent().getStringExtra("crisis_address");
        String crisis_timestamp = getIntent().getStringExtra("crisis_timestamp");

        Log.d(TAG, " receved: " + crisis_address + " " + crisis_timestamp);
        crisisAddressTextView.setText(crisis_address);
        crisisAddressTextView.setText(crisis_address);

        acceptCrisisButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "crisis accept button");
    }

}
