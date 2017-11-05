package com.indexyear.jd.dispatch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indexyear.jd.dispatch.R;

public class ShiftStartActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ShiftStartActivity: ";

    private FirebaseAuth mAuth;

    private FirebaseAnalytics mAnalyticsInstance;

    private DatabaseReference mDB;

    Spinner role_spinner;
    Spinner team_spinner;
    Spinner status_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get spinner values?
        //set up button
        findViewById(R.id.shift_start_button).setOnClickListener(this);

        role_spinner = (Spinner) findViewById(R.id.role_spinner);
        team_spinner = (Spinner) findViewById(R.id.team_spinner);
        status_spinner = (Spinner) findViewById(R.id.status_spinner);

        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //when the button is clicked, take values from spinners
    @Override
    public void onClick(View v){
        int i = v.getId();

        Log.d(TAG, " onclick");

        final Intent ShiftStartHandoff = new Intent(this, MainActivity.class);

        if (i== R.id.shift_start_button){ //do I need a test if there's only one button?
            //update employee status
            Log.d(TAG, "shift start button");
            String role = role_spinner.getSelectedItem().toString();
            String team = team_spinner.getSelectedItem().toString();
            String status = status_spinner.getSelectedItem().toString();

            Log.d(TAG, "role is " + role +", team is " + team + ", and status is " + status);

            if (role.equals("MCT")) { //&& !team.isEmpty() && !status.isEmpty()
                updateEmployeeAsMCT(role, team, status);
            } else if (role.equals("Dispatcher")){
                updateEmployeeAsDispatch(role);
            } else {
                //do something to raise error
            }

            startActivity(ShiftStartHandoff);
        }

    }

    //update the employee status with relevant values
    private void updateEmployeeAsMCT(String role, String team, String status){
        Log.d(TAG, "updateEmployeeAsMCT");
        mDB = FirebaseDatabase.getInstance().getReference("employees/");

        mDB.child(mAuth.getUid()).child("role").setValue(role);
        mDB.child(mAuth.getUid()).child("team").setValue(team);
        mDB.child(mAuth.getUid()).child("status").setValue(status);
    }

    private void updateEmployeeAsDispatch(String role){
        Log.d(TAG, "updateEmployeeAsDispatch");
        mDB = FirebaseDatabase.getInstance().getReference("employees/");

        mDB.child(mAuth.getUid()).child("role").setValue(role);
    }
    //pass to main activity

}
