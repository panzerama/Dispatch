package com.indexyear.jd.dispatch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.data.team.TeamManager;
import com.indexyear.jd.dispatch.data.user.IUserEventListener;
import com.indexyear.jd.dispatch.data.user.UserManager;
import com.indexyear.jd.dispatch.data.user.UserParcel;
import com.indexyear.jd.dispatch.models.Team;
import com.indexyear.jd.dispatch.models.User;

import java.util.ArrayList;
import java.util.List;


public class ShiftStartActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = "ShiftStartActivity: ";

    private FirebaseAuth mAuth;
    private FirebaseAnalytics mAnalyticsInstance; // TODO: 11/18/17 JD implement long-term logging
    private DatabaseReference mDB;

    private UserManager mUserManager;
    private User mUser;
    public User foundUser; // TODO: 11/18/17 JD find where this is used
    public Team mTeam;
    private TeamManager mTeamManager;
    private IUserEventListener mUserEventListener;
    private UserParcel mUserParcel;

    Spinner role_spinner;
    Spinner team_spinner;
    Spinner status_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.shift_start_button).setOnClickListener(this);

        role_spinner = (Spinner) findViewById(R.id.role_spinner);
        role_spinner.setOnItemSelectedListener(this);
        team_spinner = (Spinner) findViewById(R.id.team_spinner);
        status_spinner = (Spinner) findViewById(R.id.status_spinner);

        mAuth = FirebaseAuth.getInstance();
        mTeamManager = new TeamManager();
        mDB = FirebaseDatabase.getInstance().getReference("");

        setUserListener();
    }

    //when the button is clicked, take values from spinners
    @Override
    public void onClick(View v) {
        int i = v.getId();

        Log.d(TAG, " onclick");

        final Intent ShiftStartHandoff = new Intent(this, MainActivity.class);
        String team = "none";
        String status = "none";

        String role = role_spinner.getSelectedItem().toString();

        if (role.equals("MCT")) {
            team = team_spinner.getSelectedItem().toString();
            status = status_spinner.getSelectedItem().toString();
            Log.d(TAG, " onClick team = " + team + " status = " + status);
            updateEmployeeAsMCT(role, team, status);
        } else if (role.equals("Dispatcher")) {
            updateEmployeeAsDispatcher(role);
        }

        //putting the User(userID, role) as an extra to send with the intent.
        mUserParcel = new UserParcel(mUser);
        ShiftStartHandoff.putExtra("user", mUserParcel);
        startActivity(ShiftStartHandoff);
    }

    // JDP The spinner for team and status must depend on role. My attempt doesn't function properly
    public void onItemSelected(AdapterView adapterView, View view, int pos, long id) {
        if (role_spinner.getSelectedItem().toString().equals("MCT")) {
            // make other spinners visible
            createTeamSpinner();
            createStatusSpinner();
            team_spinner.setVisibility(View.VISIBLE);
            status_spinner.setVisibility(View.VISIBLE);
        } else if (role_spinner.getSelectedItem().toString().equals("Dispatch")) {
            team_spinner.setVisibility(View.INVISIBLE);
            status_spinner.setVisibility(View.INVISIBLE);
        }
    }

    public void onNothingSelected(AdapterView adapterView) {
        // do nothing?
    }

    //
    private void updateEmployeeAsMCT(String role, String team, String status) {
        String uid = mAuth.getCurrentUser().getUid();
        Log.d(TAG, " updateEmployeeAsMCT uid = " + uid);
        Log.d(TAG, " updateEmployeeAsMCT role = " + role);
        Log.d(TAG, " updateEmployeeAsMCT team = " + team);
        Log.d(TAG, " updateEmployeeAsMCT status = " + status);
        mUserManager.setUserRole(uid, role);
        mUser.setCurrentRole(role);

        mUserManager.setUserTeam(uid, team);
        mUser.setCurrentTeam(team);

        mUserManager.setUserStatus(uid, status);
        mUser.setCurrentStatus(status);

        // this is where the token work needs doing
        String token = FirebaseInstanceId.getInstance().getToken();
        mUserManager.setUserNotificationToken(uid, token);

        //
        mTeamManager.addEmployeeAndToken(team, uid, token);
    }

    private void updateEmployeeAsDispatcher(String role) {
        Log.d(TAG, "updateEmployeeAsDispatcher");
        String uid = mAuth.getCurrentUser().getUid();

        mUserManager.setUserRole(uid, role);
        mUser.setCurrentRole("Dispatcher");
    }

    private void createTeamSpinner() {
        List<String> teams = new ArrayList<>();
        for (Team t : mTeamManager.getCurrentTeamsList()) {
            teams.add(t.getTeamName());
        }
        ArrayAdapter<String> teamsAdapter = new ArrayAdapter<String>(ShiftStartActivity.this, android.R.layout.simple_spinner_item, teams);
        teamsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        team_spinner.setAdapter(teamsAdapter);
    }

    private void createStatusSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.status_spinner_items));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(adapter);
        status_spinner.setSelection(0);
    }

    private void setUserListener(){
        mUserManager = new UserManager();
        mUserEventListener = new IUserEventListener() {
            @Override
            public void onUserCreated(User newUser) {
                if (mUser == null) { mUser = newUser; Log.d(TAG, " onusercreated fired");}
            }

            @Override
            public void onUserRemoved(User removedUser) {
                //do nothing for now
            }

            @Override
            public void onUserUpdated(User updatedUser) {
                if (mUser == null) { mUser = updatedUser; }
                else if (updatedUser.getUserID().equals(mUser.getUserID())){
                    mUser.updateUser(updatedUser);
                }
            }
        };

        mUserManager.addNewListener(mUserEventListener);
    }

}
