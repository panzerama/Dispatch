package com.indexyear.jd.dispatch.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.data.crisis.CrisisManager;
import com.indexyear.jd.dispatch.data.crisis.CrisisParcel;
import com.indexyear.jd.dispatch.models.Crisis;
import com.indexyear.jd.dispatch.models.Team;

public class DispatchTeamActivity extends AppCompatActivity {

    private static final String TAG = "DispatchActivity";

    private ListView listOfTeams;
    private FirebaseListAdapter<Team> adapter;
    private Context context;
    private String selectedTeam;
    private Crisis inputCrisisObject;
    private CrisisManager mCrisisManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_team);

        context = getApplicationContext();
        mCrisisManager = new CrisisManager();

        //Create App Bar - Enable Back Notification
        // toolbar_messenger is defined in the activity messenger layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.toolbar_dispatch);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //TO-DO Use this address to calculate travel times
        //and update travel time node on Team object to reflect
        //those values
        Intent intent = getIntent();
        CrisisParcel incomingCrisisParcel = intent.getParcelableExtra("crisis");
        inputCrisisObject = incomingCrisisParcel.getCrisis();

        createTeamList();
    }

    private void createTeamList() {

        listOfTeams = (ListView)findViewById(R.id.mct_dispatch_list);

        adapter = new FirebaseListAdapter<Team>(this, Team.class,
                R.layout.message_list_item, FirebaseDatabase.getInstance().getReference().child("teams")) {
            @Override
            protected void populateView(View v, Team model, int position) {
                TextView teamName = (TextView)v.findViewById(R.id.title_mct_name);
                teamName.setText(model.getTeamName());
            }
        };

        listOfTeams.setAdapter(adapter);

        listOfTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Team team = (Team)parent.getItemAtPosition(position);

                selectedTeam = team.getTeamName();
                createConfirmDispatchDialog();
            }
        });
    }

    private void createConfirmDispatchDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dispatch " + selectedTeam + "?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                triggerNotification(inputCrisisObject, selectedTeam);
                Toast.makeText(context, "Dispatched"+ selectedTeam,
                        Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void triggerNotification(Crisis inputCrisis, String selectedTeam){
        inputCrisis.setTeamName(selectedTeam);
        inputCrisis.setStatus("open");
        mCrisisManager.updateCrisisInDatabase(inputCrisis);
    }

}
