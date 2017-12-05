package com.indexyear.jd.dispatch.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.data.team.TeamManager;
import com.indexyear.jd.dispatch.data.user.IGetUserListener;
import com.indexyear.jd.dispatch.data.user.UserManager;
import com.indexyear.jd.dispatch.models.Team;
import com.indexyear.jd.dispatch.models.User;

import java.util.ArrayList;
import java.util.List;


public class ShiftStartActivity extends AppCompatActivity {

    private static final String TAG = "ShiftStartActivity: ";

    private FirebaseAuth mAuth;
    private DatabaseReference mDB;

    private UserManager mUserManager;
    private TeamManager mTeamManager;
    public User mUser;
    public String selectedTeam;

    Spinner role_spinner;
    Spinner team_spinner;
    Spinner status_spinner;

    List<Team> theTeams;
    ArrayAdapter<String> teamSpinnerAdapter;
    ArrayList<String> teamNames;
    ArrayList<String> teamIDS;
    Context context;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();

        mAuth = FirebaseAuth.getInstance();
        mTeamManager = new TeamManager();
        teamNames = new ArrayList<>();

        mDB = FirebaseDatabase.getInstance().getReference("");
        teamIDS = new ArrayList<>();

        role_spinner = (Spinner) findViewById(R.id.role_spinner);
        createRoleSpinner();
        team_spinner = (Spinner) findViewById(R.id.team_spinner);
        status_spinner = (Spinner) findViewById(R.id.status_spinner);

        // 11/28/17 JD: jdp this makes sure that we have the user set before we proceed
        mUserManager = new UserManager();
        mUserManager.getUser(mAuth.getCurrentUser().getUid(), new IGetUserListener() {
            @Override
            public void onGetSingleUser(User retrievedUser) {
                mUser = retrievedUser;
                // 11/28/17 JD: then instantiate the UI so that we can't move forward until a user value is found
                createStartShiftButton();
            }

            @Override
            public void onFailedSingleUser() {
                // 11/28/17 JD: do something to avoid catastrophic failure

            }
        });
    }

    private void createRoleSpinner() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role_spinner_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role_spinner.setAdapter(adapter);
        role_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (role_spinner.getSelectedItem().toString().equals("MCT")) {
                    // make other spinners visible
                    theTeams = mTeamManager.getCurrentTeamsList();
                    createStatusSpinner();
                    createTeamSpinner();
                    team_spinner.setVisibility(View.VISIBLE);
                    status_spinner.setVisibility(View.VISIBLE);
                } else if (role_spinner.getSelectedItem().toString().equals("Dispatch")) {
                    team_spinner.setVisibility(View.INVISIBLE);
                    status_spinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void createStartShiftButton() {
        Button startShift = (Button) findViewById(R.id.shift_start_button);
        startShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent shiftStartHandoff = new Intent(context, MainActivity.class);
                String team = "none";
                String status = "none";

                String role = role_spinner.getSelectedItem().toString();

                if (role.equals("MCT"))
                {
                    status = status_spinner.getSelectedItem().toString();
                    updateEmployeeAsMCT(role, selectedTeam, status);
                } else if (role.equals("Dispatcher"))

                {
                    updateEmployeeAsDispatcher(role);
                }

                //putting the User(userID, role) as an extra to send with the intent.
                shiftStartHandoff.putExtra("user", mUser);
                shiftStartHandoff.putExtra("intent_purpose", "passing_user");

                startActivity(shiftStartHandoff);
            }
        });
    }

    private void updateEmployeeAsMCT(String role, String team, String status) {
        mUser = mUserManager.getCurrentUser();
        mUser.setCurrentRole(role);
        mUser.setCurrentTeam(team);
        mUser.setCurrentStatus(status);
        String token = FirebaseInstanceId.getInstance().getToken();
        mUser.setToken(token);
        mUser.setLatitude(0);
        mUser.setLongitude(0);
        mUserManager.addOrUpdateNewEmployee(mUser);
    }

    private void updateEmployeeAsDispatcher(String role) {
        Log.d(TAG, "updateEmployeeAsDispatcher");
        String uid = mAuth.getCurrentUser().getUid();

        mUserManager.setUserRole(uid, role);
        mUser.setCurrentRole("Dispatcher");
    }

    private void createTeamSpinner() {


        for (int i = 0; i < theTeams.size(); i++) {
            teamNames.add(theTeams.get(i).getTeamName().toString());
            teamIDS.add(theTeams.get(i).getTeamID().toString());
        }

        teamSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, teamNames);
        teamSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        team_spinner.setAdapter(teamSpinnerAdapter);

        team_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedPos = parent.getSelectedItemPosition();
                selectedTeam = teamIDS.get(selectedPos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void createStatusSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.status_spinner_items));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(adapter);
        status_spinner.setSelection(0);
    }

//    private void updateUserLocation(Location location) {
//        Log.d(TAG, "Update user location with " + location.getLatitude() + " and " + location.getLongitude());
//        mUserManager.setUserLocation(mAuth.getCurrentUser().getUid(), location);
//        mUser.setLatitude((float) location.getLatitude());
//        mUser.setLatitude((float) location.getLongitude());
//    }

//    private void setMUserValue(User retrievedUser) {
//        mUser = retrievedUser;
//    }

}


