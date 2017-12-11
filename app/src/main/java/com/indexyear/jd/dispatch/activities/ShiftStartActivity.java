package com.indexyear.jd.dispatch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.data.team.IGetTeamsListener;
import com.indexyear.jd.dispatch.data.team.TeamManager;
import com.indexyear.jd.dispatch.data.user.IGetUserListener;
import com.indexyear.jd.dispatch.data.user.UserManager;
import com.indexyear.jd.dispatch.models.Team;
import com.indexyear.jd.dispatch.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ShiftStartActivity extends AppCompatActivity {

    private static final String TAG = "ShiftStartActivity: ";
    private Context context;

    //FOR UPDATING USER SHIFT START VALUES
    private FirebaseAuth mAuth;
    private UserManager mUserManager;
    private User mUser;
    private String selectedTeam;
    private String userRole;
    private String userID;
    private String token;

    //FOR TEAM SPINNER
    Spinner team_spinner;
    private TeamManager mTeamManager;
    List<Team> theTeams;
    ArrayAdapter<String> teamSpinnerAdapter;
    ArrayList<String> teamNames;
    ArrayList<String> teamIDS;

    //FOR ROLE SPINNER
    Spinner role_spinner;

    //TODO 12/5/17 KB can be removed
//    private FusedLocationProviderClient mFusedLocationClient;
//    Spinner status_spinner;
//    private DatabaseReference mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // LUKE - 12/9/17 - lock orientation to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_shift_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();

        //FOR USER OBJECT
        getUserObject();

        //FOR ROLE SPINNER
        role_spinner = (Spinner) findViewById(R.id.role_spinner);
        createRoleSpinner();

        //TODO KB 12/5/17 can be removed
        //mDB = FirebaseDatabase.getInstance().getReference();
//        status_spinner = (Spinner) findViewById(R.id.status_spinner);
    }

    private void getUserObject(){
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        token = FirebaseInstanceId.getInstance().getToken();
        mUserManager = new UserManager();
        mUserManager.getUser(userID, new IGetUserListener() {
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

    private void getTeamObjects(){
        mTeamManager = new TeamManager();
        mTeamManager.getTeams(new IGetTeamsListener() {
            @Override
            public void onGetTeams(List<Team> retrievedTeams) {
                teamNames = new ArrayList<>();
                teamIDS = new ArrayList<>();
                team_spinner = (Spinner) findViewById(R.id.team_spinner);
                theTeams = retrievedTeams;
                createTeamSpinner();
            }

            @Override
            public void onFailedTeams() {
                Toast.makeText(context, "Unable to retrieve teams.",Toast.LENGTH_LONG).show();
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
                userRole = role_spinner.getSelectedItem().toString();
                if (userRole.equalsIgnoreCase("Mobile Crisis Team")) {
                    getTeamObjects();
                    //TODO 12/5/17 KB can be removed
//                    createStatusSpinner();
//                    status_spinner.setVisibility(View.VISIBLE);
                }
                //TODO 12/5/17 KB can be removed
//                else if (userRole.equals("Dispatcher")) {
                    //team_spinner.setVisibility(View.INVISIBLE);
//                    status_spinner.setVisibility(View.INVISIBLE);
//                }
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

                mUser = setUserShiftStartValues(mUser);
                Intent shiftStartHandoff = new Intent(context, MainActivity.class);
                shiftStartHandoff.putExtra("user", mUser);
                shiftStartHandoff.putExtra("intent_purpose", "passing_user");

                startActivity(shiftStartHandoff);
                //TODO 12/5/17 KB can be removed
//                String team = "none";
//                String status = "none";

//                String role = role_spinner.getSelectedItem().toString();
//
//                if (role.equals("MCT"))
//                {
//                    status = status_spinner.getSelectedItem().toString();
//                    updateEmployeeAsMCT(role, selectedTeam, status);
//                } else if (role.equals("Dispatcher"))
//
//                {
//                    updateEmployeeAsDispatcher(role);
//                }

                //putting the User(userID, role) as an extra to send with the intent.

            }
        });
    }

    /**
     * Sets user's role, team, and messaging token
     * @param user
     */
    private User setUserShiftStartValues(User user) {

        //FOR DB UPDATE
        Map<String, Object> userMap = user.toMap();
        userMap.put("currentRole", userRole);
        userMap.put("token", token);

        //SHALLOW COPY
        user.setCurrentRole(userRole);
        user.setToken(token);

        if (userRole.equalsIgnoreCase("Mobile Crisis Team")) {
            userMap.put("currentTeam", selectedTeam);
            user.setCurrentTeam(selectedTeam);
        } else {
            userMap.put("currentTeam", "NONE");
            user.setCurrentTeam("NONE");
        }

        mUserManager.updateUser(userMap);

        return user;
    }
        //TODO 12/5/17 KB can be removed
//        mUser.setCurrentRole(role);
//        mUser.setCurrentTeam(team);

//        mUser.setToken(token);
//        mUser.setLatitude(0);
//        mUser.setLongitude(0);
//        mUserManager.addOrUpdateNewEmployee(mUser);
//    }
//    private void updateEmployeeAsDispatcher(String role) {
//        Log.d(TAG, "updateEmployeeAsDispatcher");
//
//
//        mUserManager.setUserRole(uid, role);
//        mUser.setCurrentRole("Dispatcher");
//    }

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

        team_spinner.setVisibility(View.VISIBLE);

    }

    //TODO 12/5/17 KB can be removed
//    private void createStatusSpinner() {
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.status_spinner_items));
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        status_spinner.setAdapter(adapter);
//        status_spinner.setSelection(0);
//    }

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


