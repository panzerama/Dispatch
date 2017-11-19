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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.data.user.IUserEventListener;
import com.indexyear.jd.dispatch.data.team.TeamManager;
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
    private FirebaseAnalytics mAnalyticsInstance;
    private FirebaseDatabase mDBInstance;
    private DatabaseReference mDB;

    private UserManager mUserManager;
    private User mUser;
    public User foundUser;
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
        // jdp not sure how to set default values, may need to define arrayadapter programmatically
        // instead of in string values resource

        team_spinner = (Spinner) findViewById(R.id.team_spinner);
        status_spinner = (Spinner) findViewById(R.id.status_spinner);

        mAuth = FirebaseAuth.getInstance();
        mUserManager = new UserManager();
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

        if (i == R.id.shift_start_button) { //do I need a test if there's only one button?
            //update employee status
            String role = role_spinner.getSelectedItem().toString();

            // null condition check needed, if case is dispatch

            String team = team_spinner.getSelectedItem().toString();
            String status = status_spinner.getSelectedItem().toString();

            if (role.equals("Team")) { //&& !team.isEmpty() && !status.isEmpty()
                updateEmployeeAsMCT(role, team, status);
            } else if (role.equals("Dispatcher")) {
                updateEmployeeAsDispatch(role);
            } else {
                //do something to raise error
            }

            //putting the User(userID, role) as an extra to send with the intent.
            mUserParcel = new UserParcel(mUser);
            ShiftStartHandoff.putExtra("user", mUserParcel);
            startActivity(ShiftStartHandoff);
        }

    }

    // JDP The spinner for team and status must depend on role. My attempt doesn't function properly
    public void onItemSelected(AdapterView adapterView, View view, int pos, long id) {
        if (role_spinner.getSelectedItem().toString().equals("Team")) {
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
        mUserManager.setUserRole(uid, role);
        mUserManager.setUserTeam(uid, team);
        mUserManager.setUserStatus(uid, status);

        // this is where the token work needs doing
        String token = FirebaseInstanceId.getInstance().getToken();
        mUserManager.setUserNotificationToken(uid, token);

        //
        mTeamManager.addEmployeeAndToken(team, uid, token);

        mUser = new User(uid, role);
    }

/*    previous version - JDP
    private void updateEmployeeAsMCT() {

        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("employees").child(uid);
        dbRef.child("currentRole").setValue(MCTMEMBER);
        dbRef.child("currentStatus").setValue(getSpinnerValueAsEnum(status_spinner.getSelectedItem().toString()));
        dbRef.child("currentTeam").setValue(team_spinner.getSelectedItem().toString());
        GetEmployeeObject(uid);
        final String[] teamName = {team_spinner.getSelectedItem().toString()};
        boolean success = addUserToTeam(teamName);

        if(!success){
            Toast toast = Toast.makeText(this, "Error occurred.", Toast.LENGTH_SHORT);
            toast.show();
        }

        //this is creating the employee object that the intent is going to pass around, hopefully.
        //role is here because this was not accessing the database for me in testing, --Luke
        String role = "Team";
        mUser = new User(uid, role);
    }*/

    private void updateEmployeeAsDispatch(String role) {
        Log.d(TAG, "updateEmployeeAsDispatch");
        String uid = mAuth.getCurrentUser().getUid();

        mUserManager.setUserRole(uid, role);
    }

    private void createTeamSpinner() {
        mDB.child("teams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> teams = new ArrayList<String>();

                for (DataSnapshot teamSnapShot : dataSnapshot.getChildren()) {
                    String teamName = teamSnapShot.child("teamName").getValue(String.class);
                    teams.add(teamName);
                }
                ArrayAdapter<String> teamsAdapter = new ArrayAdapter<String>(ShiftStartActivity.this, android.R.layout.simple_spinner_item, teams);
                teamsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                team_spinner.setAdapter(teamsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createStatusSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.status_spinner_items));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(adapter);
        status_spinner.setSelection(0);
    }

    public boolean addUserToTeam(final String[] teamName){
        final boolean[] operationSuccessful = {false};
        try {
            final DatabaseReference dbRef = mDB.child("teams").getRef();
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot teamSnapshot : dataSnapshot.getChildren()){
                        if(teamSnapshot.getKey().toString().equals((teamName[0].replaceAll("\\s+","")))){
                            final Team team = teamSnapshot.getValue(Team.class);
                            List<User> teamMembers;
                            if(team.teamMembers != null){
                                teamMembers = team.teamMembers;
                            } else {
                                teamMembers = new ArrayList<User>();
                            }
                            if(foundUser != null){
                                teamMembers.add(foundUser);
                                dbRef.child((teamName[0].replaceAll("\\s+",""))).child("teamMembers").setValue(teamMembers);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            operationSuccessful[0] = false;
            Log.d(TAG, e.toString());
        }

        return operationSuccessful[0];

    }

    public void GetEmployeeObject(String uid){
        FirebaseDatabase.getInstance().getReference("employees").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                foundUser = dataSnapshot.getValue(User.class);
                Log.d(TAG, foundUser.currentTeam);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUserListener(){
        mUserEventListener = new IUserEventListener() {
            @Override
            public void onUserCreated(User newUser) {
                if (mUser == null) { mUser = newUser; }
                else if (newUser.getUserID().equals(mUser.getUserID())){
                    mUser.updateUser(newUser);
                }
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
